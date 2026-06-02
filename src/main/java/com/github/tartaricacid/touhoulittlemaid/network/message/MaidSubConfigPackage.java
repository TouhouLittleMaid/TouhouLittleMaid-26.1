package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.entity.data.ConfigData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.CONFIG;
import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record MaidSubConfigPackage(int id, ConfigData configData) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MaidSubConfigPackage> TYPE = new CustomPacketPayload.Type<>(modLoc("maid_sub_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MaidSubConfigPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MaidSubConfigPackage::id,
            ConfigData.STREAM_CODEC, MaidSubConfigPackage::configData,
            MaidSubConfigPackage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MaidSubConfigPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (!(context.player() instanceof ServerPlayer sender)) {
                    return;
                }
                Entity entity = sender.level.getEntity(message.id);
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    maid.setData(CONFIG, message.configData);
                }
            });
        }
    }
}
