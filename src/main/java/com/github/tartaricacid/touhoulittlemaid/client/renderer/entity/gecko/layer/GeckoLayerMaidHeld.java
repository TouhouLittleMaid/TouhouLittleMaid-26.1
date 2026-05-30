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

public class GeckoLayerMaidHeld implements GeoLayerRenderer<EntityMaidRenderState, GeckoMaidRenderData> {
    @Override
    public void submit(SubmitNodeCollector submitNode, PoseStack poseStack, EntityMaidRenderState state, GeckoMaidRenderData data, CameraRenderState camera) {
        if (!state.rightHandItemState.isEmpty()) {
            data.modelState.visitLocatorGroup(GeoLocatorType.RIGHT_HAND, poseStack, locator ->
                    this.renderArmWithItem(state, state.rightHandItemState, locator, submitNode)
            );
        }
        if (!state.leftHandItemState.isEmpty()) {
            data.modelState.visitLocatorGroup(GeoLocatorType.LEFT_HAND, poseStack, locator ->
                    this.renderArmWithItem(state, state.leftHandItemState, locator, submitNode)
            );
        }
    }

    protected void renderArmWithItem(EntityMaidRenderState state, ItemStackRenderState itemRender, PoseStack poseStack, SubmitNodeCollector submitNode) {
        poseStack.translate(0, -0.0625, -0.1);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        itemRender.submit(poseStack, submitNode, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }
}