package com.github.tartaricacid.touhoulittlemaid.entity.misc;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum MonsterType implements StringRepresentable {
    FRIENDLY(0, "friendly"),
    NEUTRAL(1, "neutral"),
    HOSTILE(2, "hostile");

    public static final IntFunction<MonsterType> BY_ID = ByIdMap.continuous(s -> s.id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final Codec<MonsterType> CODEC = StringRepresentable.fromEnum(MonsterType::values);
    public static final StreamCodec<ByteBuf, MonsterType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, s -> s.id);

    private final int id;
    private final String typeName;
    private final MutableComponent component;

    MonsterType(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;
        this.component = Component.translatable("gui.touhou_little_maid.monster_type.%s".formatted(typeName));
    }

    public MonsterType getPrevious() {
        return BY_ID.apply(this.id - 1);
    }

    public MonsterType getNext() {
        return BY_ID.apply(this.id + 1);
    }

    public MutableComponent getComponent() {
        return component;
    }

    @Override
    public String getSerializedName() {
        return this.typeName;
    }
}