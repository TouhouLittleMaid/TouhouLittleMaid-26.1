package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.SchedulePos;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponentDef;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle.AiStepComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle.SaveComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.TASK;

@MaidComponentDef("task")
public class MaidTaskComponent implements MaidComponent, AiStepComponent, SaveComponent {
    private final EntityMaid maid;
    public final SchedulePos schedulePos;

    public MaidTaskComponent(EntityMaid entityMaid) {
        maid = entityMaid;
        this.schedulePos = new SchedulePos(BlockPos.ZERO, maid.level.dimension().identifier());
    }

    @Override
    public int priority() {
        return 40;
    }

    public IMaidTask getTask() {
        String taskId = this.getData().taskId();
        Identifier uid = Identifier.parse(taskId);
        return TaskManager.findTask(uid).orElse(TaskManager.getIdleTask());
    }

    public void setTask(IMaidTask task) {
        TaskData oldTask = this.getData();
        String taskNewId = task.getUid().toString();
        if (taskNewId.equals(oldTask.taskId())) {
            return;
        }
        this.maid.setData(TASK, oldTask.withTaskId(taskNewId));
        if (this.maid.level instanceof ServerLevel serverLevel) {
            this.maid.refreshBrain(serverLevel);
        }
    }

    public MaidSchedule getSchedule() {
        return this.getData().schedule();
    }

    public void setSchedule(MaidSchedule schedule) {
        TaskData data = this.getData();
        this.setData(data.withSchedule(schedule));
        if (this.maid.level instanceof ServerLevel serverLevel) {
            this.maid.refreshBrain(serverLevel);
        }
    }

    public Activity getScheduleDetail() {
        EnvironmentAttribute<Activity> schedule = getSchedule().getEnvironmentAttribute();
        return maid.level.environmentAttributes().getValue(schedule, maid.blockPosition());
    }

    public void clearHome() {
        this.schedulePos.clear(this.maid);
    }

    @Override
    public void aiStep() {
        Profiler.get().push("maidSchedulePos");
        this.schedulePos.tick(this.maid);
        Profiler.get().pop();
    }

    @Override
    public void load(ValueInput input) {
        this.schedulePos.load(input, this.maid);
    }

    @Override
    public void save(ValueOutput output) {
        this.schedulePos.save(output);
    }

    public void setHomeTo(BlockPos pos, int distance) {
        TaskData data = this.getData();
        TaskData newData = new TaskData(data.taskId(), data.schedule(), pos, distance);
        this.setData(newData);
    }

    public BlockPos getHomePosition() {
        return this.getData().restrictCenter();
    }

    public int getHomeRadius() {
        return this.getData().restrictRadius();
    }

    private TaskData getData() {
        return maid.getData(TASK);
    }

    private void setData(TaskData data) {
        maid.setData(TASK, data);
    }
}
