package com.github.tartaricacid.touhoulittlemaid.network.message.ai;

import com.github.tartaricacid.touhoulittlemaid.ai.manager.site.AvailableSites;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.util.GameModeUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

@SuppressWarnings("InstantiationOfUtilityClass")
public record OpenAIConfigPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenAIConfigPacket> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("open_ai_config"));
    public static final StreamCodec<ByteBuf, OpenAIConfigPacket> STREAM_CODEC = StreamCodec.of(
            (byteBuf, message) -> {
            },
            byteBuf -> new OpenAIConfigPacket()
    );

    public static void sendToServer() {
        PacketDistributor.sendToServer(new OpenAIConfigPacket());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenAIConfigPacket message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> onHandle((ServerPlayer) context.player()));
        }
    }

    private static void onHandle(@Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }

        // 是否发送站点数据
        if (GameModeUtil.canEditSite(player)) {
            SyncAISitesPacket msg = new SyncAISitesPacket(AvailableSites.LLM_SITES, AvailableSites.TTS_SITES, false);
            NetworkHandler.sendToClientPlayer(msg, player);
        } else {
            // 否则发送一个空的站点数据
            SyncAISitesPacket msg = new SyncAISitesPacket(Collections.emptyMap(), Collections.emptyMap(), true);
            NetworkHandler.sendToClientPlayer(msg, player);
        }
    }
}
