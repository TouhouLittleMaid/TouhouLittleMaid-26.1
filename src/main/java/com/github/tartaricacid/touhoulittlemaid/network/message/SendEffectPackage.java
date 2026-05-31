package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.SendEffectPackageProxy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SendEffectPackage(int id, Collection<MobEffectInstance> effects) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SendEffectPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("send_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, Collection<MobEffectInstance>> COLLECTION_STREAM_CODEC =
            ByteBufCodecs.collection(
                    ArrayList::new,
                    MobEffectInstance.STREAM_CODEC,
                    256);

    public static final StreamCodec<RegistryFriendlyByteBuf, SendEffectPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SendEffectPackage::id,
            COLLECTION_STREAM_CODEC,
            SendEffectPackage::effects,
            SendEffectPackage::new
    );

    public static void handle(SendEffectPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> SendEffectPackageProxy.handle(message));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record EffectData(String descriptionId, int amplifier, int duration, int category) {
        public EffectData(MobEffectInstance effect) {
            this(effect.getDescriptionId(), effect.getAmplifier(), effect.getDuration(),
                    effect.getEffect().value().getCategory().ordinal());
        }
    }
}
