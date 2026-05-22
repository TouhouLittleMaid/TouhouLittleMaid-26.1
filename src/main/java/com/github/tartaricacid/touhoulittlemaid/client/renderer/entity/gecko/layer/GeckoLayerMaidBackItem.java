package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GeckoLayerMaidBackItem implements GeoLayerRenderer<EntityMaidRenderState, GeckoMaidRenderData> {
    @Override
    public void submit(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, AnimationEvent<?> event, EntityMaidRenderState state, GeckoMaidRenderData data, CameraRenderState camera) {
        if (!state.backItem.isEmpty() && state.backpack != null) {
            if (data.modelState.locatorGroupSize(GeoLocatorType.BACKPACK) > 0) {
                data.modelState.visitLocatorGroup(GeoLocatorType.BACKPACK, poseStack, locatorPoseStack -> {
                    renderBackItem(submitNodeCollector, locatorPoseStack, state);
                });
            } else {
                renderBackItem(submitNodeCollector, poseStack, state);
            }
        }
    }

    public void renderBackItem(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, EntityMaidRenderState state) {
        // TODO: 判断枪
        poseStack.translate(0, 1, 0.25);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.translate(0, 0.5, -0.25);
        state.backpack.offsetBackpackItem(poseStack);;
        state.backItem.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }
}
