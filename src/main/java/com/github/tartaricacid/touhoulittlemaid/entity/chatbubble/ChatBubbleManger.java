package com.github.tartaricacid.touhoulittlemaid.entity.chatbubble;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl.ChatBubbleComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;


/**
 * 旧版的聊天气泡管理器，因为新的设计和实现方式不一样，所以这个类就弃用了
 * <p>
 * 注意这个类的名字也是拼写错误：Man_ger -> Man_a_ger
 */
@Deprecated
public class ChatBubbleManger {
    /**
     * use {@link ChatBubbleComponent#addTextChatBubble} instead
     */
    @Deprecated
    public static void addInnerChatText(EntityMaid maid, String key) {
        maid.components().chatBubble.addTextChatBubble(key);
    }

    @Deprecated
    public static void addAiChatTextSync(EntityMaid maid, String message) {
    }

    /**
     * use {@link ChatBubbleComponent#addLLMChatText(String, long)} instead
     */
    @Deprecated
    public static void addAiChatText(EntityMaid maid, String message) {
    }
}