package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.SyncDataPackageProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record SyncDataPackage(float power, int maidNum) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncDataPackage> TYPE = new CustomPacketPayload.Type<>(modLoc("sync_data"));
    public static final StreamCodec<ByteBuf, SyncDataPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            SyncDataPackage::power,
            ByteBufCodecs.VAR_INT,
            SyncDataPackage::maidNum,
            SyncDataPackage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncDataPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> SyncDataPackageProxy.handle(message));
        }
    }
}
