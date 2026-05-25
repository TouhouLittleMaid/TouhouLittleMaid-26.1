package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.data.ProfileData;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskData;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.PROFILE;
import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.TASK;

public class MaidDataManager {
    private final EntityMaid maid;

    public MaidDataManager(EntityMaid entityMaid) {
        maid = entityMaid;
    }

    public String getModelId() {
        return maid.getData(PROFILE).modelId();
    }

    public void setModelId(String modelId) {
        ProfileData profileData = maid.getData(PROFILE).withModelId(modelId);
        maid.setData(PROFILE, profileData);
    }

    public String getSoundPackId() {
        return maid.getData(PROFILE).soundPackId();
    }

    public void setSoundPackId(String soundPackId) {
        ProfileData profileData = maid.getData(PROFILE).withSoundPackId(soundPackId);
        maid.setData(PROFILE, profileData);
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

    public interface View {
        MaidDataManager getDataManager();

        default String getModelId() {
            return getDataManager().getModelId();
        }

        default void setModelId(String modelId) {
            getDataManager().setModelId(modelId);
        }

        default String getSoundPackId() {
            return getDataManager().getSoundPackId();
        }

        default void setSoundPackId(String soundPackId) {
            getDataManager().setSoundPackId(soundPackId);
        }

        default IMaidTask getTask() {
            // TODO 受 Brain 初始化顺序影响，getDataManager 居然可能为 null
            // TODO 临时修正
            if (getDataManager() == null) {
                return TaskManager.getIdleTask();
            }
            return getDataManager().getTask();
        }

        default void setTask(IMaidTask task) {
            getDataManager().setTask(task);
        }
    }
}
