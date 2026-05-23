package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.client.gui.ITooltipButton;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public class MaidTabButton extends Button implements ITooltipButton {
    private static final Identifier SIDE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_side.png");
    private final int left;
    private final List<Component> tooltips;

    public MaidTabButton(int x, int y, int left, String key, Button.OnPress onPressIn) {
        super(Button.builder(Component.empty(), onPressIn).pos(x, y).size(24, 26));
        this.left = left;
        this.tooltips = Lists.newArrayList(
                Component.translatable("gui.touhou_little_maid.button." + key),
                Component.translatable("gui.touhou_little_maid.button." + key + ".desc")
        );
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.active) {
            GuiTools.guiBlit(graphics, SIDE, this.getX(), this.getY(), left, 21, this.width, this.height);
        }
        GuiTools.guiBlit(graphics, SIDE, this.getX() + 4, this.getY() + 6, left, 47, 16, 16);
    }

    @Override
    public boolean isTooltipHovered() {
        return this.active && this.isHovered();
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor graphics, Minecraft mc, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        graphics.setTooltipForNextFrame(font, tooltips, Optional.empty(), mouseX, mouseY);
    }
}