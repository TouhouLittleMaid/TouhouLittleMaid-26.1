package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskData;
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

@MaidManagerDef(alias = "taskManager", exposeView = true)
public class MaidTaskManager {
    private final EntityMaid maid;
    private final SchedulePos schedulePos;

    public MaidTaskManager(EntityMaid entityMaid) {
        maid = entityMaid;
        this.schedulePos = new SchedulePos(BlockPos.ZERO, maid.level.dimension().identifier());
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
        EnvironmentAttribute<Activity> schedule = this.getSchedule().getEnvironmentAttribute();
        return maid.level.environmentAttributes().getValue(schedule, maid.blockPosition());
    }

    public void clearHome() {
        this.schedulePos.clear(this.maid);
    }

    void tick() {
        Profiler.get().push("maidSchedulePos");
        this.schedulePos.tick(this.maid);
        Profiler.get().pop();
    }

    void read(ValueInput input) {
        this.schedulePos.load(input, this.maid);
    }

    void save(ValueOutput output) {
        this.schedulePos.save(output);
    }

    void setHomeTo(BlockPos pos, int distance) {
        TaskData data = this.getData();
        TaskData newData = new TaskData(data.taskId(), data.schedule(), pos, distance);
        this.setData(newData);
    }

    BlockPos getHomePosition() {
        return this.getData().restrictCenter();
    }

    int getHomeRadius() {
        return this.getData().restrictRadius();
    }

    private TaskData getData() {
        return maid.getData(TASK);
    }

    private void setData(TaskData data) {
        maid.setData(TASK, data);
    }

    interface View {
        MaidTaskManager getTaskManager();

        default IMaidTask getTask() {
            // TODO 受 Brain 初始化顺序影响，getTaskManager 居然可能为 null
            // TODO 临时修正
            if (getTaskManager() == null) {
                return TaskManager.getIdleTask();
            }
            return getTaskManager().getTask();
        }

        default void setTask(IMaidTask task) {
            getTaskManager().setTask(task);
        }

        default MaidSchedule getSchedule() {
            // TODO 受 Brain 初始化顺序影响，getTaskManager 居然可能为 null
            // TODO 临时修正
            if (getTaskManager() == null) {
                return MaidSchedule.ALL;
            }
            return getTaskManager().getSchedule();
        }

        default void setSchedule(MaidSchedule schedule) {
            getTaskManager().setSchedule(schedule);
        }

        default Activity getScheduleDetail() {
            return getTaskManager().getScheduleDetail();
        }

        default void clearHome() {
            getTaskManager().clearHome();
        }

        default SchedulePos getSchedulePos() {
            return getTaskManager().schedulePos;
        }
    }
}
