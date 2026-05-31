package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.PlayMaidSoundPackageProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record PlayMaidSoundPackage(Identifier soundEvent, String id,
                                   int entityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayMaidSoundPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("play_maid_sound"));
    public static final StreamCodec<ByteBuf, PlayMaidSoundPackage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            PlayMaidSoundPackage::soundEvent,
            ByteBufCodecs.STRING_UTF8,
            PlayMaidSoundPackage::id,
            ByteBufCodecs.VAR_INT,
            PlayMaidSoundPackage::entityId,
            PlayMaidSoundPackage::new
    );

    public static void handle(PlayMaidSoundPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> PlayMaidSoundPackageProxy.handle(message));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
