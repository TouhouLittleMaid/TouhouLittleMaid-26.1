package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;

public class GeckoLayerMaidHeld implements GeoLayerRenderer<EntityMaidRenderState, GeckoMaidRenderData> {
    @Override
    public void submit(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, EntityMaidRenderState state, GeckoMaidRenderData data, CameraRenderState camera) {
        if (!state.rightHandItemState.isEmpty()) {
            data.modelState.visitLocatorGroup(GeoLocatorType.RIGHT_HAND, poseStack, locatorPoseStack -> {
                this.renderArmWithItem(state, state.rightHandItemState, HumanoidArm.RIGHT, locatorPoseStack, submitNodeCollector);
            });
        }
        if (!state.leftHandItemState.isEmpty()) {
            data.modelState.visitLocatorGroup(GeoLocatorType.LEFT_HAND, poseStack, locatorPoseStack -> {
                this.renderArmWithItem(state, state.leftHandItemState, HumanoidArm.LEFT, locatorPoseStack, submitNodeCollector);
            });
        }
    }

    protected void renderArmWithItem(EntityMaidRenderState state, ItemStackRenderState item, HumanoidArm arm,
                                     PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        poseStack.translate(0, -0.0625, -0.1);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }
}