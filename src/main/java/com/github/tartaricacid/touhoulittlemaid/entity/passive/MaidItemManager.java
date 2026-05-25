package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidPickupEvent;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagItem;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityTombstone;
import com.github.tartaricacid.touhoulittlemaid.init.InitAttribute;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.MaidBackpackHandler;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.MaidInvWrapper;
import com.github.tartaricacid.touhoulittlemaid.item.ItemFilm;
import com.github.tartaricacid.touhoulittlemaid.mixin.accessor.ArrowAccessor;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
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
import net.neoforged.neoforge.transfer.item.LivingEntityEquipmentWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.datagen.EnchantmentKeys.getEnchantmentLevel;

public class MaidItemManager {

    public static final String MAID_INVENTORY_TAG = EntityMaid.MAID_INVENTORY_TAG;
    public static final String MAID_BAUBLE_INVENTORY_TAG = EntityMaid.MAID_BAUBLE_INVENTORY_TAG;
    public static final String MAID_HIDE_INVENTORY_TAG = EntityMaid.MAID_HIDE_INVENTORY_TAG;
    public static final String MAID_TASK_INVENTORY_TAG = EntityMaid.MAID_TASK_INVENTORY_TAG;

    private final EntityMaid maid;

    // 物品存储相关
    private final ResourceHandler<@NotNull ItemResource> armorInvWrapper;
    private final ResourceHandler<@NotNull ItemResource> handsInvWrapper;
    final ItemStacksResourceHandler maidInv;
    final BaubleItemHandler maidBauble = new BaubleItemHandler(EntityMaid.BAUBLE_INV_SIZE);
    // 用于暂存副手物品的物品栏
    private final ItemStacksResourceHandler hideInv = new ItemStacksResourceHandler(1);
    // 用于工作任务可能需要的物品栏
    private final ItemStacksResourceHandler taskInv = new ItemStacksResourceHandler(9);

    public MaidItemManager(EntityMaid entityMaid) {
        maid = entityMaid;
        armorInvWrapper = LivingEntityEquipmentWrapper.of(maid, EquipmentSlot.Type.HUMANOID_ARMOR);
        handsInvWrapper = LivingEntityEquipmentWrapper.of(maid, EquipmentSlot.Type.HAND);
        maidInv = new MaidBackpackHandler(36, maid);
    }

    public void addAdditionalSaveData(ValueOutput output) {
        maidInv.serialize(output.child(MAID_INVENTORY_TAG));
        maidBauble.serialize(output.child(MAID_BAUBLE_INVENTORY_TAG));
        hideInv.serialize(output.child(MAID_HIDE_INVENTORY_TAG));
        taskInv.serialize(output.child(MAID_TASK_INVENTORY_TAG));
    }

    public void readAdditionalSaveData(ValueInput input) {
        maidInv.deserialize(input.childOrEmpty(MAID_INVENTORY_TAG));
        maidBauble.deserialize(input.childOrEmpty(MAID_BAUBLE_INVENTORY_TAG));
        hideInv.deserialize(input.childOrEmpty(MAID_HIDE_INVENTORY_TAG));
        taskInv.deserialize(input.childOrEmpty(MAID_TASK_INVENTORY_TAG));
    }

    /**
     * 获取隐藏物品栏
     */
    public ItemStacksResourceHandler getHideInv() {
        return hideInv;
    }

    /**
     * 获取任务物品栏
     */
    public ItemStacksResourceHandler getTaskInv() {
        return taskInv;
    }

    public BaubleItemHandler getMaidBauble() {
        return maidBauble;
    }

    public ResourceHandler<@NotNull ItemResource> getHandsInvWrapper() {
        return handsInvWrapper;
    }

    public ResourceHandler<@NotNull ItemResource> getArmorInvWrapper() {
        return armorInvWrapper;
    }

    public CombinedResourceHandler<@NotNull ItemResource> getAllInv() {
        return new CombinedResourceHandler<>(getArmorInvWrapper(), getHandsInvWrapper(), getMaidInv(), getMaidBauble());
    }

    /**
     * 返回 MaidInvWrapper，方便触发 MaidRequestItemEvent 事件时使用
     */
    public CombinedResourceHandler<@NotNull ItemResource> getAvailableBackpackInv() {
        int maxContainerIndex = maid.getMaidBackpackType().getAvailableMaxContainerIndex();
        var rangedWrapper = RangedResourceHandler.of(maidInv, 0, maxContainerIndex);
        return new MaidInvWrapper(maid, rangedWrapper);
    }

    public ItemStacksResourceHandler getMaidInv() {
        return maidInv;
    }

