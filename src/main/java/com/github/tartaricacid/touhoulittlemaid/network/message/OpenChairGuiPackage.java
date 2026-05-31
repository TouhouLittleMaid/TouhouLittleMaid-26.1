package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.OpenChairGuiPackageProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record OpenChairGuiPackage(int id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenChairGuiPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("open_chair_gui"));
    public static final StreamCodec<ByteBuf, OpenChairGuiPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            OpenChairGuiPackage::id,
            OpenChairGuiPackage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenChairGuiPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> OpenChairGuiPackageProxy.handle(message));
        }
    }
}
