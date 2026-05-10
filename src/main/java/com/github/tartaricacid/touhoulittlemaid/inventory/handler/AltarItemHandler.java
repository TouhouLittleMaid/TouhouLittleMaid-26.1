package com.github.tartaricacid.touhoulittlemaid.inventory.handler;

import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;

public class AltarItemHandler extends ItemStacksResourceHandler {
    public AltarItemHandler() {
        super(1);
    }

    @Override
    protected int getCapacity(int index, @NotNull ItemResource resource) {
        return 1;
    }
}
