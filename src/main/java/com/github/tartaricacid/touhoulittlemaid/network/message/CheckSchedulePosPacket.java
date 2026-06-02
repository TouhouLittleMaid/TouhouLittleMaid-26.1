package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.CheckSchedulePosPacketProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record CheckSchedulePosPacket(String tips) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CheckSchedulePosPacket> TYPE = new CustomPacketPayload.Type<>(modLoc("check_schedule_pos"));
    public static final StreamCodec<ByteBuf, CheckSchedulePosPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            CheckSchedulePosPacket::tips,
            CheckSchedulePosPacket::new
    );

    public static void handle(CheckSchedulePosPacket message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> CheckSchedulePosPacketProxy.handle(message));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
