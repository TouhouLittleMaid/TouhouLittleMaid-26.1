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

import javax.annotation.Nullable;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ItemChairShow extends Item {
    public ItemChairShow(Identifier id) {
        super((new Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }


    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Item.TooltipContext pLevel, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag pIsAdvanced) {
        tooltip.accept(Component
                .translatable("tooltips.touhou_little_maid.chair_show.desc")
                .withStyle(ChatFormatting.GRAY)
        );
    }
}