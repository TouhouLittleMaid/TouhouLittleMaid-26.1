package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class GeckoLayerMaidBanner implements GeoLayerRenderer<EntityMaidRenderState, GeckoMaidRenderData> {
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public GeckoLayerMaidBanner(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
    }

    @Override
    public void submit(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, AnimationEvent<?> event, EntityMaidRenderState state, GeckoMaidRenderData data, CameraRenderState camera) {
        if (state.backBanner != null && data.showBackpack) {
            data.modelState.visitLocatorGroup(GeoLocatorType.BACKPACK, poseStack, locatorPoseStack -> {
                poseStack.translate(0, 0.75, 0.3);
                poseStack.scale(0.65F, -0.65F, -0.65F);
                poseStack.mulPose(Axis.YN.rotationDegrees(180));
                poseStack.mulPose(Axis.XN.rotationDegrees(5));
                blockEntityRenderDispatcher.submit(state.backBanner, locatorPoseStack, submitNodeCollector, camera);
            });
        }
    }
}
