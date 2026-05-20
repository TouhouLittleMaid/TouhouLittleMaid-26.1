package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ItemScarecrow extends BlockItem {
    public ItemScarecrow(Identifier id) {
        super(InitBlocks.SCARECROW.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        int range = MiscConfig.SCARECROW_RANGE.get();
        tooltip.accept(Component.translatable("tooltips.touhou_little_maid.scarecrow.desc", range, range).withStyle(ChatFormatting.GRAY));
    }
}
