package com.github.tartaricacid.touhoulittlemaid.entity.data;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskIdle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public record TaskData(
        String taskId,
        MaidSchedule schedule,
        BlockPos restrictCenter,
        int restrictRadius
) {
    private static final String DEFAULT_TASK_ID = TaskIdle.UID.toString();
    private static final MaidSchedule DEFAULT_SCHEDULE = MaidSchedule.ALL;
    private static final BlockPos DEFAULT_RESTRICT_CENTER = BlockPos.ZERO;

    private static final MapCodec<TaskData> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.STRING.fieldOf("task_id").forGetter(TaskData::taskId),
            MaidSchedule.CODEC.fieldOf("schedule").forGetter(TaskData::schedule),
            BlockPos.CODEC.fieldOf("restrict_center").forGetter(TaskData::restrictCenter),
            Codec.INT.fieldOf("restrict_radius").forGetter(TaskData::restrictRadius)
    ).apply(ins, TaskData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, TaskData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TaskData::taskId,
            MaidSchedule.STREAM_CODEC, TaskData::schedule,
            BlockPos.STREAM_CODEC, TaskData::restrictCenter,
            ByteBufCodecs.INT, TaskData::restrictRadius,
            TaskData::new
    );

    public TaskData {
        taskId = taskId == null ? DEFAULT_TASK_ID : taskId;
        schedule = schedule == null ? DEFAULT_SCHEDULE : schedule;
        restrictCenter = restrictCenter == null ? DEFAULT_RESTRICT_CENTER : restrictCenter;
        restrictRadius = Math.max(0, restrictRadius);
    }

    public TaskData() {
        this(DEFAULT_TASK_ID, DEFAULT_SCHEDULE, DEFAULT_RESTRICT_CENTER, MaidConfig.MAID_NON_HOME_RANGE.get());
    }

    public TaskData withTaskId(String taskId) {
        return new TaskData(taskId, this.schedule, this.restrictCenter, this.restrictRadius);
    }

    public TaskData withSchedule(MaidSchedule schedule) {
        return new TaskData(this.taskId, schedule, this.restrictCenter, this.restrictRadius);
    }

    public TaskData withRestrictCenter(BlockPos restrictCenter) {
        return new TaskData(this.taskId, this.schedule, restrictCenter, this.restrictRadius);
    }

    public TaskData withRestrictRadius(int restrictRadius) {
        return new TaskData(this.taskId, this.schedule, this.restrictCenter, restrictRadius);
    }

    public static final AttachmentType<TaskData> TYPE = AttachmentType
            .builder(TaskData::new)
            .serialize(CODEC)
            .sync(STREAM_CODEC)
            .build();
}
