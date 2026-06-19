package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemNormalBauble extends Item {
    public ItemNormalBauble(Identifier id) {
        super(new Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
