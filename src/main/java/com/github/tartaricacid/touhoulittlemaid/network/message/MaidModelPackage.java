package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record MaidModelPackage(int id, Identifier modelId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MaidModelPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("maid_model"));
    public static final StreamCodec<ByteBuf, MaidModelPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            MaidModelPackage::id,
            Identifier.STREAM_CODEC,
            MaidModelPackage::modelId,
            MaidModelPackage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MaidModelPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                Entity entity = sender.level.getEntity(message.id);
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    if (sender.isCreative() || MaidConfig.MAID_CHANGE_MODEL.get()) {
                        maid.components.profile.setModelId(message.modelId.toString());
                        InitTrigger.MAID_EVENT.get().trigger(sender, TriggerType.CHANGE_MAID_MODEL);
                    } else {
                        sender.sendSystemMessage(Component.translatable("message.touhou_little_maid.change_model.disabled"));
                    }
                }
            });
        }
    }
}
