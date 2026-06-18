package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ItemEntityIdCopy extends Item {
    public ItemEntityIdCopy(Identifier id) {
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, TooltipDisplay display,
                                Consumer<Component> components, TooltipFlag pIsAdvanced) {
        components.accept(Component
                .translatable("tooltips.touhou_little_maid.entity_id_copy.desc")
                .withStyle(ChatFormatting.GRAY)
        );
    }
}
