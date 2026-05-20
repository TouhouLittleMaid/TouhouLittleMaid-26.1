package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ItemExtinguisher extends Item {
    public ItemExtinguisher(Identifier id) {
        super((new Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .durability(128));
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (worldIn.isClientSide()) {
            playerIn.sendSystemMessage(Component.translatable("message.touhou_little_maid.extinguisher.player_cannot_use"));
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
