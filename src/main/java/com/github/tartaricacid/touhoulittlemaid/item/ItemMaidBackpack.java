package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ItemMaidBackpack extends Item {
    public ItemMaidBackpack(Identifier id) {
        super((new Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }
}
