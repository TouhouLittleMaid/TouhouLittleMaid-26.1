package com.github.tartaricacid.touhoulittlemaid.entity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public record AnimationData(long booleanValue) {
    private static final int BEGGING_FLAG = 0;
    private static final int CHARGING_CROSSBOW_FLAG = 1;
    private static final int SWINGING_ARMS_FLAG = 2;
    private static final int AIMING_FLAG = 3;

    private static final long DEFAULT_BOOLEAN_VALUE = 0L;

    private static final MapCodec<AnimationData> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.LONG.fieldOf("boolean_value").forGetter(AnimationData::booleanValue)
    ).apply(ins, AnimationData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AnimationData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG, AnimationData::booleanValue,
            AnimationData::new
    );

    public static final AttachmentType<AnimationData> TYPE = AttachmentType
            .builder(AnimationData::defaultAnimation)
            .serialize(CODEC)
            .sync(STREAM_CODEC)
            .build();

    private static AnimationData defaultAnimation() {
        return new AnimationData(DEFAULT_BOOLEAN_VALUE);
    }

    private AnimationData setFlag(int flag, boolean enabled) {
        long mask = 1L << flag;
        long newBooleanValue = enabled ? (booleanValue | mask) : (booleanValue & ~mask);
        return new AnimationData(newBooleanValue);
    }

    private boolean hasFlag(int flag) {
        return (booleanValue & (1L << flag)) != 0;
    }

    public AnimationData setBegging(boolean begging) {
        return setFlag(BEGGING_FLAG, begging);
    }

    public boolean isBegging() {
        return hasFlag(BEGGING_FLAG);
    }

    public AnimationData setChargingCrossbow(boolean charging) {
        return setFlag(CHARGING_CROSSBOW_FLAG, charging);
    }

    public boolean isChargingCrossbow() {
        return hasFlag(CHARGING_CROSSBOW_FLAG);
    }

    public AnimationData setSwingingArms(boolean swingingArms) {
        return setFlag(SWINGING_ARMS_FLAG, swingingArms);
    }

    public boolean isSwingingArms() {
        return hasFlag(SWINGING_ARMS_FLAG);
    }

    public AnimationData setAiming(boolean aiming) {
        return setFlag(AIMING_FLAG, aiming);
    }

    public boolean isAiming() {
        return hasFlag(AIMING_FLAG);
    }
}
