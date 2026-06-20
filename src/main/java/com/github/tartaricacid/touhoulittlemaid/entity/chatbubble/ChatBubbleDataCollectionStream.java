package com.github.tartaricacid.touhoulittlemaid.entity.chatbubble;

import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import static com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleDataCollection.MAX_SIZE;

public class ChatBubbleDataCollectionStream implements StreamCodec<RegistryFriendlyByteBuf, ChatBubbleDataCollection> {
    @Override
    public void encode(RegistryFriendlyByteBuf buf, ChatBubbleDataCollection data) {
        buf.writeVarInt(Math.min(MAX_SIZE, data.size()));
        int i = 0;
        for (long key : data.keySet()) {
            if (i < MAX_SIZE) {
                IChatBubbleData bubbleData = data.get(key);
                buf.writeLong(key);
                Identifier id = bubbleData.id();
                buf.writeIdentifier(id);
                ChatBubbleRegister.CODEC_MAP.get(id).writeToBuff(buf, bubbleData);
            }
            i++;
        }
    }

    @Override
    public ChatBubbleDataCollection decode(RegistryFriendlyByteBuf buf) {
        ChatBubbleDataCollection map = new ChatBubbleDataCollection(new Long2ObjectAVLTreeMap<>());
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            long key = buf.readLong();
            Identifier id = buf.readIdentifier();
            IChatBubbleData bubbleData = ChatBubbleRegister.CODEC_MAP.get(id).readFromBuff(buf);
            map.put(key, bubbleData);
        }
        return map;
    }
}
