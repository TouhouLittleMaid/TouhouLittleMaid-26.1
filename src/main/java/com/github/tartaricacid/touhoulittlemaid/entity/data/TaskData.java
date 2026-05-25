package com.github.tartaricacid.touhoulittlemaid.entity.data;

import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskIdle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public record TaskData(String taskId) {
    private static final MapCodec<TaskData> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.STRING.fieldOf("task_id").forGetter(TaskData::taskId)
    ).apply(ins, TaskData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, TaskData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TaskData::taskId,
            TaskData::new
    );

    public static final AttachmentType<TaskData> TYPE = AttachmentType
            .builder(() -> new TaskData(TaskIdle.UID.toString()))
            .serialize(CODEC)
            .sync(STREAM_CODEC)
            .build();
}
