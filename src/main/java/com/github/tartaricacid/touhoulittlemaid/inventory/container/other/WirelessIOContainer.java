package com.github.tartaricacid.touhoulittlemaid.inventory.container.other;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemWirelessIO;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;

public class WirelessIOContainer extends AbstractContainerMenu {
    public static final MenuType<WirelessIOContainer> TYPE = IMenuTypeExtension.create((windowId, inv, data) -> new WirelessIOContainer(windowId, inv, ItemStack.STREAM_CODEC.decode(data)));
    private final ItemStack wirelessIO;
    private final ItemStacksResourceHandler filterListInv;

    public WirelessIOContainer(int id, Inventory inventory, ItemStack wirelessIO) {
        super(TYPE, id);
        this.wirelessIO = wirelessIO;
        this.filterListInv = ItemWirelessIO.getFilterList(inventory.player.registryAccess(), wirelessIO);
        this.addPlayerSlots(inventory);
        this.addWirelessIOSlots();
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return playerIn.getMainHandItem().getItem() == InitItems.WIRELESS_IO.get();
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput containerInput, Player player) {
        // 禁阻一切对当前手持物品的交互，防止刷物品 bug
        if (slotId == 27 + player.getInventory().getSelectedSlot()) {
            return;
        }
        if (containerInput == ContainerInput.SWAP) {
            return;
        }
        super.clicked(slotId, button, containerInput, player);
        ItemWirelessIO.setFilterList(player.registryAccess(), wirelessIO, filterListInv);
    }

    private void addWirelessIOSlots() {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new WirelessIOSlotResourceHandler(filterListInv, filterListInv::set, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
    }

    private void addPlayerSlots(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack1 = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack2 = slot.getItem();
            stack1 = stack2.copy();
            if (index < 27) {
                if (!this.moveItemStackTo(stack2, 27, 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack2, 0, 27, false)) {
                return ItemStack.EMPTY;
            }
            if (stack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack1;
    }

    public ItemStack getWirelessIO() {
        return wirelessIO;
    }

    private class WirelessIOSlotResourceHandler extends ResourceHandlerSlot {
        private WirelessIOSlotResourceHandler(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition) {
            super(handler, slotModifier, index, xPosition, yPosition);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return EntityMaid.canInsertItem(stack) && super.mayPlace(stack);
        }
    }
}
