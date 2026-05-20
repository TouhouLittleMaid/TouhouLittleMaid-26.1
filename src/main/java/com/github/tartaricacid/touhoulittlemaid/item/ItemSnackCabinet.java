package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ItemSnackCabinet extends BlockItem {
    public ItemSnackCabinet() {
        super(InitBlocks.SNACK_CABINET.get(), new Item.Properties());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("block.touhou_little_maid.snack_cabinet.tip").withStyle(ChatFormatting.GRAY));
    }
}
