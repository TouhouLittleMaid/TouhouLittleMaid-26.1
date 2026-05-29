package com.github.tartaricacid.touhoulittlemaid.item.bauble;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IChestType;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidWirelessIOEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.inventory.chest.ChestManager;
import com.github.tartaricacid.touhoulittlemaid.item.ItemWirelessIO;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WirelessIOBauble implements IMaidBauble {
    private static final int SLOT_NUM = 38;

    @Nonnull
    public static ItemStack insertItemStacked(ResourceHandler<@NotNull ItemResource> inventory, @Nonnull ItemStack stack, boolean simulate, @Nullable List<Boolean> slotConfig) {
        if (stack.isEmpty()) {
            return stack;
        }
        if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate, slotConfig);
        }
        int sizeInventory = inventory.size();
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = ItemUtil.getStack(inventory, i);
            if (slotConfig != null && i < slotConfig.size() && slotConfig.get(i)) {
                continue;
            }
            if (ItemStack.isSameItemSameComponents(slot, stack) && !slot.isEmpty() && slot.isStackable()) {
                stack = ItemUtil.insertItemReturnRemaining(inventory, i, stack, simulate, null);
                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        if (!stack.isEmpty()) {
            for (int i = 0; i < sizeInventory; i++) {
                if (slotConfig != null && i < slotConfig.size() && slotConfig.get(i)) {
                    continue;
                }
                if (ItemUtil.getStack(inventory, i).isEmpty()) {
                    stack = ItemUtil.insertItemReturnRemaining(inventory, i, stack, simulate, null);
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return stack;
    }

    public static ItemStack insertItem(ResourceHandler<@NotNull ItemResource> dest, @Nonnull ItemStack stack, boolean simulate, @Nullable List<Boolean> slotConfig) {
        if (stack.isEmpty()) {
            return stack;
        }
        for (int i = 0; i < dest.size(); i++) {
            if (slotConfig != null && i < slotConfig.size() && slotConfig.get(i)) {
                continue;
            }
            stack = ItemUtil.insertItemReturnRemaining(dest, i, stack, simulate, null);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.tickCount % 100 == 0 && !maid.guiOpening) {
            BlockPos bindingPos = ItemWirelessIO.getBindingPos(baubleItem);
            if (bindingPos == null) {
                return;
            }
            float maxDistance = maid.getHomeRadius();
            if (maid.distanceToSqr(bindingPos.getX(), bindingPos.getY(), bindingPos.getZ()) > (maxDistance * maxDistance)) {
                return;
            }
            BlockEntity te = maid.level.getBlockEntity(bindingPos);
            if (te == null) {
                return;
            }
            for (IChestType type : ChestManager.getAllChestTypes()) {
                if (!type.isChest(te)) {
                    continue;
                }
                int openCount = type.getOpenCount(maid.level, bindingPos, te);
                if (openCount > 0) {
                    return;
                }
                var chestInv = maid.level.getCapability(Capabilities.Item.BLOCK, te.getBlockPos(), null);
                if (chestInv != null) {
                    var maidInv = maid.components().item.getAvailableInv(false);
                    boolean isMaidToChest = ItemWirelessIO.isMaidToChest(baubleItem);
                    boolean isBlacklist = ItemWirelessIO.isBlacklist(baubleItem);
                    List<Boolean> slotConfig = ItemWirelessIO.getSlotConfig(baubleItem);
                    List<Boolean> slotConfigData;
                    if (slotConfig != null) {
                        slotConfigData = new ArrayList<>(slotConfig);
                        slotConfigData.set(maidInv.size() - 2, slotConfig.get(SLOT_NUM - 2));
                        slotConfigData.set(maidInv.size() - 1, slotConfig.get(SLOT_NUM - 1));
                    } else {
                        slotConfigData = new ArrayList<>(Collections.nCopies(SLOT_NUM, false));
                    }
                    var filterList = ItemWirelessIO.getFilterList(maid.registryAccess(), baubleItem);

                    if (isMaidToChest) {
                        var event = new MaidWirelessIOEvent.MaidToChest(maid, maidInv, chestInv, filterList, isBlacklist, slotConfigData);
                        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
                            maidToChest(maidInv, chestInv, isBlacklist, filterList, slotConfigData);
                        }
                    } else {
                        var event = new MaidWirelessIOEvent.ChestToMaid(maid, maidInv, chestInv, filterList, isBlacklist, slotConfigData);
                        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
                            chestToMaid(chestInv, maidInv, isBlacklist, filterList, slotConfigData);
                        }
                    }
                }
                if (maid.getOwner() instanceof ServerPlayer serverPlayer) {
                    InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.USE_WIRELESS_IO);
                }
                return;
            }
        }
    }

    private void maidToChest(ResourceHandler<@NotNull ItemResource> maid, ResourceHandler<@NotNull ItemResource> chest, boolean isBlacklist, ResourceHandler<@NotNull ItemResource> filterList, List<Boolean> slotConfig) {
        for (int i = 0; i < maid.size(); i++) {
            if (i < slotConfig.size() && slotConfig.get(i)) {
                continue;
            }
            ItemStack maidInvItem = ItemUtil.getStack(maid, i);
            boolean allowMove = isBlacklist;
            for (int j = 0; j < filterList.size(); j++) {
                ItemStack filterItem = ItemUtil.getStack(filterList, j);
                boolean isEqual = ItemStack.isSameItem(maidInvItem, filterItem);
                if (isEqual) {
                    allowMove = !isBlacklist;
                    break;
                }
            }
            if (allowMove) {
                int beforeCount = maidInvItem.getCount();
                ItemStack after = ItemUtil.insertItemReturnRemaining(chest, maidInvItem.copy(), false, null);
                int afterCount = after.getCount();
                // Sync Client & Server
                if (beforeCount != afterCount) {
                    ItemsUtil.extractItem(maid, i, beforeCount - afterCount, false, null);
                }
            }
        }
    }

    private void chestToMaid(ResourceHandler<@NotNull ItemResource> chest, ResourceHandler<@NotNull ItemResource> maid, boolean isBlacklist, ResourceHandler<@NotNull ItemResource> filterList, List<Boolean> slotConfig) {
        for (int i = 0; i < chest.size(); i++) {
            ItemStack chestInvStack = ItemUtil.getStack(chest, i);
            boolean allowMove = isBlacklist;
            for (int j = 0; j < filterList.size(); j++) {
                ItemStack filterItem = ItemUtil.getStack(filterList, j);
                boolean isEqual = ItemStack.isSameItem(chestInvStack, filterItem);
                if (isEqual) {
                    allowMove = !isBlacklist;
                    break;
                }
            }
            if (allowMove) {
                int beforeCount = chestInvStack.getCount();
                ItemStack after = insertItemStacked(maid, chestInvStack.copy(), false, slotConfig);
                int afterCount = after.getCount();
                // Sync Client & Server
                if (beforeCount != afterCount) {
                    ItemsUtil.extractItem(chest, i, beforeCount - afterCount, false, null);
                }
            }
        }
    }
}
