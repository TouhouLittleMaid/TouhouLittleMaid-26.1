package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.implement;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.EntityGraphics;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.IChatBubbleRenderer;
import net.minecraft.resources.Identifier;

public class ImageChatBubbleRenderer implements IChatBubbleRenderer {
    private final int width;
    private final int height;
    private final int uOffset;
    private final int vOffset;
    private final int textureWidth;
    private final int textureHeight;
    private final Identifier bg;
    private final Identifier image;

    public ImageChatBubbleRenderer(int width, int height, int uOffset, int vOffset, int textureWidth,
                                   int textureHeight, Identifier bg, Identifier image) {
        this.width = width;
        this.height = height;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.bg = bg;
        this.image = image;
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
        graphics.blit(this.image, 0, 0, this.uOffset, this.vOffset, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    @Override
    public Identifier getBackgroundTexture() {
        return this.bg;
    }
}
