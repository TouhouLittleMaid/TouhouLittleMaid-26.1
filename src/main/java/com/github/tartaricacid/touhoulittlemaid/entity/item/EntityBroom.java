package com.github.tartaricacid.touhoulittlemaid.entity.item;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IBroomControl;
import com.github.tartaricacid.touhoulittlemaid.entity.item.control.BroomControlManager;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.network.message.OpenPlayerInventoryPackage;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.github.tartaricacid.touhoulittlemaid.network.message.OpenPlayerInventoryPackage.OPEN_PLAYER_INVENTORY;
import static net.minecraft.network.syncher.EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE;

public class EntityBroom extends AbstractEntityFromItem implements OwnableEntity, HasCustomInventoryScreen {
    public static final Identifier ENTITY_ID = IdentifierUtil.modLoc("broom");
    public static final ResourceKey<EntityType<?>> ENTITY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ENTITY_ID);
    public static final EntityType<EntityBroom> TYPE = EntityType
            .Builder.<EntityBroom>of(EntityBroom::new, MobCategory.MISC)
            .sized(1.375F, 0.5625F)
            .clientTrackingRange(10)
            .ridingOffset(0)
            .build(ENTITY_KEY);

    // 扫帚会把放置出来的那个人标记为 OWNER
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER = SynchedEntityData.defineId(
            EntityBroom.class, OPTIONAL_LIVING_ENTITY_REFERENCE
    );

    // NBT key
    private static final String OWNER_TAG = "Owner";

    // 扫帚控制器，供拓展修改扫帚的操纵方式
    private final List<IBroomControl> broomControls;

    // 动态修改实体的碰撞体积，用来避免玩家卡进方块里
    public boolean inPhysicalCheck = false;
    private AABB physicalBoundingBox = new AABB(Vec3.ZERO, Vec3.ZERO);

    public EntityBroom(EntityType<EntityBroom> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.broomControls = BroomControlManager.onBroomInit(this);
    }

    public EntityBroom(Level level) {
        this(TYPE, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setOwnerReference(EntityReference.read(input, OWNER_TAG));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        EntityReference.store(this.getOwnerReference(), output, OWNER_TAG);
    }

    @Override
    protected AABB makeBoundingBox(Vec3 position) {
        AABB aabb = super.makeBoundingBox(position);
        if (this.getPassengers().size() > 1) {
            // 如果有乘客，扫帚的碰撞盒就变大一点
            this.physicalBoundingBox = new AABB(
                    aabb.minX, aabb.minY, aabb.minZ,
                    aabb.maxX, aabb.maxY + 1, aabb.maxZ
            );
        } else {
            this.physicalBoundingBox = aabb;
        }
        return aabb;
    }

    @Override
    public void travel(Vec3 vec3) {
        Entity entity = this.getControllingPassenger();
        Entity secondPassenger = this.getPassengers().size() >= 2 ? this.getPassengers().get(1) : null;

        // 只有玩家和女仆都骑乘时，才能操纵扫帚
        if (entity instanceof Player player && secondPassenger instanceof EntityMaid maid) {
            for (IBroomControl broomControl : this.broomControls) {
                if (broomControl.inControl(player, maid)) {
                    broomControl.travel(player, maid);
                    break;
                }
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            return;
        }

        // 玩家没有坐在扫帚上，那就让它掉下来
        if (!this.onGround()) {
            super.travel(new Vec3(0, -0.3f, 0));
            return;
        }
        super.travel(vec3);
    }

    @Override
    public float getRiddenSpeed(Player player) {
        return (float) player.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    protected void pushEntities() {
        // 已经坐满两人，不执行
        if (this.getPassengers().size() >= 2) {
            return;
        }
        // 已经坐了一人，但不是玩家，不执行
        if (!this.getPassengers().isEmpty() && !(this.getControllingPassenger() instanceof Player)) {
            return;
        }
        // 执行，此时周围的女仆会尝试坐上去
        if (!level.isClientSide()) {
            AABB aabb = getBoundingBox().expandTowards(0.5, 0.1, 0.5);
            List<EntityMaid> list = level.getEntitiesOfClass(EntityMaid.class, aabb, this::canMaidRide);
            list.stream().findFirst().ifPresent(entity -> entity.startRiding(this));
        }
    }

    private boolean canMaidRide(EntityMaid maid) {
        if (maid.canBrainMoving() && !maid.isVehicle() && EntitySelector.pushableBy(this).test(maid)) {
            var maidOwner = maid.getOwnerReference();
            if (maidOwner == null) {
                return false;
            }
            // 扫帚和女仆的所属者都是同一个人，才可以坐上去
            return Objects.equals(maidOwner, this.getOwnerReference());
        }
        return false;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        // 记得将 fall distance 设置为 0，否则会摔死
        this.fallDistance = 0;

        // 施加上下晃动
        if (!this.onGround()) {
            double yOffset = 0.003 * Math.sin(this.tickCount * Math.PI / 18);
            this.addDeltaMovement(new Vec3(0, yOffset, 0));
        }

        // 与旋转有关系的一堆东西，用来控制扫帚朝向
        Entity secondPassenger = this.getPassengers().size() >= 2 ? this.getPassengers().get(1) : null;
        if (secondPassenger instanceof EntityMaid maid) {
            for (IBroomControl broomControl : this.broomControls) {
                if (broomControl.inControl(player, maid)) {
                    broomControl.tickRot(player, maid);
                    break;
                }
            }
        } else {
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
            this.setRot(player.getYRot(), player.getXRot());
        }

        super.tickRidden(player, travelVector);
    }

    @Override
    public void setRot(float pYRot, float pXRot) {
        super.setRot(pYRot, pXRot);
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimension, float partialTick) {
        double xOffset = passenger instanceof EntityMaid ? -0.5 : 0;
        if (this.getPassengers().size() > 1) {
            if (this.getPassengers().indexOf(passenger) == 0) {
                xOffset = 0.35;
            } else {
                xOffset = -0.35;
            }
        }
        Vec3 hOffset = new Vec3(xOffset, -0.3125, 0).yRot((float) (-(this.getYRot() + 90) * Math.PI / 180));
        return super.getPassengerAttachmentPoint(passenger, dimension, partialTick).add(hOffset);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 position) {
        if (player.isDiscrete() || this.isPassenger() || this.getControllingPassenger() instanceof Player) {
            return super.interact(player, hand, position);
        }
        if (this.getPassengers().size() > 1) {
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide()) {
            player.startRiding(this);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        // 操控实体只能是玩家
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        boolean hasPlayer = false;
        EntityMaid maidOpen = null;

        // 先检查是否有自己和女仆骑乘在上面
        List<Entity> passengers = this.getPassengers();
        for (int i = 0; i < Math.min(passengers.size(), 2); i++) {
            Entity entity = passengers.get(i);
            if (entity.equals(player)) {
                hasPlayer = true;
            }
            if (entity instanceof EntityMaid maid && maid.isOwnedBy(player)) {
                maidOpen = maid;
            }
        }

        // 玩家不对不能打开物品栏
        if (!hasPlayer) {
            return;
        }

        if (maidOpen == null) {
            // 玩家物品栏打开很特殊，需要从客户端触发
            OpenPlayerInventoryPackage msg = new OpenPlayerInventoryPackage(OPEN_PLAYER_INVENTORY);
            PacketDistributor.sendToPlayer(serverPlayer, msg);
        } else {
            maidOpen.openMaidGui(serverPlayer);
        }
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return this.isAlive();
    }

    /**
     * 当玩家骑在扫帚上时，让扫帚本体不可被选中
     * 防止其碰撞箱影响正常交互
     */
    @Override
    public boolean isPickable() {
        boolean hasPlayer = this.getPassengers().stream().anyMatch(e -> e instanceof Player);
        return !hasPlayer && super.isPickable();
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() < 2;
    }

    @Override
    protected boolean canKillEntity(Player player) {
        return true;
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.WOOL_BREAK;
    }

    @Override
    protected Item getWithItem() {
        return InitItems.BROOM.get();
    }

    @Override
    protected ItemStack getKilledStack() {
        return this.getWithItem().getDefaultInstance();
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
        this.resetFallDistance();
    }

    public AABB getPhysicalBoundingBox() {
        return physicalBoundingBox;
    }

    @Override
    @Nullable
    public EntityReference<LivingEntity> getOwnerReference() {
        return this.entityData.get(OWNER).orElse(null);
    }

    public void setOwnerReference(@Nullable EntityReference<LivingEntity> owner) {
        this.entityData.set(OWNER, Optional.ofNullable(owner));
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.setOwnerReference(EntityReference.of(owner));
    }
}
