package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidPickupEvent;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagItem;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityTombstone;
import com.github.tartaricacid.touhoulittlemaid.init.InitAttribute;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.MaidBackpackHandler;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.MaidInvWrapper;
import com.github.tartaricacid.touhoulittlemaid.item.ItemFilm;
import com.github.tartaricacid.touhoulittlemaid.mixin.accessor.ArrowAccessor;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.ItemBreakPackage;
import com.github.tartaricacid.touhoulittlemaid.util.EntityMaidEquipmentWrapper;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.datagen.EnchantmentKeys.getEnchantmentLevel;
import static com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagItem.MAID_VANISHING_BLOCKLIST_ITEM;
import static net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP;

/**
 * 物品管理类，各种形式的物品存入与取出
 */
@MaidManagerDef(alias = "itemManager", exposeView = true)
public class MaidItemManager {
    public static final String MAID_INVENTORY_TAG = "MaidInventory";
    public static final String MAID_BAUBLE_INVENTORY_TAG = "MaidBaubleInventory";
    public static final String MAID_HIDE_INVENTORY_TAG = "MaidHideInventory";
    public static final String MAID_TASK_INVENTORY_TAG = "MaidTaskInventory";

    private final EntityMaid maid;
    /**
     * 护甲栏包装类
     */
    private final ResourceHandler<ItemResource> armorInvWrapper;
    /**
     * 主副手包装类
     */
    private final ResourceHandler<ItemResource> handsInvWrapper;
    /**
     * 女仆主背包
     */
    private final ItemStacksResourceHandler maidInv;
    /**
     * 女仆饰品栏
     */
    private final BaubleItemHandler maidBauble;
    /**
     * 用于暂存副手物品的物品栏
     */
    private final ItemStacksResourceHandler hideInv;
    /**
     * 用于工作任务可能需要的物品栏
     */
    private final ItemStacksResourceHandler taskInv;

    public MaidItemManager(EntityMaid entityMaid) {
        this.maid = entityMaid;
        // TODO
        // 原版的LivingEntityEquipmentWrapper限定了实体的主副手槽的装备，这里我们依据其改成EntityMaidEquipmentWrapper专用，需要检查一下是否符合
        this.armorInvWrapper = EntityMaidEquipmentWrapper.of(maid, EquipmentSlot.Type.HUMANOID_ARMOR);
        this.handsInvWrapper = EntityMaidEquipmentWrapper.of(maid, EquipmentSlot.Type.HAND);
        this.maidInv = new MaidBackpackHandler(36, maid);
        this.maidBauble = new BaubleItemHandler(30);
        this.hideInv = new ItemStacksResourceHandler(1);
        this.taskInv = new ItemStacksResourceHandler(9);
    }

    @SuppressWarnings("deprecation")
    public static boolean canInsertItem(ItemStack stack) {
        Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (MaidConfig.MAID_BACKPACK_BLACKLIST.get().contains(key.toString())) {
            return false;
        }
        return stack.getItem().canFitInsideContainerItems();
    }

    public ResourceHandler<ItemResource> getArmorInvWrapper() {
        return armorInvWrapper;
    }

    public ResourceHandler<ItemResource> getHandsInvWrapper() {
        return handsInvWrapper;
    }

    public ItemStacksResourceHandler getMaidInv() {
        return maidInv;
    }

    public BaubleItemHandler getMaidBauble() {
        return maidBauble;
    }

    public ItemStacksResourceHandler getHideInv() {
        return hideInv;
    }

    public ItemStacksResourceHandler getTaskInv() {
        return taskInv;
    }

    /**
     * 获取女仆的全部物品栏（不考虑任何限制）
     * <p>
     * 一般情况下不应该调用此访问
     */
    public CombinedResourceHandler<ItemResource> getAllInv() {
        return new CombinedResourceHandler<>(
                getArmorInvWrapper(),
                getHandsInvWrapper(),
                getMaidInv(),
                getMaidBauble()
        );
    }

    /**
     * 获取可用的背包物品栏（因为女仆背包是可变大小的）
     * <p>
     * 返回 MaidInvWrapper，方便触发 MaidRequestItemEvent 事件时使用
     */
    public CombinedResourceHandler<ItemResource> getAvailableBackpackInv() {
        int maxContainerIndex = maid.getMaidBackpackType().getAvailableMaxContainerIndex();
        var rangedWrapper = RangedResourceHandler.of(maidInv, 0, maxContainerIndex);
        return new MaidInvWrapper(maid, rangedWrapper);
    }

