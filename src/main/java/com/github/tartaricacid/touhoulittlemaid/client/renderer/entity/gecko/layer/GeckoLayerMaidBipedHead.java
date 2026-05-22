package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.compat.simplehats.SimpleHatsCompat;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GeckoLayerMaidBipedHead implements GeoLayerRenderer<EntityMaidRenderState, GeckoMaidRenderData> {
    private final BlockEntityRenderDispatcher beRenderDispatcher;

    public GeckoLayerMaidBipedHead(BlockEntityRenderDispatcher beRenderDispatcher) {
        this.beRenderDispatcher = beRenderDispatcher;
    }

    @Override
    public void submit(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, AnimationEvent<?> event, EntityMaidRenderState state, GeckoMaidRenderData data, CameraRenderState camera) {
        var headSkull = state.headSkull != null;
        var headBlock = !state.headBlock.isEmpty();
        var simpleHat = !state.simpleHat.isEmpty();
        if (headSkull || headBlock || simpleHat) {
            data.modelState.visitLocatorGroup(GeoLocatorType.HEAD, poseStack, locatorPoseStack -> {
                if (headSkull) {
                    poseStack.scale(-1.1875F, 1.1875F, -1.1875F);
                    poseStack.translate(-0.5D, 0.0D, -0.5D);
                    beRenderDispatcher.submit(state.headSkull, locatorPoseStack, submitNodeCollector, camera);
                }
                if (headBlock) {
                    poseStack.scale(-0.8F, 0.8F, -0.8F);
                    poseStack.translate(-0.5, 0.625, -0.5);
                    state.headBlock.submit(locatorPoseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
                }
                if (simpleHat) {
                    SimpleHatsCompat.submit(state.simpleHat, locatorPoseStack, submitNodeCollector, camera);
                }
            });
        }
    }
}