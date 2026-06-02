package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.OpenSwitcherGuiPackageProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record OpenSwitcherGuiPackage(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenSwitcherGuiPackage> TYPE = new CustomPacketPayload.Type<>(modLoc("open_switcher_gui"));
    public static final StreamCodec<ByteBuf, OpenSwitcherGuiPackage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            OpenSwitcherGuiPackage::pos,
            OpenSwitcherGuiPackage::new
    );

    public static void handle(OpenSwitcherGuiPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> OpenSwitcherGuiPackageProxy.handle(message));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
