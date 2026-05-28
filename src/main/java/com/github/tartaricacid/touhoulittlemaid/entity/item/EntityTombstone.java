package com.github.tartaricacid.touhoulittlemaid.entity.item;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.github.tartaricacid.touhoulittlemaid.world.data.MaidWorldData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil.canItemInsert;

public class EntityTombstone extends Entity {
    public static final EntityType<EntityTombstone> TYPE = EntityType.Builder.<EntityTombstone>of(EntityTombstone::new, MobCategory.MISC)
            .sized(0.8f, 1.2f).clientTrackingRange(10)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "tombstone")));
    private static final String OWNER_ID_TAG = "OwnerId";
    private static final String TOMBSTONE_ITEMS_TAG = "TombstoneItems";
    private static final String MAID_NAME_TAG = "MaidName";
    private static final EntityDataAccessor<Component> MAID_NAME = SynchedEntityData.defineId(EntityTombstone.class, EntityDataSerializers.COMPONENT);
    // 考虑其他模组会添加额外的存储内容，加之饰品模组拓展了数量，故将墓碑存储上限修改为 256 组
    private final ItemStacksResourceHandler items = new ItemStacksResourceHandler(256);
    private UUID ownerId = Util.NIL_UUID;

    public EntityTombstone(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public EntityTombstone(Level worldIn, UUID ownerId, Vec3 pos) {
        this(TYPE, worldIn);
        this.ownerId = ownerId;
        this.setPos(pos);
    }

    public void insertItem(ItemStack item) {
        ItemsUtil.insertItemStacked(this.items, item, false, null);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 position) {
        ItemStack itemInHand = player.getItemInHand(hand);

        // 只能主手触发
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        // NTR 工具可以收回墓碑
        if (player.getUUID().equals(this.ownerId) || itemInHand.is(InitItems.OWNER_CONVERSION_TOOL.get())) {
            var stacks = this.items.copyToList();
            // 第一步：预检查所有物品是否能被玩家容纳（不实际提取物品）
            // 如果玩家按下了 Shift 键，则强制取出
            if (!player.isSecondaryUseActive()) {
                for (ItemStack stack : stacks) {
                    if (stack.isEmpty() || canItemInsert(player, stack)) {
                        continue;
                    }
                    // 一旦发现有物品不能插入，立即中断检查
                    if (!player.level.isClientSide()) {
                        player.sendSystemMessage(Component.translatable("message.touhou_little_maid.tombstone.player_inventory_full.1"));
                        player.sendSystemMessage(Component.translatable("message.touhou_little_maid.tombstone.player_inventory_full.2"));
                    }
                    return InteractionResult.FAIL;
                }
            }

            // 第二步：确认可以处理后，才实际提取并给予物品
            for (int i = 0; i < stacks.size(); i++) {
                int size = this.items.getCapacityAsInt(i, this.items.getResource(i));
                ItemStack extractItem = ItemsUtil.extractItem(this.items, i, size, false, null);
                if (!extractItem.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(extractItem);
                }
            }

            // 所有物品处理完毕后，再销毁实体
            this.discard();
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        // 其他逻辑...
        if (!player.level.isClientSide()) {
            Component displayName = InitItems.OWNER_CONVERSION_TOOL.toStack().getDisplayName();
            player.sendSystemMessage(Component.translatable("message.touhou_little_maid.tombstone.not_yours.1"));
            player.sendSystemMessage(Component.translatable("message.touhou_little_maid.tombstone.not_yours.2").append(displayName));
        }
        return super.interact(player, hand, position);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(MAID_NAME, Component.empty());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        input.read(OWNER_ID_TAG, UUIDUtil.CODEC).ifPresent(t -> this.ownerId = t);
        this.items.deserialize(input.childOrEmpty(TOMBSTONE_ITEMS_TAG));
        input.read(MAID_NAME_TAG, ComponentSerialization.CODEC).ifPresent(this::setMaidName);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store(OWNER_ID_TAG, UUIDUtil.CODEC, this.ownerId);
        this.items.serialize(output.child(TOMBSTONE_ITEMS_TAG));
        output.store(MAID_NAME_TAG, ComponentSerialization.CODEC, this.getMaidName());
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide()) {
            this.checkBelowWorld();
        }
    }

    @Override
    public boolean skipAttackInteraction(Entity pEntity) {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    public void push(double pX, double pY, double pZ) {
    }

    @Override
    protected boolean repositionEntityAfterLoad() {
        return false;
    }

    @Override
    public void thunderHit(ServerLevel pLevel, LightningBolt pLightning) {
    }

    @Override
    public void refreshDimensions() {
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy()) {
            MaidWorldData maidWorldData = MaidWorldData.get(level);
            if (maidWorldData != null) {
                maidWorldData.removeTombstones(this);
            }
        }
        super.remove(reason);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setMaidName(@Nullable Component name) {
        if (name != null) {
            this.entityData.set(MAID_NAME, name);
        }
    }

    public Component getMaidName() {
        return this.entityData.get(MAID_NAME);
    }

    public ItemStacksResourceHandler getItems() {
        return items;
    }
}
