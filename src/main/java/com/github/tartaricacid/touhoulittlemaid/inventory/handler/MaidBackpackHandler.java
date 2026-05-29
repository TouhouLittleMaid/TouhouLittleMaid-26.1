package com.github.tartaricacid.touhoulittlemaid.inventory.handler;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl.MaidItemComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import javax.annotation.Nonnull;

public class MaidBackpackHandler extends ItemStacksResourceHandler {
    public static final int BACKPACK_ITEM_SLOT = 5;
    private final EntityMaid maid;

    public MaidBackpackHandler(int size, EntityMaid maid) {
        super(size);
        this.maid = maid;
    }

    @Override
    public boolean isValid(int slot, @Nonnull ItemResource resource) {
        return MaidItemComponent.canInsertItem(resource.toStack());
    }

    @Override
    protected void onContentsChanged(int slot, @Nonnull ItemStack previousStack) {
        if (slot == BACKPACK_ITEM_SLOT) {
            maid.setBackpackShowItem(ItemUtil.getStack(this, slot));
        }
    }
}
