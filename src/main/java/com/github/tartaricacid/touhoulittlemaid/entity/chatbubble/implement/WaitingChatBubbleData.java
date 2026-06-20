package com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.IChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.implement.WaitingChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.IChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public class WaitingChatBubbleData implements IChatBubbleData {
    public static final Identifier ID = IdentifierUtil.modLoc("waiting");

    private final int existTick;
    private final Identifier bg;
    private final int priority;
    private final Component text;
    private final @Nullable Component secondaryText;
    private final Identifier icon;

    private IChatBubbleRenderer renderer;

    private WaitingChatBubbleData(
            int existTick, Identifier bg, int priority, Component text,
            @Nullable Component secondaryText, Identifier icon
    ) {
        this.existTick = existTick;
        this.bg = bg;
        this.priority = priority;
        this.text = text;
        this.secondaryText = secondaryText;
        this.icon = icon;
    }

    public static WaitingChatBubbleData create(
            int existTick, Identifier bg, int priority, Component text, Identifier icon
    ) {
        return new WaitingChatBubbleData(
                existTick, bg, priority, text, null, icon
        );
    }

    public static WaitingChatBubbleData create(
            int existTick, Identifier bg, int priority, Component text,
            @Nullable Component secondaryText, Identifier icon
    ) {
        return new WaitingChatBubbleData(existTick, bg, priority, text, secondaryText, icon);
    }

    public static WaitingChatBubbleData create(Component text, Identifier icon) {
        return new WaitingChatBubbleData(
                DEFAULT_EXIST_TICK, TYPE_2, DEFAULT_PRIORITY, text, null, icon
        );
    }

    public static WaitingChatBubbleData create(
            Component text, @Nullable Component secondaryText, Identifier icon
    ) {
        return new WaitingChatBubbleData(
                DEFAULT_EXIST_TICK, TYPE_2, DEFAULT_PRIORITY, text, secondaryText, icon
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
        if (renderer == null) {
            renderer = new WaitingChatBubbleRenderer(
                    this.bg, this.text, this.secondaryText, this.icon
            );
        }
        return renderer;
    }

    @Nullable
    public Component getSecondaryText() {
        return secondaryText;
    }

    public static class WaitingChatSerializer implements IChatBubbleData.ChatSerializer {
        @Override
        public IChatBubbleData readFromBuff(FriendlyByteBuf buf) {
            // 往客户端同步的数据里，不需要同步 existTick 和 priority，这两个数据仅在服务端有效
            Identifier bg = buf.readIdentifier();
            Component text = buf.readLenientJsonWithCodec(ComponentSerialization.CODEC);
            Component secondaryText = null;
            if (buf.readBoolean()) {
                secondaryText = buf.readLenientJsonWithCodec(ComponentSerialization.CODEC);
            }
            return new WaitingChatBubbleData(
                    DEFAULT_EXIST_TICK, bg, DEFAULT_PRIORITY,
                    text, secondaryText, buf.readIdentifier()
            );
        }

        @Override
        public void writeToBuff(FriendlyByteBuf buf, IChatBubbleData data) {
            WaitingChatBubbleData textChat = (WaitingChatBubbleData) data;
            buf.writeIdentifier(textChat.bg);
            buf.writeJsonWithCodec(ComponentSerialization.CODEC, textChat.text);
            buf.writeBoolean(textChat.secondaryText != null);
            if (textChat.secondaryText != null) {
                buf.writeJsonWithCodec(ComponentSerialization.CODEC, textChat.secondaryText);
            }
            buf.writeIdentifier(textChat.icon);
        }
    }
}
