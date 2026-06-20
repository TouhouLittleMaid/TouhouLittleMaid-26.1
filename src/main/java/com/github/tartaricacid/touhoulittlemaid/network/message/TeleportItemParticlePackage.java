package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.TeleportItemParticlePackageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record TeleportItemParticlePackage(
        int entityId, BlockPos chestPos, ItemStack itemStack, boolean chestToMaid, int delayTicks
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TeleportItemParticlePackage> TYPE = new CustomPacketPayload.Type<>(modLoc("teleport_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportItemParticlePackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TeleportItemParticlePackage::entityId,
            BlockPos.STREAM_CODEC, TeleportItemParticlePackage::chestPos,
            ItemStack.STREAM_CODEC, TeleportItemParticlePackage::itemStack,
            ByteBufCodecs.BOOL, TeleportItemParticlePackage::chestToMaid,
            ByteBufCodecs.VAR_INT, TeleportItemParticlePackage::delayTicks,
            TeleportItemParticlePackage::new
    );

    public static void handle(TeleportItemParticlePackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> TeleportItemParticlePackageProxy.handle(message));
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
