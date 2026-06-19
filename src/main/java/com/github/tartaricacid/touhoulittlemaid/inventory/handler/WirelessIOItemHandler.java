package com.github.tartaricacid.touhoulittlemaid.inventory.handler;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidItemManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class WirelessIOItemHandler extends ItemAccessItemHandler {
    public static final int FILTER_LIST_SIZE = 9;

    public WirelessIOItemHandler(ItemAccess itemAccess) {
        super(itemAccess, DataComponents.CONTAINER, FILTER_LIST_SIZE);
    }

    public static WirelessIOItemHandler fromStack(ItemStack stack) {
        ItemAccess access = ItemAccess.forStack(stack);
        return new WirelessIOItemHandler(access);
    }

    public static WirelessIOItemHandler fromPlayer(Player player) {
        ItemAccess access = ItemAccess.forPlayerSlot(player, player.getInventory().getSelectedSlot());
        return new WirelessIOItemHandler(access);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return 1;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return super.isValid(index, resource) && resource.test(MaidItemManager::canInsertItem);
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

    public void setFilter(int index, ItemStack stack) {
        ItemResource resource = stack.isEmpty() ? ItemResource.EMPTY : ItemResource.of(stack.copyWithCount(1));
        this.set(index, resource, resource.isEmpty() ? 0 : 1);
    }
}
