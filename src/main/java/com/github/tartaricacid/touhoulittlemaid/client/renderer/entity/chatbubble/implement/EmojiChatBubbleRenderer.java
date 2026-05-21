package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.implement;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.EntityGraphics;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.IChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.resource.listener.EmojiReloadListener;
import net.minecraft.resources.Identifier;

public class EmojiChatBubbleRenderer implements IChatBubbleRenderer {
    private final int width;
    private final int height;
    private final Identifier bg;
    private final Identifier emoji;

    public EmojiChatBubbleRenderer(Identifier bg) {
        this.bg = bg;
        var randomEmojis = EmojiReloadListener.getRandomEmojis();
        if (randomEmojis.isPresent()) {
            var emojiRes = randomEmojis.get();
            this.emoji = emojiRes.location();
            this.width = emojiRes.width();
            this.height = emojiRes.height();
            // 如果是 gif 表情的话，需要手动注册
            // FIXME 暂时删除 gif 动图功能
            if (emojiRes.isGif()) {
                //this.registerGifImage();
            }
        } else {
            // 如果没有表情资源，就使用一个默认的空白资源
            this.emoji = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/chat_bubble/maid_emoji/emoji_0.png");
            this.width = 24;
            this.height = 24;
        }
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
        graphics.blit(this.emoji, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
    }

    @Override
    public Identifier getBackgroundTexture() {
        return this.bg;
    }
}
