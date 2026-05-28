package com.github.tartaricacid.touhoulittlemaid.entity.data;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.PickType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public record ConfigData(long booleanValue, PickType pickupType, float soundFreq) {
    private static final int PICKUP_FLAG = 0;
    private static final int HOME_MODE_FLAG = 1;
    private static final int RIDEABLE_FLAG = 2;
    private static final int SHOW_BACKPACK_FLAG = 3;
    private static final int SHOW_BACK_ITEM_FLAG = 4;
    private static final int CHAT_BUBBLE_SHOW_FLAG = 5;
    private static final int OPEN_DOOR_FLAG = 6;
    private static final int OPEN_FENCE_GATE_FLAG = 7;
    private static final int ACTIVE_CLIMBING_FLAG = 8;

    private static final long DEFAULT_BOOLEAN_VALUE =
            (1L << PICKUP_FLAG)
            // HOME_MODE 默认关闭
            | (1L << RIDEABLE_FLAG)
            | (1L << SHOW_BACKPACK_FLAG)
            | (1L << SHOW_BACK_ITEM_FLAG)
            | (1L << CHAT_BUBBLE_SHOW_FLAG)
            | (1L << OPEN_DOOR_FLAG)
            | (1L << OPEN_FENCE_GATE_FLAG)
            | (1L << ACTIVE_CLIMBING_FLAG);

    private static final float DEFAULT_SOUND_FREQ = 1.0f;

    private static final MapCodec<ConfigData> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.LONG.fieldOf("boolean_value").forGetter(ConfigData::booleanValue),
            PickType.CODEC.fieldOf("pickup_type").forGetter(ConfigData::pickupType),
            Codec.FLOAT.fieldOf("sound_freq").forGetter(ConfigData::soundFreq)
    ).apply(ins, ConfigData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG, ConfigData::booleanValue,
            PickType.STREAM_CODEC, ConfigData::pickupType,
            ByteBufCodecs.FLOAT, ConfigData::soundFreq,
            ConfigData::new
    );

    public static final AttachmentType<ConfigData> TYPE = AttachmentType
            .builder(ConfigData::defaultConfig)
            .serialize(CODEC)
            .sync(STREAM_CODEC)
            .build();

    private static ConfigData defaultConfig() {
        return new ConfigData(DEFAULT_BOOLEAN_VALUE, PickType.ALL, DEFAULT_SOUND_FREQ);
    }

    public ConfigData {
        soundFreq = Math.max(0f, Math.min(1f, soundFreq));
    }

    private ConfigData setFlag(int flag, boolean enabled) {
        long mask = 1L << flag;
        long newBooleanValue = enabled ? (booleanValue | mask) : (booleanValue & ~mask);
        return new ConfigData(newBooleanValue, pickupType, soundFreq);
    }

    private boolean hasFlag(int flag) {
        return (booleanValue & (1L << flag)) != 0;
    }

    public ConfigData setPickup(boolean isPickup) {
        return setFlag(PICKUP_FLAG, isPickup);
    }

    public boolean isPickup() {
        return hasFlag(PICKUP_FLAG);
    }

    public ConfigData setHomeModeEnable(boolean enable) {
        return setFlag(HOME_MODE_FLAG, enable);
    }

    public boolean isHomeModeEnable() {
        return hasFlag(HOME_MODE_FLAG);
    }

    public ConfigData setRideable(boolean rideable) {
        return setFlag(RIDEABLE_FLAG, rideable);
    }

    public boolean isRideable() {
        return hasFlag(RIDEABLE_FLAG);
    }

    public ConfigData setShowBackpack(boolean show) {
        return setFlag(SHOW_BACKPACK_FLAG, show);
    }

    public boolean isShowBackpack() {
        return hasFlag(SHOW_BACKPACK_FLAG);
    }

    public ConfigData setShowBackItem(boolean show) {
        return setFlag(SHOW_BACK_ITEM_FLAG, show);
    }

    public boolean isShowBackItem() {
        return hasFlag(SHOW_BACK_ITEM_FLAG);
    }

    public ConfigData setChatBubbleShow(boolean show) {
        return setFlag(CHAT_BUBBLE_SHOW_FLAG, show);
    }

    public boolean isChatBubbleShow() {
        return hasFlag(CHAT_BUBBLE_SHOW_FLAG);
    }

    public ConfigData setOpenDoor(boolean openDoor) {
        return setFlag(OPEN_DOOR_FLAG, openDoor);
    }

    public boolean isOpenDoor() {
        return hasFlag(OPEN_DOOR_FLAG);
    }

    public ConfigData setOpenFenceGate(boolean openFenceGate) {
        return setFlag(OPEN_FENCE_GATE_FLAG, openFenceGate);
    }

    public boolean isOpenFenceGate() {
        return hasFlag(OPEN_FENCE_GATE_FLAG);
    }

    public ConfigData setActiveClimbing(boolean activeClimbing) {
        return setFlag(ACTIVE_CLIMBING_FLAG, activeClimbing);
    }

    public boolean isActiveClimbing() {
        return hasFlag(ACTIVE_CLIMBING_FLAG);
    }

    public ConfigData setPickupType(PickType pickupType) {
        return new ConfigData(booleanValue, pickupType, soundFreq);
    }

    public PickType getPickupType() {
        return pickupType;
    }

    public ConfigData setSoundFreq(float soundFreq) {
        return new ConfigData(booleanValue, pickupType, soundFreq);
    }
}
