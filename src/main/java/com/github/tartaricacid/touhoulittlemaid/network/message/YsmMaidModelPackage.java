package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record YsmMaidModelPackage(int maidId, String modeId, String texture,
                                  Component name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<YsmMaidModelPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("ysm_maid_model"));
    public static final StreamCodec<RegistryFriendlyByteBuf, YsmMaidModelPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            YsmMaidModelPackage::maidId,
            ByteBufCodecs.STRING_UTF8,
            YsmMaidModelPackage::modeId,
            ByteBufCodecs.STRING_UTF8,
            YsmMaidModelPackage::texture,
            ComponentSerialization.STREAM_CODEC,
            YsmMaidModelPackage::name,
            YsmMaidModelPackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(YsmMaidModelPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                Entity entity = sender.level.getEntity(message.maidId);
                if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
                    if (sender.isCreative() || MaidConfig.MAID_CHANGE_MODEL.get()) {
                        maid.setIsYsmModel(true);
                        maid.setYsmModel(message.modeId, message.texture, message.name);
                        InitTrigger.MAID_EVENT.get().trigger(sender, TriggerType.CHANGE_MAID_MODEL);
                    } else {
                        sender.sendSystemMessage(Component.translatable("message.touhou_little_maid.change_model.disabled"));
                    }
                }
            });
        }
    }
}
