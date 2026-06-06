package com.github.tartaricacid.touhoulittlemaid.inventory.handler;

import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class ShrineItemHandler extends ItemStacksResourceHandler {
    public ShrineItemHandler() {
        super(1);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return resource.is(InitItems.FILM.get());
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return 1;
    }
}
