package com.github.tartaricacid.touhoulittlemaid.compat.sbackpack.curios;

import com.github.tartaricacid.touhoulittlemaid.compat.extracontainer.curios.CuriosSlotRef;
import com.github.tartaricacid.touhoulittlemaid.compat.sbackpack.SBackpackCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.ITrackedContentsItemHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;

import java.util.Set;
import java.util.function.Predicate;

public class SBackpackSlotRef extends CuriosSlotRef {
    public SBackpackSlotRef(String slotType, int slotIndex) {
        super(slotType, slotIndex);
    }

    public ItemStack getBackpackStack(EntityMaid maid) {
        ItemStack stack = getCuriosStack(maid);
        if (SBackpackCompat.isBackpack(stack)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean containing(EntityMaid maid, ItemStack itemToCheck) {
        ItemStack backpackStack = getBackpackStack(maid);
        if (backpackStack.isEmpty()) {
            return false;
        }
        var wrapper = BackpackWrapper.fromStack(backpackStack);
        ITrackedContentsItemHandler inv = wrapper.getInventoryForUpgradeProcessing();
        Set<ItemStackKey> trackedStacks = inv.getTrackedStacks();
        return trackedStacks.stream().anyMatch(key ->
                ItemStack.isSameItemSameComponents(key.getStack(), itemToCheck));
    }

    @Override
    public ItemStack insert(EntityMaid maid, ItemStack itemStack, boolean simulate) {
        ItemStack backpackStack = getBackpackStack(maid);
        if (backpackStack.isEmpty()) {
            return itemStack;
        }
        var wrapper = BackpackWrapper.fromStack(backpackStack);
        ITrackedContentsItemHandler inv = wrapper.getInventoryForUpgradeProcessing();
        return ItemHandlerHelper.insertItemStacked(inv, itemStack, simulate);
    }

    @Override
    public ItemStack extract(EntityMaid maid, Predicate<ItemStack> filter, int maxCount) {
        ItemStack backpackStack = getBackpackStack(maid);
        if (backpackStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        var wrapper = BackpackWrapper.fromStack(backpackStack);
        ITrackedContentsItemHandler inv = wrapper.getInventoryForUpgradeProcessing();
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stackInSlot = inv.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || !filter.test(stackInSlot)) {
                continue;
            }

            int itemMaxStack = stackInSlot.getMaxStackSize();
            int effectiveMaxCount = (maxCount == -1)
                    ? itemMaxStack
                    : Math.min(maxCount, itemMaxStack);
            int extractCount = Math.min(effectiveMaxCount, stackInSlot.getCount());
            return inv.extractItem(slot, extractCount, false);
        }
        return ItemStack.EMPTY;
    }
}
