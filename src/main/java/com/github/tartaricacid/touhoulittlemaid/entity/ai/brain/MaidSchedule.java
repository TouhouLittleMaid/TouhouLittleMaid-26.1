package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain;


import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Locale;
import java.util.function.IntFunction;

public enum MaidSchedule implements StringRepresentable {
    // 日程表的模式
    DAY, NIGHT, ALL;

    public static final IntFunction<MaidSchedule> BY_ID = ByIdMap.continuous(MaidSchedule::ordinal, MaidSchedule.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final Codec<MaidSchedule> CODEC = StringRepresentable.fromEnum(MaidSchedule::values);
    public static final StreamCodec<ByteBuf, MaidSchedule> STREAM_CODEC = ByteBufCodecs.idMapper(MaidSchedule.BY_ID, MaidSchedule::ordinal);

    public EnvironmentAttribute<Activity> getEnvironmentAttribute() {
        return switch (this) {
            case DAY -> InitEntities.MAID_DAY_SHIFT_SCHEDULES.get();
            case NIGHT -> InitEntities.MAID_NIGHT_SHIFT_SCHEDULES.get();
            case ALL -> InitEntities.MAID_ALL_DAY_SCHEDULES.get();
        };
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}