    /**
     * 获取可用的背包物品栏 + 主手物品栏（因为女仆背包是可变大小的）
     * <p>
     * 返回 MaidInvWrapper，方便触发 MaidRequestItemEvent 事件时使用
     *
     * @param handsFirst 是否将手持物品栏放在前面，放在前面会优先使用手持物品栏的物品
     */
    public CombinedResourceHandler<ItemResource> getAvailableInv(boolean handsFirst) {
        int maxContainerIndex = maid.getMaidBackpackType().getAvailableMaxContainerIndex();
        var combinedInvWrapper = RangedResourceHandler.of(maidInv, 0, maxContainerIndex);
        if (handsFirst) {
            return new MaidInvWrapper(maid, handsInvWrapper, combinedInvWrapper);
        } else {
            return new MaidInvWrapper(maid, combinedInvWrapper, handsInvWrapper);
        }
    }

    /**
     * 直接将指定坐标处方块的掉落物放入女仆背包里，如果放不下了就掉落在地上
     *
     * @param state       准备被挖掘的方块状态
     * @param level       世界
     * @param pos         方块坐标
     * @param blockEntity 方块实体（可能为 null）
     * @param tool        挖掘方块使用的工具
     */
    public void dropResourcesToMaidInv(BlockState state, Level level, BlockPos pos, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        var availableInv = getAvailableInv(false);
        Block.getDrops(state, serverLevel, pos, blockEntity, maid, tool).forEach(stack -> {
            ItemStack remindItemStack = ItemsUtil.insertItemStacked(availableInv, stack, false, null);
            if (!remindItemStack.isEmpty()) {
                Block.popResource(level, pos, remindItemStack);
            }
        });
        state.spawnAfterBreak(serverLevel, pos, tool, true);
    }

    /**
     * 用于物品的耐久损失，同步到附近的玩家，发出破碎的效果
     */
    public void hurtAndBreak(ItemStack stack, int amount) {
        if (maid.level instanceof ServerLevel serverLevel) {
            stack.hurtAndBreak(amount, serverLevel, maid, stackIn -> {
                ItemStack instance = stackIn.getDefaultInstance();
                ItemBreakPackage msg = new ItemBreakPackage(maid.getId(), instance);
                NetworkHandler.sendToNearby(maid, msg);
            });
        }
    }

