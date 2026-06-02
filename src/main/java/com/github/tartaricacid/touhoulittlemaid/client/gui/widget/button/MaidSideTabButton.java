package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
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
    private static final Identifier SIDE = IdentifierUtil.modLoc("textures/gui/maid_gui_side.png");
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
            GuiTools.guiBlit(graphics, SIDE, this.getX() + 2, this.getY(), 209, top, this.width, this.height);
        }
        GuiTools.guiBlit(graphics, SIDE, this.getX() + 6, this.getY() + 4, 193, top + 4, 16, 16);
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