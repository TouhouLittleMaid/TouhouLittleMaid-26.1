package com.github.tartaricacid.touhoulittlemaid.inventory.container.other;

import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.WirelessIOItemHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.Nullable;

public class WirelessIOContainer extends AbstractContainerMenu {
    public static final MenuType<WirelessIOContainer> TYPE = IMenuTypeExtension.create(WirelessIOContainer::new);

    private static final int PLAYER_SLOT_COUNT = 36;
    private static final int FILTER_SLOT_COUNT = 9;

    private final ItemStack stack;
    private final WirelessIOItemHandler handler;

    public WirelessIOContainer(int id, Inventory inventory, @Nullable RegistryFriendlyByteBuf extraData) {
        super(TYPE, id);

        this.stack = inventory.player.getMainHandItem();
        this.handler = WirelessIOItemHandler.fromPlayer(inventory.player);

        this.addPlayerSlots(inventory);
        this.addWirelessIOSlots();
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return playerIn.getMainHandItem().is(InitItems.WIRELESS_IO);
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
        // 虚拟放入和放出，不消耗产生物品
        if (isFilterSlot(slotId)) {
            setFilterSlot(slotId - PLAYER_SLOT_COUNT, this.getCarried());
            return;
        }
        super.clicked(slotId, button, containerInput, player);
    }

    private void addWirelessIOSlots() {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                int index = col + row * 3;
                this.addSlot(new WirelessIOFilterSlot(handler, index, 62 + col * 18, 17 + row * 18));
            }
        }
    }

    private void addPlayerSlots(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    @SuppressWarnings("all")
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack2 = slot.getItem();
            if (isFilterSlot(index)) {
                setFilterSlot(index - PLAYER_SLOT_COUNT, ItemStack.EMPTY);
                return ItemStack.EMPTY;
            }
            if (index < PLAYER_SLOT_COUNT) {
                addFilterMarker(stack2);
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getStack() {
        return stack;
    }

    private boolean isFilterSlot(int slotId) {
        return slotId >= PLAYER_SLOT_COUNT && slotId < PLAYER_SLOT_COUNT + FILTER_SLOT_COUNT;
    }

    private void addFilterMarker(ItemStack stack) {
        for (int i = 0; i < FILTER_SLOT_COUNT; i++) {
            if (handler.getResource(i).isEmpty() && setFilterSlot(i, stack)) {
                return;
            }
        }
    }

    private boolean setFilterSlot(int index, ItemStack stack) {
        ItemResource resource = stack.isEmpty() ? ItemResource.EMPTY : ItemResource.of(stack);
        if (!resource.isEmpty() && !handler.isValid(index, resource)) {
            return false;
        }
        handler.setFilter(index, stack);
        this.slots.get(PLAYER_SLOT_COUNT + index).setChanged();
        return true;
    }

    private class WirelessIOFilterSlot extends ResourceHandlerSlot {
        private WirelessIOFilterSlot(WirelessIOItemHandler handler, int index, int xPosition, int yPosition) {
            super(handler, handler::set, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return handler.isValid(this.getSlotIndex(), ItemResource.of(stack));
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public void setByPlayer(ItemStack stack) {
            handler.setFilter(this.getSlotIndex(), stack);
            this.setChanged();
        }

        @Override
        public ItemStack remove(int amount) {
            handler.setFilter(this.getSlotIndex(), ItemStack.EMPTY);
            this.setChanged();
            return ItemStack.EMPTY;
        }
    }
}