    public boolean pickupArrow(AbstractArrow arrow, boolean simulate) {
        MaidPickupEvent.ArrowResult event = new MaidPickupEvent.ArrowResult(maid, arrow, simulate);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return event.isCanPickup();
        }
        if (!maid.level.isClientSide() && arrow.isAlive() && arrow.shakeTime <= 0) {
            // 先判断箭是否处于可以拾起的状态
            if (arrow.pickup != AbstractArrow.Pickup.ALLOWED) {
                return false;
            }
            // 能够塞入
            ItemStack stack = getArrowFromEntity(arrow);
            if (stack.isEmpty()) {
                return false;
            }
            var inv = getAvailableInv(false);
            ItemStack inserted = ItemsUtil.insertItemStacked(inv, stack, simulate, null);
            if (!inserted.isEmpty()) {
                return false;
            }
            // 非模拟状态下，清除实体箭
            if (!simulate) {
                // 这是向客户端同步数据用的，如果加了这个方法，会有短暂的拾取动画和音效
                maid.take(arrow, 1);
                maid.tryPlayMaidPickupSound();
                arrow.discard();
            }
            return true;
        }
        return false;
    }

    public boolean pickupItem(ItemEntity entityItem, boolean simulate) {
        MaidPickupEvent.ItemResultPre event = new MaidPickupEvent.ItemResultPre(maid, entityItem, simulate);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return event.isCanPickup();
        }
        if (!maid.level.isClientSide() && entityItem.isAlive() && !entityItem.hasPickUpDelay()) {
            // 获取实体的物品堆
            ItemStack itemstack = entityItem.getItem();
            // 检查物品是否合法
            if (!MaidItemManager.canInsertItem(itemstack)) {
                return false;
            }
            // 获取数量，为后面方面用
            int count = itemstack.getCount();
            var inv = getAvailableInv(false);
            itemstack = ItemsUtil.insertItemStacked(inv, itemstack, simulate, null);
            if (count == itemstack.getCount()) {
                return false;
            }
            if (!simulate) {
                // 这是向客户端同步数据用的，如果加了这个方法，会有短暂的拾取动画和音效
                maid.take(entityItem, count - itemstack.getCount());
                maid.tryPlayMaidPickupSound();
                ItemStack copy = new ItemStack(itemstack.getItem(), count - itemstack.getCount());
                // 如果遍历塞完后发现为空了
                if (itemstack.isEmpty()) {
                    // 清除这个实体
                    entityItem.discard();
                } else {
                    // 将物品数量同步到客户端
                    entityItem.setItem(itemstack);
                }
                NeoForge.EVENT_BUS.post(new MaidPickupEvent.ItemResultPost(maid, copy));
            }
            return true;
        }
        return false;
    }

    public void pickupXPOrb(ExperienceOrb entityXPOrb) {
        MaidPickupEvent.ExperienceResult event = new MaidPickupEvent.ExperienceResult(maid, entityXPOrb, false);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return;
        }
        if (!maid.level.isClientSide() && entityXPOrb.isAlive() && entityXPOrb.tickCount > 2) {
            // 这是向客户端同步数据用的，如果加了这个方法，会有短暂的拾取动画和音效
            maid.take(entityXPOrb, 1);
            maid.tryPlayMaidPickupSound();

            // 普通的经验球可以修补护甲栏，主副手和女仆饰品栏
            var allItems = new CombinedResourceHandler<>(armorInvWrapper, handsInvWrapper, maidBauble);
            ItemStack itemstack = getRandomItemWithMendingEnchantments(allItems);
            if (!itemstack.isEmpty() && itemstack.isDamaged()) {
                int i = Math.min((int) (entityXPOrb.getValue() * itemstack.getXpRepairRatio()), itemstack.getDamageValue());
                entityXPOrb.setValue(entityXPOrb.getValue() - (i / 2));
                itemstack.setDamageValue(itemstack.getDamageValue() - i);
            }
            if (entityXPOrb.getValue() > 0) {
                maid.setExperience(maid.getExperience() + entityXPOrb.getValue());
            }
            entityXPOrb.discard();
        }
    }

    public void pickupPowerPoint(EntityPowerPoint powerPoint) {
        MaidPickupEvent.PowerPointResult event = new MaidPickupEvent.PowerPointResult(maid, powerPoint, false);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return;
        }
        if (!maid.level.isClientSide() && powerPoint.isAlive() && powerPoint.throwTime == 0) {
            // 这是向客户端同步数据用的，如果加了这个方法，会有短暂的拾取动画和音效
            powerPoint.take(maid, 1);
            maid.tryPlayMaidPickupSound();

            // P 点则可以修补女仆身上所有的物品栏（包括背包）
            var allItems = getAllInv();
            ItemStack itemstack = getRandomItemWithMendingEnchantments(allItems);
            int xpValue = EntityPowerPoint.transPowerValueToXpValue(powerPoint.getValue());
            if (!itemstack.isEmpty() && itemstack.isDamaged()) {
                int i = Math.min((int) (xpValue * itemstack.getXpRepairRatio()), itemstack.getDamageValue());
                xpValue -= (i / 2);
                itemstack.setDamageValue(itemstack.getDamageValue() - i);
            }
            if (xpValue > 0) {
                maid.setExperience(maid.getExperience() + xpValue);
            }
            powerPoint.discard();
        }
    }

    public boolean canPickup(Entity pickupEntity, boolean checkInWater) {
        if (maid.isPickup()) {
            if (checkInWater && pickupEntity.isInWater()) {
                return false;
            }
            PickType pickupType = maid.getConfigManager().getPickupType();
            if (pickupType.canPickItem() && pickupEntity instanceof ItemEntity entity) {
                return pickupItem(entity, true);
            }
            if (pickupType.canPickItem() && pickupEntity instanceof AbstractArrow entity) {
                return pickupArrow(entity, true);
            }
            if (pickupType.canPickXp() && pickupEntity instanceof ExperienceOrb) {
                return true;
            }
            return pickupType.canPickXp() && pickupEntity instanceof EntityPowerPoint;
        }
        return false;
    }

    void pickupEntities() {
        AABB pickupBox;
        AttributeInstance attribute = maid.getAttribute(InitAttribute.MAID_PICKUP_RANGE);
        if (attribute != null) {
            pickupBox = maid.getBoundingBox().inflate(attribute.getValue());
        } else {
            pickupBox = maid.getBoundingBox().inflate(0.5);
        }

        List<Entity> entityList = maid.level.getEntities(maid, pickupBox, maid::canPickup);
        if (!entityList.isEmpty() && maid.isAlive()) {
            for (Entity entityPickup : entityList) {
                // 如果是物品
                if (entityPickup instanceof ItemEntity entity) {
                    pickupItem(entity, false);
                }
                // 如果是经验
                if (entityPickup instanceof ExperienceOrb entity) {
                    pickupXPOrb(entity);
                }
                // 如果是 P 点
                if (entityPickup instanceof EntityPowerPoint entity) {
                    pickupPowerPoint(entity);
                }
                // 如果是箭
                if (entityPickup instanceof AbstractArrow entity) {
                    pickupArrow(entity, false);
                }
            }
        }
    }

    void addItemsToTomb(EntityTombstone tombstone) {
        // 女仆物品栏
        var allInv = new CombinedResourceHandler<>(armorInvWrapper, handsInvWrapper, maidInv, maidBauble, hideInv, taskInv);
        // 需要考虑消失诅咒附魔
        this.destroyVanishingCursedItems(allInv);
        // 将物品栏里的物品都放入墓碑里
        for (int i = 0; i < allInv.size(); i++) {
            ItemResource resource = allInv.getResource(i);
            if (resource.isEmpty()) {
                continue;
            }
            int size = allInv.getCapacityAsInt(i, resource);
            ItemStack extractItem = ItemsUtil.extractItem(allInv, i, size, false, null);
            tombstone.insertItem(extractItem);
        }
        // 背包额外数据
        IMaidBackpack maidBackpack = maid.getMaidBackpackType();
        tombstone.insertItem(maidBackpack.getTakeOffItemStack(ItemStack.EMPTY, null, maid));
        maidBackpack.onSpawnTombstone(maid, tombstone);
        // 胶片
        ItemStack filmItem = ItemFilm.maidToFilm(maid);
        tombstone.insertItem(filmItem);
    }

    private void destroyVanishingCursedItems(CombinedResourceHandler<ItemResource> invWrapper) {
        if (maid.level instanceof ServerLevel level && level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            return;
        }
        for (int i = 0; i < invWrapper.size(); ++i) {
            ItemStack stack = ItemUtil.getStack(invWrapper, i);
            if (!stack.isEmpty() && EnchantmentHelper.has(stack, PREVENT_EQUIPMENT_DROP) && !stack.is(MAID_VANISHING_BLOCKLIST_ITEM)) {
                ItemsUtil.extractItem(invWrapper, i, stack.getCount(), false, null);
            }
        }
    }

    /**
     * 将之前临时存在背包里的物品再次放在对应的手上
     *
     */
    void backCurrentHandItemStack(EntityMaid maid) {
        // 先看看副手是否为空？
        ItemStack offhandItem = maid.getItemInHand(InteractionHand.OFF_HAND);
        if (!offhandItem.isEmpty()) {
            var backpackInv = getAvailableBackpackInv();
            ItemStack stack = ItemsUtil.insertItemStacked(backpackInv, offhandItem.copy(), false, null);
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(maid.level(), maid.getX(), maid.getY() + 0.5, maid.getZ(), stack);
                maid.level.addFreshEntity(itemEntity);
            }
        }
        // 副手此时为空，那么插入我们的物品
        var hide = this.getHideInv();
        ItemStack stack = ItemUtil.getStack(hide, 0);
        ItemStack output = ItemsUtil.extractItem(hide, 0, stack.getCount(), false, null);
        maid.setItemInHand(InteractionHand.OFF_HAND, output);
    }

    void onEquipItem(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem) {
        if (newItem.isEmpty() || maid.firstTick() || !slot.isArmor()) {
            return;
        }

        // 触发成就
        if (maid.getOwner() instanceof ServerPlayer serverPlayer) {
            InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.ANY_EQUIPMENT);
        }

        // 如果是下界合金
        if (this.isNetheriteArmor(newItem)) {
            // 检查全身装备
            for (EquipmentSlot slotIn : EquipmentSlot.values()) {
                if (!slotIn.isArmor() || slotIn == slot || slotIn == EquipmentSlot.BODY) {
                    continue;
                }
                ItemStack itemBySlot = maid.getItemBySlot(slotIn);
                if (!isNetheriteArmor(itemBySlot)) {
                    return;
                }
            }
            // 触发事件
            if (maid.getOwner() instanceof ServerPlayer serverPlayer) {
                InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.ALL_NETHERITE_EQUIPMENT);
            }
        }
    }

    void updateUsingItem(ItemStack usingItem) {
        // 处理问题 https://github.com/TartaricAcid/TouhouLittleMaid/issues/1003
        // 检测女仆是否处于异常的进食状态：正在使用物品但手中物品不是可正常使用状态下的物品
        if (maid.isUsingItem()) {
            ItemStack currentItem = maid.getUseItem();
            // 如果正在使用物品但该物品无法继续使用（例如食物已被移除），则强制停止使用
            if (currentItem.isEmpty() || currentItem.getUseDuration(maid) <= 0) {
                maid.stopUsingItem();
                return;
            }
        }

        if (!usingItem.isEmpty()) {
            AttributeInstance attribute = maid.getAttribute(InitAttribute.MAID_USE_ITEM_SPEED);
            if (attribute != null) {
                // MAID_USE_ITEM_SPEED 默认是 1
                // 故这里减去属性值再加 1，保证属性值为 1 时行为和原版一致
                maid.setUseItemRemainingTicks(maid.getUseItemRemainingTicks() - (int) attribute.getValue() + 1);
            }
        }
    }

    void save(ValueOutput output) {
        maidInv.serialize(output.child(MAID_INVENTORY_TAG));
        maidBauble.serialize(output.child(MAID_BAUBLE_INVENTORY_TAG));
        hideInv.serialize(output.child(MAID_HIDE_INVENTORY_TAG));
        taskInv.serialize(output.child(MAID_TASK_INVENTORY_TAG));
    }

    void read(ValueInput input) {
        maidInv.deserialize(input.childOrEmpty(MAID_INVENTORY_TAG));
        maidBauble.deserialize(input.childOrEmpty(MAID_BAUBLE_INVENTORY_TAG));
        hideInv.deserialize(input.childOrEmpty(MAID_HIDE_INVENTORY_TAG));
        taskInv.deserialize(input.childOrEmpty(MAID_TASK_INVENTORY_TAG));
    }

    /**
     * 当需要临时调换手中物品和背包内物品时，可调用此方法
     * 当置换后的物品使用完后会自动将之前的手中物品再次返回到手上
     *
     * @param itemStack 当前手上的物品（必须是能使用--需要持续使用的物品）
     */
    public void memoryHandItemStack(ItemStack itemStack) {
        var hide = getHideInv();
        // 先检查内部存储是否已经有物品了，有就掉落
        ItemStack hideItemStack = ItemUtil.getStack(hide, 0);
        if (!hideItemStack.isEmpty()) {
            ItemStack extractItem = ItemsUtil.extractItem(hide, 0, hideItemStack.getCount(), false, null);
            if (!extractItem.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(maid.level(), maid.getX(), maid.getY() + 0.5, maid.getZ(), extractItem);
                maid.level.addFreshEntity(itemEntity);
            }
        }
        // 然后存入我们的物品
        ItemsUtil.insertItemStacked(hide, itemStack, false, null);
    }

    /**
     * 当女仆吃完后的容器无处可存，就直接以实体形式掉落
     */
    void handleExtraItemsCreatedOnUse(ItemStack convertedStack) {
        var availableInv = this.getAvailableInv(false);
        try (Transaction tx = Transaction.openRoot()) {
            ItemResource resource = ItemResource.of(convertedStack);
            int insert = availableInv.insert(resource, convertedStack.count(), tx);
            // 如果女仆背包满了，掉落在地上
            if (insert < convertedStack.count()) {
                ItemStack droppedStack = convertedStack.copyWithCount(convertedStack.count() - insert);
                ItemEntity itemEntity = new ItemEntity(maid.level, maid.getX(), maid.getY(), maid.getZ(), droppedStack);
                maid.level.addFreshEntity(itemEntity);
            }
            tx.commit();
        }
    }

    private ItemStack getRandomItemWithMendingEnchantments(ResourceHandler<ItemResource> handler) {
        RegistryAccess access = maid.level.registryAccess();
        List<ItemStack> stacks = Lists.newArrayList();
        for (int i = 0; i < handler.size(); i++) {
            ItemStack stackInSlot = ItemUtil.getStack(handler, i);
            if (!stackInSlot.isEmpty() && getEnchantmentLevel(access, Enchantments.MENDING, stackInSlot) > 0
                && stackInSlot.isDamaged() && !stackInSlot.is(TagItem.MAID_MENDING_BLOCKLIST_ITEM)) {
                stacks.add(stackInSlot);
            }
        }
        return stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(maid.getRandom().nextInt(stacks.size()));
    }

    private boolean isNetheriteArmor(ItemStack stack) {
        //FIXME 判断合理?
        if (stack.has(DataComponents.EQUIPPABLE) && stack.has(DataComponents.REPAIRABLE)) {
            return stack.get(DataComponents.REPAIRABLE).isValidRepairItem(Items.NETHERITE_INGOT.getDefaultInstance());
        }
        return false;
    }

    private ItemStack getArrowFromEntity(AbstractArrow entity) {
        if (entity instanceof ArrowAccessor mixinArrow) {
            if (mixinArrow.tlmInGround() || entity.isNoPhysics()) {
                return mixinArrow.getTlmPickupItem();
            }
        }
        return ItemStack.EMPTY;
    }

    public interface View {
        MaidItemManager getItemManager();

        default ResourceHandler<ItemResource> getArmorInvWrapper() {
            return getItemManager().getArmorInvWrapper();
        }

        default ResourceHandler<ItemResource> getHandsInvWrapper() {
            return getItemManager().getHandsInvWrapper();
        }

        default ItemStacksResourceHandler getMaidInv() {
            return getItemManager().getMaidInv();
        }

        default BaubleItemHandler getMaidBauble() {
            return getItemManager().getMaidBauble();
        }

        default ItemStacksResourceHandler getHideInv() {
            return getItemManager().getHideInv();
        }

        default ItemStacksResourceHandler getTaskInv() {
            return getItemManager().getTaskInv();
        }

        default CombinedResourceHandler<ItemResource> getAllInv() {
            return getItemManager().getAllInv();
        }

        default CombinedResourceHandler<ItemResource> getAvailableBackpackInv() {
            return getItemManager().getAvailableBackpackInv();
        }

        default CombinedResourceHandler<ItemResource> getAvailableInv(boolean handsFirst) {
            return getItemManager().getAvailableInv(handsFirst);
        }

        default void dropResourcesToMaidInv(BlockState state, Level level, BlockPos pos,
                                            @Nullable BlockEntity blockEntity, ItemStack tool) {
            getItemManager().dropResourcesToMaidInv(state, level, pos, blockEntity, tool);
        }

        default void hurtAndBreak(ItemStack stack, int amount) {
            getItemManager().hurtAndBreak(stack, amount);
        }

        default boolean pickupArrow(AbstractArrow arrow, boolean simulate) {
            return getItemManager().pickupArrow(arrow, simulate);
        }

        default boolean pickupItem(ItemEntity entityItem, boolean simulate) {
            return getItemManager().pickupItem(entityItem, simulate);
        }

        default void pickupXPOrb(ExperienceOrb entityXPOrb) {
            getItemManager().pickupXPOrb(entityXPOrb);
        }

        default void pickupPowerPoint(EntityPowerPoint powerPoint) {
            getItemManager().pickupPowerPoint(powerPoint);
        }

        default boolean canPickup(Entity pickupEntity, boolean checkInWater) {
            return getItemManager().canPickup(pickupEntity, checkInWater);
        }

        default boolean canPickup(Entity pickupEntity) {
            return getItemManager().canPickup(pickupEntity, false);
        }

        default void memoryHandItemStack(ItemStack itemStack) {
            getItemManager().memoryHandItemStack(itemStack);
        }
    }
}