    /**
     * 返回 MaidInvWrapper，方便触发 MaidRequestItemEvent 事件时使用
     *
     * @param handsFirst
     */
    public CombinedResourceHandler<@NotNull ItemResource> getAvailableInv(boolean handsFirst) {
        int maxContainerIndex = maid.getMaidBackpackType().getAvailableMaxContainerIndex();
        var combinedInvWrapper = RangedResourceHandler.of(maidInv, 0, maxContainerIndex);
        return handsFirst ? new MaidInvWrapper(maid, handsInvWrapper, combinedInvWrapper)
                : new MaidInvWrapper(maid, combinedInvWrapper, handsInvWrapper);
    }

    public void dropResourcesToMaidInv(BlockState state, Level level, BlockPos pos, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (level instanceof ServerLevel serverLevel) {
            var availableInv = getAvailableInv(false);
            Block.getDrops(state, serverLevel, pos, blockEntity, maid, tool).forEach(stack -> {
                ItemStack remindItemStack = ItemsUtil.insertItemStacked(availableInv, stack, false, null);
                if (!remindItemStack.isEmpty()) {
                    Block.popResource(level, pos, remindItemStack);
                }
            });
            state.spawnAfterBreak(serverLevel, pos, tool, true);
        }
    }

    private ItemStack getRandomItemWithMendingEnchantments(ResourceHandler<@NotNull ItemResource> handler) {
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

    @SuppressWarnings("ReferenceToMixin")
    private ItemStack getArrowFromEntity(AbstractArrow entity) {
        if (entity instanceof ArrowAccessor mixinArrow) {
            if (mixinArrow.tlmInGround() || entity.isNoPhysics()) {
                return mixinArrow.getTlmPickupItem();
            }
        }
        return ItemStack.EMPTY;
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
            if (!ItemsUtil.insertItemStacked(getAvailableInv(false), stack, simulate, null).isEmpty()) {
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
            if (!EntityMaid.canInsertItem(itemstack)) {
                return false;
            }
            // 获取数量，为后面方面用
            int count = itemstack.getCount();
            itemstack = ItemsUtil.insertItemStacked(getAvailableInv(false), itemstack, simulate, null);
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

            // 对经验修补的应用，因为全部来自于原版，所以效果也是相同的
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

            // 对经验修补的应用，因为全部来自于原版，所以效果也是相同的
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

    public void pickupEntities() {

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
                if (entityPickup instanceof ItemEntity) {
                    pickupItem((ItemEntity) entityPickup, false);
                }
                // 如果是经验
                if (entityPickup instanceof ExperienceOrb) {
                    pickupXPOrb((ExperienceOrb) entityPickup);
                }
                // 如果是 P 点
                if (entityPickup instanceof EntityPowerPoint) {
                    pickupPowerPoint((EntityPowerPoint) entityPickup);
                }
                // 如果是箭
                if (entityPickup instanceof AbstractArrow) {
                    pickupArrow((AbstractArrow) entityPickup, false);
                }
            }
        }
    }

    public boolean canPickup(Entity pickupEntity, boolean checkInWater) {
        if (maid.isPickup()) {
            if (checkInWater && pickupEntity.isInWater()) {
                return false;
            }
            PickType pickupType = maid.getConfigManager().getPickupType();
            if (pickupType.canPickItem() && pickupEntity instanceof ItemEntity) {
                return pickupItem((ItemEntity) pickupEntity, true);
            }
            if (pickupType.canPickItem() && pickupEntity instanceof AbstractArrow) {
                return pickupArrow((AbstractArrow) pickupEntity, true);
            }
            if (pickupType.canPickXp() && pickupEntity instanceof ExperienceOrb) {
                return true;
            }
            return pickupType.canPickXp() && pickupEntity instanceof EntityPowerPoint;
        }
        return false;
    }

    public void addItemsToTomb(EntityTombstone tombstone) {
        // 女仆物品栏
        CombinedResourceHandler<@NotNull ItemResource> invWrapper = new CombinedResourceHandler<>(armorInvWrapper, handsInvWrapper, maidInv, maidBauble, hideInv, taskInv);
        // 需要考虑消失诅咒附魔
        destroyVanishingCursedItems(invWrapper);
        for (int i = 0; i < invWrapper.size(); i++) {
            ItemResource resource = invWrapper.getResource(i);
            // TODO resource不能为空，否则会报错
            if (resource.isEmpty())
                continue;
            int size = invWrapper.getCapacityAsInt(i, resource);
            tombstone.insertItem(ItemsUtil.extractItem(invWrapper, i, size, false, null));
        }
        // 背包额外数据
        IMaidBackpack maidBackpack = maid.getMaidBackpackType();
        tombstone.insertItem(maidBackpack.getTakeOffItemStack(ItemStack.EMPTY, null, maid));
        maidBackpack.onSpawnTombstone(maid, tombstone);
        // 胶片
        ItemStack filmItem = ItemFilm.maidToFilm(maid);
        tombstone.insertItem(filmItem);
    }

    private void destroyVanishingCursedItems(CombinedResourceHandler<@NotNull ItemResource> invWrapper) {
        if (maid.level instanceof ServerLevel level && level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            return;
        }
        for (int i = 0; i < invWrapper.size(); ++i) {
            ItemStack stack = ItemUtil.getStack(invWrapper, i);
            if (!stack.isEmpty() && EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP) && !stack.is(TagItem.MAID_VANISHING_BLOCKLIST_ITEM)) {
                ItemsUtil.extractItem(invWrapper, i, stack.getCount(), false, null);
            }
        }
    }

