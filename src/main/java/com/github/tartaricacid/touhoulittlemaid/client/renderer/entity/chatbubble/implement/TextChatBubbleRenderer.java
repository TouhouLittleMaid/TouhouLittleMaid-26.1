package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.implement;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.EntityGraphics;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.IChatBubbleRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TextChatBubbleRenderer implements IChatBubbleRenderer {
    private static final int MAX_WIDTH = 240;
    private static final int MAX_CENTER_WIDTH = 480;

    private final List<FormattedCharSequence> split;
    private final Font font;
    private final int width;
    private final int height;
    private final ResourceLocation bg;

    public TextChatBubbleRenderer(Component text, ResourceLocation bg, IChatBubbleRenderer.Position position) {
        this.font = Minecraft.getInstance().font;
        int fontWidth = font.width(text);
        if (position == Position.CENTER) {
            this.split = font.split(text, MAX_CENTER_WIDTH);
            this.width = Math.min(fontWidth, MAX_CENTER_WIDTH);
        } else {
            this.split = font.split(text, MAX_WIDTH);
            this.width = Math.min(fontWidth, MAX_WIDTH);
        }
        this.height = split.size() * font.lineHeight;
        this.bg = bg;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void render(EntityMaidRenderer renderer, EntityGraphics graphics) {
        int y = 0;
        for (FormattedCharSequence sequence : this.split) {
            graphics.drawString(font, sequence, 0, y, 0x000000, false);
            y += font.lineHeight;
        }
    }

    @Override
    public ResourceLocation getBackgroundTexture() {
        return this.bg;
    }
}
