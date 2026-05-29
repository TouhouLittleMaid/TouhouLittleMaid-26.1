package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl;

import com.github.tartaricacid.touhoulittlemaid.entity.data.ProfileData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponentDef;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.PROFILE;

/**
 * 女仆档案管理器，主要管理女仆的模型和音效包等信息
 */
@MaidComponentDef("profile")
public class MaidProfileComponent implements MaidComponent {
    private final EntityMaid maid;

    public MaidProfileComponent(EntityMaid entityMaid) {
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
}
