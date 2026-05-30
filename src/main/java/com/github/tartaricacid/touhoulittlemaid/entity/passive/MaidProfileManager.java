package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.entity.data.ProfileData;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.PROFILE;

/**
 * 女仆档案管理器，主要管理女仆的模型和音效包等信息
 */
public class MaidProfileManager {
    private final EntityMaid maid;

    public MaidProfileManager(EntityMaid entityMaid) {
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

    public interface View {
        MaidProfileManager getProfileManager();

        default String getModelId() {
            return getProfileManager().getModelId();
        }

        default void setModelId(String modelId) {
            getProfileManager().setModelId(modelId);
        }

        default String getSoundPackId() {
            return getProfileManager().getSoundPackId();
        }

        default void setSoundPackId(String soundPackId) {
            getProfileManager().setSoundPackId(soundPackId);
        }
    }
}
