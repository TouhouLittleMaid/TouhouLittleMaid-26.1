package com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.IChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.implement.EmojiChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.IChatBubbleData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EmojiChatBubbleData implements IChatBubbleData {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "emoji");
    private final Identifier bg;
    @OnlyIn(Dist.CLIENT)
    private IChatBubbleRenderer renderer;

    public EmojiChatBubbleData(Identifier bg) {
        this.bg = bg;
    }

    public static EmojiChatBubbleData create() {
        return new EmojiChatBubbleData(TYPE_2);
    }

    @Override
    public int existTick() {
        return DEFAULT_EXIST_TICK;
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public IChatBubbleRenderer getRenderer(IChatBubbleRenderer.Position position) {
        if (this.renderer == null) {
            this.renderer = new EmojiChatBubbleRenderer(this.bg);
        }
        return this.renderer;
    }

    public static class EmojiChatSerializer implements IChatBubbleData.ChatSerializer {
        @Override
        public IChatBubbleData readFromBuff(FriendlyByteBuf buf) {
            Identifier bg = buf.readIdentifier();
            return new EmojiChatBubbleData(bg);
        }

        @Override
        public void writeToBuff(FriendlyByteBuf buf, IChatBubbleData data) {
            EmojiChatBubbleData emojiChat = (EmojiChatBubbleData) data;
            buf.writeIdentifier(emojiChat.bg);
        }
    }
}
