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
public class ItemAdvancementIcon extends Item {
    public ItemAdvancementIcon(Identifier id) {
        super(new Properties()
                .stacksTo(1)
                .setId(ResourceKey.create(Registries.ITEM, id))
                .overrideDescription("item.touhou_little_maid.advancement_icon"));
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, TooltipDisplay display,
                                Consumer<Component> components, TooltipFlag pIsAdvanced) {
        components.accept(Component
                .translatable("tooltips.touhou_little_maid.advancement_icon.desc")
                .withStyle(ChatFormatting.GRAY)
        );
    }
}