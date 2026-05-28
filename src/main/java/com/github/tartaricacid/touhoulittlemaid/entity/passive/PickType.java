package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public enum PickType implements StringRepresentable {
    ONLY_ITEM(true, false),
    ONLY_XP(false, true),
    ALL(true, true);

    public static final IntFunction<PickType> BY_ID = ByIdMap.continuous(PickType::ordinal, PickType.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final Codec<PickType> CODEC = StringRepresentable.fromEnum(PickType::values);
    public static final StreamCodec<ByteBuf, PickType> STREAM_CODEC = ByteBufCodecs.idMapper(PickType.BY_ID, PickType::ordinal);

    private final boolean pickItem;
    private final boolean pickXp;

    PickType(boolean pickItem, boolean pickXp) {
        this.pickItem = pickItem;
        this.pickXp = pickXp;
    }

    public boolean canPickItem() {
        return pickItem;
    }

    public boolean canPickXp() {
        return pickXp;
    }

    public static String getTransKey(final PickType pickType) {
        return switch (pickType) {
            case ONLY_ITEM -> "gui.touhou_little_maid.maid_config.value.item";
            case ONLY_XP -> "gui.touhou_little_maid.maid_config.value.xp";
            default -> "gui.touhou_little_maid.maid_config.value.all";
        };
    }

    public static PickType getNextPickType(final PickType pickType) {
        return values()[(pickType.ordinal() + 1) % values().length];
    }

    public static PickType getPreviousPickType(final PickType pickType) {
        int index = pickType.ordinal() - 1;
        if (index < 0) {
            index = values().length - 1;
        }
        return values()[index % values().length];
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}