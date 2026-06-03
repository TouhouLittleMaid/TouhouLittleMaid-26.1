package com.github.tartaricacid.touhoulittlemaid.client.model.bedrock;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockVersion;
import com.github.tartaricacid.touhoulittlemaid.api.animation.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.List;

public class EntityMaidModel extends SimpleBedrockEntityModel<EntityMaidRenderState> {
    private static final String HEAD = "head";
    private static final String ARM_LEFT = "armLeft";
    private static final String ARM_RIGHT = "armRight";
    private static final String BACKPACK_POSITIONING_BONE = "backpackPositioningBone";
    private static final String ARM_LEFT_POSITIONING_BONE = "armLeftPositioningBone";
    private static final String ARM_RIGHT_POSITIONING_BONE = "armRightPositioningBone";
    private static final String WAIST_LEFT_POSITIONING_BONE = "waistLeftPositioningBone";
    private static final String WAIST_RIGHT_POSITIONING_BONE = "waistRightPositioningBone";

    private List<IAnimation<EntityMaidRenderState>> animations = Lists.newArrayList();

    public EntityMaidModel() {
        super();
    }

    public EntityMaidModel(InputStream stream) {
        super(stream);
    }

    public EntityMaidModel(BedrockModelPOJO pojo, BedrockVersion version) {
        super(pojo, version);
    }

    @Override
    @NullMarked
    public void setupAnim(EntityMaidRenderState state) {
        super.setupAnim(state);
        if (animations == null || animations.isEmpty()) {
            return;
        }
        this.animations.forEach(animation ->
                animation.setupAnimation(state, modelMap)
        );
    }

    public void translateToHand(HumanoidArm sideIn, PoseStack poseStack) {
        BedrockPart arm = getArm(sideIn);
        if (arm != null) {
            arm.translateAndRotate(poseStack);
        }
    }

    public boolean hasBackpackPositioningModel() {
        return modelMap.get(BACKPACK_POSITIONING_BONE) != null;
    }

    public BedrockPart getBackpackPositioningModel() {
        return modelMap.get(BACKPACK_POSITIONING_BONE);
    }

    @Nullable
    private BedrockPart getArm(HumanoidArm sideIn) {
        return getModelPartBySide(sideIn, ARM_LEFT, ARM_RIGHT);
    }

    @Nullable
    private BedrockPart getModelPartBySide(HumanoidArm side, String leftKey, String rightKey) {
        return modelMap.get(side == HumanoidArm.LEFT ? leftKey : rightKey);
    }

    public boolean hasHead() {
        return modelMap.containsKey(HEAD);
    }

    public BedrockPart getHead() {
        return modelMap.get(HEAD);
    }

    public boolean hasLeftArm() {
        return modelMap.containsKey(ARM_LEFT);
    }

    public BedrockPart getLeftArm() {
        return modelMap.get(ARM_LEFT);
    }

    public boolean hasRightArm() {
        return modelMap.containsKey(ARM_RIGHT);
    }

    public BedrockPart getRightArm() {
        return modelMap.get(ARM_RIGHT);
    }

    public boolean hasArmPositioningModel(HumanoidArm side) {
        BedrockPart arm = getArmPositioningModel(side);
        return arm != null;
    }

    @Nullable
    public BedrockPart getArmPositioningModel(HumanoidArm side) {
        return getModelPartBySide(side, ARM_LEFT_POSITIONING_BONE, ARM_RIGHT_POSITIONING_BONE);
    }

    public void translateToPositioningHand(HumanoidArm sideIn, PoseStack poseStack) {
        BedrockPart arm = getArmPositioningModel(sideIn);
        if (arm != null) {
            arm.translateAndRotate(poseStack);
        }
    }

    public boolean hasWaistPositioningModel(HumanoidArm side) {
        BedrockPart waist = getWaistPositioningModel(side);
        return waist != null;
    }

    @Nullable
    private BedrockPart getWaistPositioningModel(HumanoidArm side) {
        return getModelPartBySide(side, WAIST_LEFT_POSITIONING_BONE, WAIST_RIGHT_POSITIONING_BONE);
    }

    public void translateToPositioningWaist(HumanoidArm sideIn, PoseStack poseStack) {
        BedrockPart waist = getWaistPositioningModel(sideIn);
        if (waist != null) {
            waist.translateAndRotate(poseStack);
        }
    }

    public void setAnimations(@Nullable List<IAnimation<EntityMaidRenderState>> animations) {
        this.animations = animations;
    }
}
