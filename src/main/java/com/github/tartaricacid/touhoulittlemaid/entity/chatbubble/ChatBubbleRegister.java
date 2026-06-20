package com.github.tartaricacid.touhoulittlemaid.entity.chatbubble;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class ChatBubbleRegister {
    public static final EntityDataSerializer<ChatBubbleDataCollection> INSTANCE = new DataSerializer();
    public static Map<Identifier, IChatBubbleData.ChatSerializer> CODEC_MAP = Maps.newHashMap();

    public static void init() {
        ChatBubbleRegister register = new ChatBubbleRegister();

        register.register(TextChatBubbleData.ID, new TextChatBubbleData.TextChatSerializer());
        register.register(ImageChatBubbleData.ID, new ImageChatBubbleData.ImageChatSerializer());
        register.register(WaitingChatBubbleData.ID, new WaitingChatBubbleData.WaitingChatSerializer());
        register.register(ProgressChatBubbleData.ID, new ProgressChatBubbleData.ProgressChatSerializer());
        register.register(EmojiChatBubbleData.ID, new EmojiChatBubbleData.EmojiChatSerializer());

        for (ILittleMaid littleMaid : TouhouLittleMaid.EXTENSIONS) {
            littleMaid.registerChatBubble(register);
        }

        CODEC_MAP = ImmutableMap.copyOf(CODEC_MAP);
    }

    public void register(Identifier id, IChatBubbleData.ChatSerializer serializer) {
        if (CODEC_MAP.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate codec id: " + id);
        }
        CODEC_MAP.put(id, serializer);
    }

    private static class DataSerializer implements EntityDataSerializer<ChatBubbleDataCollection> {
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ChatBubbleDataCollection> codec() {
            return new ChatBubbleDataCollectionStream();
        }

        @Override
        public ChatBubbleDataCollection copy(ChatBubbleDataCollection value) {
            return value.copy();
        }
    }
}
