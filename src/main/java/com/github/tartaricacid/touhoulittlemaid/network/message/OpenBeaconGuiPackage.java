package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.OpenBeaconGuiPackageProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record OpenBeaconGuiPackage(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenBeaconGuiPackage> TYPE = new CustomPacketPayload.Type<>(modLoc("open_beacon_gui"));
    public static final StreamCodec<ByteBuf, OpenBeaconGuiPackage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            OpenBeaconGuiPackage::pos,
            OpenBeaconGuiPackage::new
    );

    public static void handle(OpenBeaconGuiPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> OpenBeaconGuiPackageProxy.handle(message));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
