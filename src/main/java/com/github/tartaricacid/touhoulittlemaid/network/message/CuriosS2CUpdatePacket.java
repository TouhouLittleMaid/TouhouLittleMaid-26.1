package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.CuriosS2CUpdatePacketProxy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record CuriosS2CUpdatePacket(int page) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CuriosS2CUpdatePacket> TYPE = new CustomPacketPayload.Type<>(modLoc("curios_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CuriosS2CUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CuriosS2CUpdatePacket::page,
            CuriosS2CUpdatePacket::new
    );

    public static void handle(CuriosS2CUpdatePacket message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> CuriosS2CUpdatePacketProxy.handle(message));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
