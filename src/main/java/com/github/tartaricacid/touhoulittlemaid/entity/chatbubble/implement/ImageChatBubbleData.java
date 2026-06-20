package com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.IChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.implement.ImageChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.IChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class ImageChatBubbleData implements IChatBubbleData {
    public static final Identifier ID = IdentifierUtil.modLoc("image");

    private final int existTick;
    private final Identifier bg;
    private final Identifier image;
    private final int width;
    private final int height;
    private final int uOffset;
    private final int vOffset;
    private final int textureWidth;
    private final int textureHeight;
    private final int priority;

    private IChatBubbleRenderer renderer;

    private ImageChatBubbleData(
            int existTick, Identifier bg, Identifier image, int width, int height,
            int uOffset, int vOffset, int textureWidth, int textureHeight, int priority
    ) {
        this.existTick = existTick;
        this.bg = bg;
        this.image = image;
        this.width = width;
        this.height = height;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.priority = priority;
    }

    public static ImageChatBubbleData create(Identifier image, int width, int height) {
        return new ImageChatBubbleData(
                DEFAULT_EXIST_TICK, TYPE_2, image, width, height,
                0, 0, 256, 256, DEFAULT_PRIORITY
        );
    }

    public static ImageChatBubbleData create(Identifier image, int width, int height, int uOffset, int vOffset) {
        return new ImageChatBubbleData(
                DEFAULT_EXIST_TICK, TYPE_2, image, width, height,
                uOffset, vOffset, 256, 256, DEFAULT_PRIORITY
        );
    }

    public static ImageChatBubbleData singleImage(Identifier image, int width, int height) {
        return new ImageChatBubbleData(
                DEFAULT_EXIST_TICK, TYPE_2, image, width, height,
                0, 0, width, height, DEFAULT_PRIORITY
        );
    }

    public static ImageChatBubbleData create(
            int existTick, Identifier bg, Identifier image, int width, int height,
            int uOffset, int vOffset, int textureWidth, int textureHeight, int priority
    ) {
        return new ImageChatBubbleData(
                existTick, bg, image, width, height,
                uOffset, vOffset, textureWidth, textureHeight, priority
        );
    }

    @Override
    public int existTick() {
        return this.existTick;
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public IChatBubbleRenderer getRenderer(IChatBubbleRenderer.Position position) {
        if (this.renderer == null) {
            this.renderer = new ImageChatBubbleRenderer(
                    this.width, this.height, this.uOffset, this.vOffset,
                    this.textureWidth, this.textureHeight, this.bg, this.image
            );
        }
        return this.renderer;
    }

    public static class ImageChatSerializer implements IChatBubbleData.ChatSerializer {
        @Override
        public IChatBubbleData readFromBuff(FriendlyByteBuf buf) {
            int width = buf.readVarInt();
            int height = buf.readVarInt();
            int uOffset = buf.readVarInt();
            int vOffset = buf.readVarInt();
            int textureWidth = buf.readVarInt();
            int textureHeight = buf.readVarInt();
            Identifier bg = buf.readIdentifier();
            Identifier image = buf.readIdentifier();
            return new ImageChatBubbleData(
                    DEFAULT_EXIST_TICK, bg, image, width, height,
                    uOffset, vOffset, textureWidth, textureHeight, DEFAULT_PRIORITY
            );
        }

        @Override
        public void writeToBuff(FriendlyByteBuf buf, IChatBubbleData data) {
            ImageChatBubbleData imageChat = (ImageChatBubbleData) data;
            buf.writeVarInt(imageChat.width);
            buf.writeVarInt(imageChat.height);
            buf.writeVarInt(imageChat.uOffset);
            buf.writeVarInt(imageChat.vOffset);
            buf.writeVarInt(imageChat.textureWidth);
            buf.writeVarInt(imageChat.textureHeight);
            buf.writeIdentifier(imageChat.bg);
            buf.writeIdentifier(imageChat.image);
        }
    }
}
