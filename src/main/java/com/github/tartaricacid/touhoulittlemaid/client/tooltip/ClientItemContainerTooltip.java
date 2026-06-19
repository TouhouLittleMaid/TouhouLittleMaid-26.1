package com.github.tartaricacid.touhoulittlemaid.client.tooltip;

import com.github.tartaricacid.touhoulittlemaid.inventory.tooltip.ItemContainerTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ClientItemContainerTooltip implements ClientTooltipComponent {
    private final ItemContainerContents contents;

    public ClientItemContainerTooltip(ItemContainerTooltip tooltip) {
        this.contents = tooltip.contents();
    }

    @Override
    public int getHeight(Font font) {
        return 20;
    }

    @Override
    public int getWidth(Font font) {
        return contents.getSlots() * 20;
    }

    @Override
    public void extractImage(Font font, int pX, int pY, int w, int h, GuiGraphicsExtractor graphics) {
        for (int i = 0; i < contents.getSlots(); i++) {
            ItemStack stack = contents.getStackInSlot(i);
            int xOffset = pX + i * 20;
            graphics.item(stack, xOffset, pY);
            graphics.itemDecorations(font, stack, xOffset, pY);
        }
    }
}
