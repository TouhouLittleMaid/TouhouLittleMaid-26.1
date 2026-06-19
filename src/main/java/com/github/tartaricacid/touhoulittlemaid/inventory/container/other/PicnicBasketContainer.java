package com.github.tartaricacid.touhoulittlemaid.inventory.container.other;

import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.PicnicBasketItemHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.Nullable;

public class PicnicBasketContainer extends AbstractContainerMenu {
    public static final MenuType<PicnicBasketContainer> TYPE = IMenuTypeExtension.create(PicnicBasketContainer::new);

    public PicnicBasketContainer(int id, Inventory inventory, @Nullable RegistryFriendlyByteBuf extraData) {
        super(TYPE, id);
        this.addPicnicInventory(inventory);
        this.addPlayerInventory(inventory);
    }

    private void addPicnicInventory(Inventory inventory) {
        var container = PicnicBasketItemHandler.fromPlayer(inventory.player);
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new ResourceHandlerSlot(container, container::set, i, 8 + i * 18, 18));
        }
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 49 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 107));
        }
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput containerInput, Player player) {
        // 禁阻一切对当前手持物品的交互，防止刷物品 bug
        if (slotId == 36 + player.getInventory().getSelectedSlot()) {
            return;
        }
        if (containerInput == ContainerInput.SWAP) {
            return;
        }
        super.clicked(slotId, button, containerInput, player);
    }

    @Override
    @SuppressWarnings("all")
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack output = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            output = stack.copy();

            if (index < 9) {
                if (!this.moveItemStackTo(stack, 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return output;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem().is(InitItems.PICNIC_BASKET);
    }
}
