package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.BeaconAbsorbPackageProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record BeaconAbsorbPackage(float x, float y, float z) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BeaconAbsorbPackage> TYPE = new CustomPacketPayload.Type<>(modLoc("beacon_absorb"));
    public static final StreamCodec<ByteBuf, BeaconAbsorbPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            BeaconAbsorbPackage::x,
            ByteBufCodecs.FLOAT,
            BeaconAbsorbPackage::y,
            ByteBufCodecs.FLOAT,
            BeaconAbsorbPackage::z,
            BeaconAbsorbPackage::new
    );

    public static void handle(BeaconAbsorbPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> BeaconAbsorbPackageProxy.handle(message));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
