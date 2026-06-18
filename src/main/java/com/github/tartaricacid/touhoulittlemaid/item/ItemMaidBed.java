package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class ItemMaidBed extends BlockItem {
    public ItemMaidBed(Identifier id, Supplier<? extends Block> blockSupplier) {
        super(blockSupplier.get(), (new Item.Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        int flag = Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS;
        return context.getLevel().setBlock(context.getClickedPos(), state, flag);
    }
}