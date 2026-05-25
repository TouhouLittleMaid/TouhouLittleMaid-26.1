package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GeckoLayerMaidBackpack implements GeoLayerRenderer<EntityMaidRenderState, GeckoMaidRenderData> {
    @Override
    public void submit(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, EntityMaidRenderState state, GeckoMaidRenderData data, CameraRenderState camera) {
        if (state.backpack != null && state.showBackpack) {
            data.modelState.visitLocatorGroup(GeoLocatorType.BACKPACK, poseStack, locatorPoseStack -> {
                locatorPoseStack.translate(0, 1, 0.25);
                locatorPoseStack.mulPose(Axis.ZP.rotationDegrees(180));
                IMaidBackpack backpack = state.showBackpack ? state.backpack : BackpackManager.getEmptyBackpack();
                BackpackManager.findBackpackModel(backpack.getId()).ifPresent(pair ->{
                    submitNodeCollector.submitModel(
                            pair.getLeft(),
                            state,
                            locatorPoseStack,
                            RenderTypes.entityCutout(pair.getRight()),
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            state.outlineColor,
                            null);
                });
            });
        }
    }
}