package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ItemFavorabilityTool extends Item {
    private final String type;

    public ItemFavorabilityTool(Identifier id, String type) {
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1)
                .rarity(Rarity.EPIC));
        this.type = type;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Item.TooltipContext pLevel, TooltipDisplay display,
                                Consumer<Component> components, TooltipFlag pIsAdvanced) {
        String key = "tooltips.touhou_little_maid.favorability_tool.%s".formatted(this.type);
        components.accept(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }
}
