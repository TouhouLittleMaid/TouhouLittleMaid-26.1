package com.github.tartaricacid.touhoulittlemaid.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record TTSSystemAudioToClientPackage(String chatText) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TTSSystemAudioToClientPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("tts_system_audio_to_client"));
    public static final StreamCodec<ByteBuf, TTSSystemAudioToClientPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TTSSystemAudioToClientPackage::chatText,
            TTSSystemAudioToClientPackage::new
    );

    public static void handle(TTSSystemAudioToClientPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> onHandle(message));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void onHandle(TTSSystemAudioToClientPackage message) {
        Minecraft mc = Minecraft.getInstance();
        mc.getNarrator().narrator.say(message.chatText, true);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
