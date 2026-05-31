package com.github.tartaricacid.touhoulittlemaid.network.message.ai;

import com.github.tartaricacid.touhoulittlemaid.ai.service.SerializableSite;
import com.github.tartaricacid.touhoulittlemaid.ai.service.SerializerRegister;
import com.github.tartaricacid.touhoulittlemaid.ai.service.ServiceType;
import com.github.tartaricacid.touhoulittlemaid.ai.service.Site;
import com.github.tartaricacid.touhoulittlemaid.ai.service.llm.LLMSite;
import com.github.tartaricacid.touhoulittlemaid.ai.service.tts.TTSSite;
import com.github.tartaricacid.touhoulittlemaid.network.client.ai.SyncAISitesPacketProxy;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SyncAISitesPacket(
        Map<String, LLMSite> llmSites,
        Map<String, TTSSite> ttsSites,
        boolean insufficientPermissions
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncAISitesPacket> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("sync_ai_sites"));
    public static final StreamCodec<ByteBuf, SyncAISitesPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncAISitesPacket decode(ByteBuf byteBuf) {
            FriendlyByteBuf buf = new FriendlyByteBuf(byteBuf);
            int llmSize = buf.readInt();
            Map<String, LLMSite> llmSites = Maps.newHashMap();
            for (int i = 0; i < llmSize; i++) {
                String key = buf.readUtf();
                String apiType = buf.readUtf();
                LLMSite site = readSiteFromNetwork(ServiceType.LLM, apiType, buf);
                if (site != null) {
                    llmSites.put(key, site);
                }
            }

            int ttsSize = buf.readInt();
            Map<String, TTSSite> ttsSites = Maps.newHashMap();
            for (int i = 0; i < ttsSize; i++) {
                String key = buf.readUtf();
                String apiType = buf.readUtf();
                TTSSite site = readSiteFromNetwork(ServiceType.TTS, apiType, buf);
                if (site != null) {
                    ttsSites.put(key, site);
                }
            }

            boolean insufficientPermissions = buf.readBoolean();
            return new SyncAISitesPacket(llmSites, ttsSites, insufficientPermissions);
        }

        @Override
        public void encode(ByteBuf byteBuf, SyncAISitesPacket message) {
            FriendlyByteBuf buf = new FriendlyByteBuf(byteBuf);
            buf.writeInt(message.llmSites.size());
            message.llmSites.forEach((key, value) -> {
                buf.writeUtf(key);
                buf.writeUtf(value.getApiType());
                writeSiteToNetwork(value, buf);
            });

            buf.writeInt(message.ttsSites.size());
            message.ttsSites.forEach((key, value) -> {
                buf.writeUtf(key);
                buf.writeUtf(value.getApiType());
                writeSiteToNetwork(value, buf);
            });

            buf.writeBoolean(message.insufficientPermissions);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncAISitesPacket message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> SyncAISitesPacketProxy.handle(message));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Site> void writeSiteToNetwork(T site, FriendlyByteBuf buf) {
        ((SerializableSite<T>) site.serializer()).writeToNetwork(site, buf);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T extends Site> T readSiteFromNetwork(ServiceType type, String apiType, FriendlyByteBuf buf) {
        SerializableSite<? extends Site> serializer = SerializerRegister.getSerializer(type, apiType);
        if (serializer == null) {
            return null;
        }
        return ((SerializableSite<T>) serializer).fromNetwork(buf);
    }
}
