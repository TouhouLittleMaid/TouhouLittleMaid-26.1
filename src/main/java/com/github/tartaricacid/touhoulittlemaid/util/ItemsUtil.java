package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidRequestItemEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.MaidInvWrapper;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public final class ItemsUtil {
    private ItemsUtil() {
    }

    /**
     * 直接设置对应槽位的物品堆
     */
    public static void setStackInSlot(ResourceHandler<ItemResource> itemHandler, int slot, ItemStack stack) {
        if (itemHandler instanceof StacksResourceHandler<?, ItemResource> stacksResourceHandler) {
            stacksResourceHandler.set(slot, ItemResource.of(stack), stack.getCount());
        } else {
            extractItem(itemHandler, slot, itemHandler.getAmountAsInt(slot), false, null);
            ItemUtil.insertItemReturnRemaining(itemHandler, slot, stack, false, null);
        }
    }

    /**
     * 旧版ItemHandlerHelper#insertItemStacked的新实现
     */
    public static ItemStack insertItemStacked(ResourceHandler<ItemResource> itemHandler, ItemStack stack, boolean simulate, @Nullable TransactionContext parent) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try (Transaction tx = Transaction.open(parent)) {
            int inserted = ResourceHandlerUtil.insertStacking(itemHandler, ItemResource.of(stack), stack.getCount(), tx);
            if (!simulate) {
                tx.commit();
            }
            int leftover = stack.getCount() - inserted;
            return leftover == 0 ? ItemStack.EMPTY : stack.copyWithCount(leftover);
        }
    }

    /**
     * 旧版ItemStackHandler#extractItem的新实现
     */
    public static ItemStack extractItem(ResourceHandler<ItemResource> itemHandler, int index, int amount, boolean simulate, @Nullable TransactionContext parent) {
        try (Transaction tx = Transaction.open(parent)) {
            ItemResource resource = itemHandler.getResource(index);
            if (resource.isEmpty()) {
                return ItemStack.EMPTY;
            }
            int extracted = itemHandler.extract(index, resource, amount, tx);
            if (!simulate) {
                tx.commit();
            }
            return extracted == 0 ? ItemStack.EMPTY : resource.toStack(extracted);
        }
    }

    /**
     * 掉落指定起始、结束槽位的物品
     */
    public static void dropEntityItems(Entity entity, ResourceHandler<ItemResource> itemHandler, int startIndex, int endIndex, @Nullable TransactionContext parent) {
        for (int i = startIndex; i < endIndex; i++) {
            ItemStack stackInSlot = ItemUtil.getStack(itemHandler, i);
            ItemStack extractItem = extractItem(itemHandler, i, stackInSlot.getCount(), false, parent);
            if (!extractItem.isEmpty() && entity.level() instanceof ServerLevel serverLevel) {
                entity.spawnAtLocation(serverLevel, extractItem);
            }
        }
    }

    /**
     * 掉落指定起始的物品
     */
    public static void dropEntityItems(Entity entity, ResourceHandler<ItemResource> itemHandler, int startIndex, @Nullable TransactionContext parent) {
        dropEntityItems(entity, itemHandler, startIndex, itemHandler.size(), parent);
    }

    /**
     * 掉落全部物品
     */
    public static void dropEntityItems(Entity entity, ResourceHandler<ItemResource> itemHandler, @Nullable TransactionContext parent) {
        dropEntityItems(entity, itemHandler, 0, itemHandler.size(), parent);
    }

    /**
     * 传入 IItemHandler 和判定条件 filter，获取对应的格子数
     *
     * @return 如果没找到，返回 -1
     */
    public static int findStackSlot(ResourceHandler<ItemResource> handler, Predicate<ItemStack> filter) {
        return findStackSlot(handler, filter, -1);
    }

    /**
     * 如果传入的 handler 是 {@link MaidInvWrapper}，
     * 在物品栏中找不到时会触发 {@link MaidRequestItemEvent} 事件尝试从外部存储请求物品到物品栏，再次查找。
     *
     * @return 如果没找到，返回 -1
     */
    public static int findStackSlot(ResourceHandler<ItemResource> handler, Predicate<ItemStack> filter, int maxCount) {
        // 先正常在普通物品栏中查找
        for (int i = 0; i < handler.size(); i++) {
            ItemStack stack = ItemUtil.getStack(handler, i);
            if (filter.test(stack)) {
                return i;
            }
        }

        // 如果没找到，并且 handler 是 MaidInvWrapper，就触发事件请求物品后再找一次
        if (!(handler instanceof MaidInvWrapper maidInv) || maidInv.getMaid().level.isClientSide()) {
            return -1;
        }

        // 触发事件请求物品，让外部存储（如精妙背包等）把物品放到物品栏里
        MaidRequestItemEvent event = new MaidRequestItemEvent(maidInv.getMaid(), filter, maxCount);
        NeoForge.EVENT_BUS.post(event);
        ItemStack requested = event.getRequestedItem();
        // 如果请求的物品为空，说明没有外部存储提供物品，直接返回 -1
        if (requested.isEmpty()) {
            return -1;
        }

        // 再次查找物品栏，找到该物品
        for (int i = 0; i < handler.size(); i++) {
            ItemStack stack = ItemUtil.getStack(handler, i);
            if (filter.test(stack)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 获取符合条件的 slot 列表
     */
    public static List<Integer> getFilterStackSlots(ResourceHandler<ItemResource> handler, Predicate<ItemStack> filter) {
        IntList slots = new IntArrayList();
        for (int i = 0; i < handler.size(); i++) {
            ItemStack stack = ItemUtil.getStack(handler, i);
            if (filter.test(stack)) {
                slots.add(i);
            }
        }
        return slots;
    }

    /**
     * 符合 filter 条件的物品是否在 handler 中
     */
    public static boolean isStackIn(ResourceHandler<ItemResource> handler, Predicate<ItemStack> filter) {
        return findStackSlot(handler, filter) >= 0;
    }

    public static boolean isStackIn(EntityMaid maid, Predicate<ItemStack> filter) {
        return findStackSlot(maid.components.item.getAvailableInv(false), filter) >= 0;
    }

    /**
     * 获取符合 filter 添加的 ItemStack
     *
     * @return 如果该物品不存在，返回 ItemStack.EMPTY
     */
    public static ItemStack getStack(ResourceHandler<ItemResource> handler, Predicate<ItemStack> filter) {
        int slotIndex = findStackSlot(handler, filter);
        if (slotIndex >= 0) {
            return ItemUtil.getStack(handler, slotIndex);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack getStack(EntityMaid maid, Predicate<ItemStack> filter) {
        return getStack(maid.components.item.getAvailableInv(false), filter);
    }

    /**
     * 获取女仆饰品栏的饰品数据
     * <p>
     * 此方法为遍历查找，性能为 O(n)，不适合频繁调用
     *
     * @return 如果没找到，返回 -1
     */
    public static int getBaubleSlotInMaid(EntityMaid maid, IMaidBauble bauble) {
        BaubleItemHandler handler = maid.components.item.getMaidBauble();
        return handler.getBaubleSlot(bauble);
    }

    /**
     * 女仆是否拥有该饰品物品
     * <p>
     * 此方法采用了缓存机制，性能为 O(1)，适合频繁调用
     */
    @ApiStatus.AvailableSince("1.4.3")
    public static boolean hasBaubleItemInMaid(EntityMaid maid, Item bauble) {
        BaubleItemHandler handler = maid.components.item.getMaidBauble();
        return handler.containsItem(bauble);
    }

    /**
     * 女仆是否拥有该饰品物品
     * <p>
     * 此方法采用了缓存机制，性能为 O(1)，适合频繁调用
     */
    @ApiStatus.AvailableSince("1.4.3")
    public static boolean hasBaubleStackInMaid(EntityMaid maid, ItemStack bauble) {
        return hasBaubleItemInMaid(maid, bauble.getItem());
    }

    /**
     * 获取物品Id
     */
    public static String getItemId(Item item) {
        Identifier key = BuiltInRegistries.ITEM.getKey(item);
        Preconditions.checkNotNull(key);
        return key.toString();
    }


    /**
     * 获取物品
     */
    public static ItemStack getItemStack(String itemId) {
        Identifier resourceLocation = Identifier.parse(itemId);
        Item value = BuiltInRegistries.ITEM.getValue(resourceLocation);
        return new ItemStack(value);
    }

    public static void giveItemToMaid(EntityMaid maid, ItemStack itemStack) {
        var inv = maid.components.item.getAvailableInv(false);
        ItemStack stack = ItemsUtil.insertItemStacked(inv, itemStack, false, null);
        if (!stack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(maid.level(), maid.getX(), maid.getY() + 0.5, maid.getZ(), stack);
            maid.level.addFreshEntity(itemEntity);
        }
    }

    /**
     * 判断玩家主背包（包括快捷栏）能否插入物品
     *
     * @param player 要检查的玩家
     * @return 如果背包已满返回true，否则返回false
     */
    public static boolean canItemInsert(Player player, ItemStack testStack) {
        // 获取玩家主背包的物品处理器（与giveItemToPlayer使用相同的包装器）
        var inventory = PlayerInventoryWrapper.of(player).getMainSlots();

        // 遍历所有背包槽位
        for (int i = 0; i < inventory.size(); i++) {
            // 模拟插入物品（第三个参数为true表示仅测试，不实际修改物品栏）
            ItemStack remainder = ItemUtil.insertItemReturnRemaining(inventory, i, testStack, true, null);

            // 如果插入后没有剩余，说明该槽位可以容纳物品
            if (remainder.isEmpty()) {
                return true;
            }
        }

        // 所有槽位都无法容纳测试物品
        return false;
    }


    public static NonNullList<ItemStack> containerToItemList(Container container) {
        NonNullList<ItemStack> itemStack = NonNullList.create();
        for (int i = 0; i < container.getContainerSize(); i++) {
            itemStack.add(container.getItem(i));
        }
        return itemStack;
    }

    public static void fillContainerByItemList(Container container, List<ItemStack> itemStack) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            container.setItem(i, itemStack.get(i));
        }
    }


    public static void saveContainer(Container container, String key, ValueOutput valueOutput) {
        valueOutput.store(key, ItemStack.OPTIONAL_CODEC.listOf(), containerToItemList(container));
    }

    public static void loadContainer(Container container, String key, ValueInput valueInput) {
        valueInput.read("container", ItemStack.OPTIONAL_CODEC.listOf()).ifPresent(l -> fillContainerByItemList(container, l));
    }

    public static ItemStacksResourceHandler createDummyHandler(List<ItemStack> stack) {
        return new ItemStacksResourceHandler(NonNullList.copyOf(stack));
    }

    public static IndexModifier<ItemResource> createIndexModifier(ResourceHandler<ItemResource> handler) {
        return (index, resource, amount) ->
                setStackInSlot(handler, index, resource.isEmpty() || amount <= 0 ? ItemStack.EMPTY : resource.toStack(amount));
    }
}
