package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.WChessToClientPackageProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record WChessToClientPackage(BlockPos pos, String fenData) implements CustomPacketPayload {
    public static final Type<WChessToClientPackage> TYPE = new Type<>(modLoc("wchess_to_client"));
    public static final StreamCodec<ByteBuf, WChessToClientPackage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            WChessToClientPackage::pos,
            ByteBufCodecs.STRING_UTF8,
            WChessToClientPackage::fenData,
            WChessToClientPackage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WChessToClientPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> WChessToClientPackageProxy.handle(message));
        }
    }
}
