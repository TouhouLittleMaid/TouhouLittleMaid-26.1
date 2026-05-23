package com.github.tartaricacid.touhoulittlemaid.client.model.bedrock;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.AbstractBedrockEntityModel;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockVersion;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.util.List;

public class BedrockModel<T extends LivingEntityRenderState> extends AbstractBedrockEntityModel<T> {
    private List<Object> animations = Lists.newArrayList();

    public BedrockModel() {
        super();
    }

    public BedrockModel(BedrockModelPOJO pojo, BedrockVersion version) {
        super(pojo, version);
    }

    @Override
    @NullMarked
    @SuppressWarnings("unchecked")
    public void setupAnim(LivingEntityRenderState state) {
        if (animations == null || animations.isEmpty()) {
            return;
        }

        T typedState = (T) state;
        for (Object animation : animations) {
            if (animation instanceof IAnimation<?> iAnimation) {
                ((IAnimation<T>) iAnimation).setupAnimation(typedState, modelMap);
            }
        }
    }

    public void translateToHand(HumanoidArm sideIn, PoseStack poseStack) {
        BedrockPart arm = getArm(sideIn);
        if (arm != null) {
            arm.translateAndRotate(poseStack);
        }
    }

    public boolean hasBackpackPositioningModel() {
        return modelMap.get("backpackPositioningBone") != null;
    }

    public BedrockPart getBackpackPositioningModel() {
        return modelMap.get("backpackPositioningBone");
    }

    @Nullable
    private BedrockPart getArm(HumanoidArm sideIn) {
        return sideIn == HumanoidArm.LEFT ? modelMap.get("armLeft") : modelMap.get("armRight");
    }

    public boolean hasHead() {
        return modelMap.containsKey("head");
    }

    public BedrockPart getHead() {
        return modelMap.get("head");
    }

    public boolean hasLeftArm() {
        return modelMap.containsKey("armLeft");
    }

    public BedrockPart getLeftArm() {
        return modelMap.get("armLeft");
    }

    public boolean hasRightArm() {
        return modelMap.containsKey("armRight");
    }

    public BedrockPart getRightArm() {
        return modelMap.get("armRight");
    }

    public boolean hasArmPositioningModel(HumanoidArm side) {
        BedrockPart arm = (side == HumanoidArm.LEFT ? modelMap.get("armLeftPositioningBone") : modelMap.get("armRightPositioningBone"));
        return arm != null;
    }

    @Nullable
    public BedrockPart getArmPositioningModel(HumanoidArm side) {
        return (side == HumanoidArm.LEFT ? modelMap.get("armLeftPositioningBone") : modelMap.get("armRightPositioningBone"));
    }

    public void translateToPositioningHand(HumanoidArm sideIn, PoseStack poseStack) {
        BedrockPart arm = (sideIn == HumanoidArm.LEFT ? modelMap.get("armLeftPositioningBone") : modelMap.get("armRightPositioningBone"));
        if (arm != null) {
            arm.translateAndRotate(poseStack);
        }
    }

    public boolean hasWaistPositioningModel(HumanoidArm side) {
        BedrockPart waist = (side == HumanoidArm.LEFT ? modelMap.get("waistLeftPositioningBone") : modelMap.get("waistRightPositioningBone"));
        return waist != null;
    }

    public void translateToPositioningWaist(HumanoidArm sideIn, PoseStack poseStack) {
        BedrockPart waist = (sideIn == HumanoidArm.LEFT ? modelMap.get("waistLeftPositioningBone") : modelMap.get("waistRightPositioningBone"));
        if (waist != null) {
            waist.translateAndRotate(poseStack);
        }
    }

    public void setAnimations(@Nullable List<Object> animations) {
        this.animations = animations;
    }
}
