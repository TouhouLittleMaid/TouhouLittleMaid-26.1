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
            data.modelState.visitLocatorGroup(GeoLocatorType.BACKPACK, poseStack, locatorPoseStack -> {
                // TODO: 判断枪
                locatorPoseStack.translate(0, 1, 0.25);
                locatorPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
                locatorPoseStack.translate(0, 0.5, -0.25);
                state.backpack.offsetBackpackItem(locatorPoseStack);;
                state.backItem.submit(locatorPoseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            });
        }
    }
}
