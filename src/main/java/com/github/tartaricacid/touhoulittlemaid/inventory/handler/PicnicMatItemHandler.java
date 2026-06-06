package com.github.tartaricacid.touhoulittlemaid.inventory.handler;

import net.minecraft.core.component.DataComponents;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class PicnicMatItemHandler extends ItemStacksResourceHandler {
    public PicnicMatItemHandler() {
        super(9);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        if (resource.isEmpty()) {
            return false;
        }
        return resource.test(stack -> stack.has(DataComponents.FOOD));
    }
}
