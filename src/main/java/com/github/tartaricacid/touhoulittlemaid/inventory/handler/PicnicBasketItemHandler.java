package com.github.tartaricacid.touhoulittlemaid.inventory.handler;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class PicnicBasketItemHandler extends ItemAccessItemHandler {
    private static final int PICNIC_BASKET_SIZE = 9;

    public PicnicBasketItemHandler(ItemAccess itemAccess) {
        super(itemAccess, DataComponents.CONTAINER, PICNIC_BASKET_SIZE);
    }

    public static PicnicBasketItemHandler fromStack(ItemStack stack) {
        ItemAccess access = ItemAccess.forStack(stack);
        return new PicnicBasketItemHandler(access);
    }

    public static PicnicBasketItemHandler fromPlayer(Player player) {
        ItemAccess access = ItemAccess.forPlayerSlot(player, player.getInventory().getSelectedSlot());
        return new PicnicBasketItemHandler(access);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return super.isValid(index, resource) && resource.test(stack -> stack.has(DataComponents.FOOD));
    }

    public void set(int index, ItemResource resource, int amount) {
        try (Transaction tx = Transaction.openRoot()) {
            ItemResource currentResource = this.getResource(index);
            int currentAmount = this.getAmountAsInt(index);
            if (currentAmount > 0) {
                this.extract(index, currentResource, currentAmount, tx);
            }
            if (!resource.isEmpty() && amount > 0) {
                this.insert(index, resource, amount, tx);
            }
            tx.commit();
        }
    }
}
