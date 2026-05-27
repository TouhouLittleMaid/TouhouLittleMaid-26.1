package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskData;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.TASK;

public class MaidTaskManager {
    private final EntityMaid maid;

    public MaidTaskManager(EntityMaid entityMaid) {
        maid = entityMaid;
    }

    public IMaidTask getTask() {
        String taskId = maid.getData(TASK).taskId();
        Identifier uid = Identifier.parse(taskId);
        return TaskManager.findTask(uid).orElse(TaskManager.getIdleTask());
    }

    public void setTask(IMaidTask task) {
        String taskOld = maid.getData(TASK).taskId();
        String taskNew = task.getUid().toString();
        if (taskNew.equals(taskOld)) {
            return;
        }
        maid.task = task;
        maid.setData(TASK, new TaskData(taskNew));
        if (maid.level instanceof ServerLevel serverLevel) {
            maid.refreshBrain(serverLevel);
        }
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
    }
}
