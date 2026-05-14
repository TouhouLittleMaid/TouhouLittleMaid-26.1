package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.client.gui.ITooltipButton;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * 女仆界面侧边栏按钮
 */
public class MaidSideTabButton extends Button implements ITooltipButton {
    private static final Identifier SIDE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_side.png");
    private static final int V_OFFSET = 107;
    private final List<Component> tooltips;
    private final int top;

    public MaidSideTabButton(int x, int y, int top, OnPress onPressIn, List<Component> tooltips) {
        super(Button.builder(Component.empty(), onPressIn).pos(x, y).size(26, 24));
        this.top = V_OFFSET + top;
        this.tooltips = tooltips;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.active) {
            GuiTools.blit(graphics, SIDE, this.getX() + 2, this.getY(), this.width, this.height, 209, top, this.width, this.height, 256, 256);
        }
        GuiTools.blit(graphics, SIDE, this.getX() + 6, this.getY() + 4, 16, 16, 193, top + 4, 16, 16, 256, 256);
    }

    @Override
    public boolean isTooltipHovered() {
        return this.isHovered();
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor graphics, Minecraft mc, int mouseX, int mouseY) {
        graphics.setTooltipForNextFrame(mc.font, this.tooltips, Optional.empty(), mouseX, mouseY);
    }
}