    /**
     * 将之前临时存在背包里的物品再次放在对应的手上
     *
     * @param maid
     */
    void backCurrentHandItemStack(EntityMaid maid) {
        // 先看看副手是否为空？
        ItemStack offhandItem = maid.getItemInHand(InteractionHand.OFF_HAND);
        if (!offhandItem.isEmpty()) {
            ItemStack stack = ItemsUtil.insertItemStacked(getAvailableBackpackInv(), offhandItem.copy(), false, null);
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(maid.level(), maid.getX(), maid.getY() + 0.5, maid.getZ(), stack);
                maid.level.addFreshEntity(itemEntity);
            }
        }
        // 副手此时为空，那么插入我们的物品
        ItemStack output = ItemsUtil.extractItem(getHideInv(), 0, ItemUtil.getStack(getHideInv(), 0).getCount(), false, null);
        maid.setItemInHand(InteractionHand.OFF_HAND, output);
    }

    /**
     * 当需要临时调换手中物品和背包内物品时，可调用此方法
     * 当置换后的物品使用完后会自动将之前的手中物品再次返回到手上
     *
     * @param itemStack 当前手上的物品（必须是能使用--需要持续使用的物品）
     */
    public void memoryHandItemStack(ItemStack itemStack) {
        // 先检查内部存储是否已经有物品了，有就掉落
        ItemStack hideItemStack = ItemUtil.getStack(getHideInv(), 0);
        if (!hideItemStack.isEmpty()) {
            ItemStack extractItem = ItemsUtil.extractItem(getHideInv(), 0, hideItemStack.getCount(), false, null);
            if (!extractItem.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(maid.level(), maid.getX(), maid.getY() + 0.5, maid.getZ(), extractItem);
                maid.level.addFreshEntity(itemEntity);
            }
        }
        // 然后存入我们的物品
        ItemsUtil.insertItemStacked(getHideInv(), itemStack, false, null);
    }

    public interface View {

        MaidItemManager getItemManager();

        /**
         * 获取隐藏物品栏
         */
        default ItemStacksResourceHandler getHideInv() {
            return getItemManager().getHideInv();
        }

        /**
         * 获取任务物品栏
         */
        default ItemStacksResourceHandler getTaskInv() {
            return getItemManager().getTaskInv();
        }

        default BaubleItemHandler getMaidBauble() {
            return getItemManager().getMaidBauble();
        }

        default ResourceHandler<@NotNull ItemResource> getHandsInvWrapper() {
            return getItemManager().getHandsInvWrapper();
        }

        default ResourceHandler<@NotNull ItemResource> getArmorInvWrapper() {
            return getItemManager().getArmorInvWrapper();
        }

        default CombinedResourceHandler<@NotNull ItemResource> getAllInv() {
            return getItemManager().getAllInv();
        }

        /**
         * 返回 MaidInvWrapper，方便触发 MaidRequestItemEvent 事件时使用
         */
        default CombinedResourceHandler<@NotNull ItemResource> getAvailableBackpackInv() {
            return getItemManager().getAvailableBackpackInv();
        }

        default ItemStacksResourceHandler getMaidInv() {
            return getItemManager().getMaidInv();
        }

        /**
         * 返回 MaidInvWrapper，方便触发 MaidRequestItemEvent 事件时使用
         *
         * @param handsFirst
         */
        default CombinedResourceHandler<@NotNull ItemResource> getAvailableInv(boolean handsFirst) {
            return getItemManager().getAvailableInv(handsFirst);
        }

        default void dropResourcesToMaidInv(BlockState state, Level level, BlockPos pos, @Nullable BlockEntity blockEntity, EntityMaid maid, ItemStack tool) {
            getItemManager().dropResourcesToMaidInv(state, level, pos, blockEntity, tool);
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

        default void memoryHandItemStack(ItemStack itemStack) {
            getItemManager().memoryHandItemStack(itemStack);
        }

    }

}
