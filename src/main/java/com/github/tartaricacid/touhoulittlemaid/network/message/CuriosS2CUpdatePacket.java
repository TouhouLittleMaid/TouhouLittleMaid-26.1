package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.compat.curios.CuriosCompat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record CuriosS2CUpdatePacket(int page) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CuriosS2CUpdatePacket> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("curios_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CuriosS2CUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CuriosS2CUpdatePacket::page,
            CuriosS2CUpdatePacket::new
    );

    public static void handle(CuriosS2CUpdatePacket message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            Player sender = context.player();
            if (!(sender instanceof ServerPlayer serverPlayer)) {
                return;
            }
            context.enqueueWork(() -> CuriosCompat.clientUpdatePage(message.page()));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
