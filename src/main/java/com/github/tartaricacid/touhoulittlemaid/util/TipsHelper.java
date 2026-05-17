package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

public final class TipsHelper {
    private static final Identifier BUTTON = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_button.png");

    public static void renderTips(GuiGraphicsExtractor graphics, Button button, Component text) {
        if (text.equals(Component.empty())) {
            return;
        }
        graphics.nextStratum();

        int xOffset = button.getX() + button.getWidth() - 8;
        int yOffset = button.getY() - 12;
        long number = (System.currentTimeMillis() / 400) % 2;
        if (number == 1) {
            yOffset += 1;
        }

        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> split = font.split(text, 124);
        int size = split.size();

        if (size == 1) {
            int textWidth = Mth.clamp(font.width(text) + 26, 40, 150);
            blitButton(graphics, xOffset, yOffset, 0, 128, 10, 20);
            int times = (textWidth - 20) / 20;
            int startX = xOffset + 10;
            for (int i = 0; i < times; i++) {
                blitButton(graphics, startX, yOffset, 10, 128, 20, 20);
                startX = startX + 20;
            }
            int last = textWidth - times * 20 - 20;
            blitButton(graphics, startX, yOffset, 10, 128, last, 20);
            blitButton(graphics, xOffset + textWidth - 10, yOffset, 30, 128, 10, 20);
            blitButton(graphics, xOffset + textWidth - 20, yOffset - 2, 42, 128, 16, 16);
            graphics.text(font, text, xOffset + 5, yOffset + 4, 0xFF_FF_FF_55, false);
        }

        if (size == 2) {
            yOffset = yOffset - 10;
            int textWidth = font.width(split.get(0)) + 26;
            blitButton(graphics, xOffset, yOffset, 0, 149, 10, 30);
            int times = (textWidth - 20) / 20;
            int startX = xOffset + 10;
            for (int i = 0; i < times; i++) {
                blitButton(graphics, startX, yOffset, 10, 149, 20, 30);
                startX = startX + 20;
            }
            int last = textWidth - times * 20 - 20;
            blitButton(graphics, startX, yOffset, 10, 149, last, 30);
            blitButton(graphics, xOffset + textWidth - 10, yOffset, 30, 149, 10, 30);
            blitButton(graphics, xOffset + textWidth - 20, yOffset + 5, 42, 128, 16, 16);
            graphics.text(font, split.get(0), xOffset + 5, yOffset + 4, 0xFF_FF_FF_55, false);
            graphics.text(font, split.get(1), xOffset + 5, yOffset + 14, 0xFF_FF_FF_55, false);
        }

        if (size >= 3) {
            yOffset = yOffset - 20;
            int textWidth = font.width(split.get(0)) + 26;
            blitButton(graphics, xOffset, yOffset, 0, 180, 10, 40);
            int times = (textWidth - 20) / 20;
            int startX = xOffset + 10;
            for (int i = 0; i < times; i++) {
                blitButton(graphics, startX, yOffset, 10, 180, 20, 40);
                startX = startX + 20;
            }
            int last = textWidth - times * 20 - 20;
            blitButton(graphics, startX, yOffset, 10, 180, last, 40);
            blitButton(graphics, xOffset + textWidth - 10, yOffset, 30, 180, 10, 40);
            blitButton(graphics, xOffset + textWidth - 20, yOffset + 10, 42, 128, 16, 16);
            graphics.text(font, split.get(0), xOffset + 5, yOffset + 4, 0xFF_FF_FF_55, false);
            graphics.text(font, split.get(1), xOffset + 5, yOffset + 14, 0xFF_FF_FF_55, false);
            graphics.text(font, split.get(2), xOffset + 5, yOffset + 24, 0xFF_FF_FF_55, false);
        }
    }

    private static void blitButton(GuiGraphicsExtractor graphics, int x, int y, int u, int v, int w, int h) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BUTTON, x, y, (float) u, (float) v, w, h, 256, 256);
    }
}
