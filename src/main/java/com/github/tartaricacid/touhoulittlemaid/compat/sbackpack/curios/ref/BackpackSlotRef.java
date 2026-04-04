package com.github.tartaricacid.touhoulittlemaid.compat.sbackpack.curios.ref;

import com.github.tartaricacid.touhoulittlemaid.compat.sbackpack.SBackpackCompat;
import com.github.tartaricacid.touhoulittlemaid.compat.sbackpack.curios.SBackpackCuriosCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.ITrackedContentsItemHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Set;

public class BackpackSlotRef implements ContainerRef {
    public final String slotType;
    public final int slotIndex;
    public final int priority;
    private final EntityMaid maid;

    public BackpackSlotRef(EntityMaid maid, String slotType, int slotIndex) {
        this.maid = maid;
        this.slotType = slotType;
        this.slotIndex = slotIndex;
        this.priority = SBackpackCuriosCompat.getSlotPriority(slotType);
    }

    public ItemStack getBackpackStack() {
        var inventory = CuriosApi.getCuriosInventory(maid);
        return inventory.map(handler -> handler.getStacksHandler(slotType)
                .map(stacksHandler -> {
                    IDynamicStackHandler stacks = stacksHandler.getStacks();
                    if (slotIndex >= stacks.getSlots()) {
                        return ItemStack.EMPTY;
                    }

                    ItemStack stack = stacks.getStackInSlot(slotIndex);
                    if (SBackpackCompat.isBackpack(stack)) {
                        return stack;
                    }

                    return ItemStack.EMPTY;
                }).orElse(ItemStack.EMPTY)
        ).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean containing(ItemStack itemToCheck) {
        ItemStack backpackStack = getBackpackStack();
        if (backpackStack.isEmpty()) {
            return false;
        }
        var wrapper = BackpackWrapper.fromStack(backpackStack);
        ITrackedContentsItemHandler inv = wrapper.getInventoryForUpgradeProcessing();
        Set<ItemStackKey> trackedStacks = inv.getTrackedStacks();
        return trackedStacks.stream()
                .anyMatch(key -> ItemStack.isSameItemSameComponents(key.getStack(), itemToCheck));
    }

    @Override
    public ItemStack insert(ItemStack itemstack, boolean simulate) {
        ItemStack backpackStack = getBackpackStack();
        if (backpackStack.isEmpty()) {
            return itemstack;
        }
        var wrapper = BackpackWrapper.fromStack(backpackStack);
        ITrackedContentsItemHandler inv = wrapper.getInventoryForUpgradeProcessing();
        return ItemHandlerHelper.insertItemStacked(inv, itemstack, simulate);
    }

    public int compareTo(BackpackSlotRef other) {
        int priorityCompare = Integer.compare(this.priority, other.priority);
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        return Integer.compare(this.slotIndex, other.slotIndex);
    }
}
