package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.MaidBackpackRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import static com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager.RENDER_DATA_CACHE;

public class GeckoLayerMaidBackpack implements GeoLayerRenderer<EntityMaidRenderState, GeckoMaidRenderData> {
    @Override
    public void submit(SubmitNodeCollector submitNode, PoseStack poseStack, EntityMaidRenderState state, GeckoMaidRenderData data, CameraRenderState camera) {
        if (state.backpack == null || !state.showBackpack) {
            return;
        }
        data.modelState.visitLocatorGroup(GeoLocatorType.BACKPACK, poseStack, locator -> {
            locator.translate(0, 1, 0.25);
            locator.mulPose(Axis.ZP.rotationDegrees(180));

            Identifier id = state.backpack.getId();
            MaidBackpackRenderData renderData = RENDER_DATA_CACHE.apply(id);
            var backpackModel = renderData.getBackpackModel();
            var backpackTexture = renderData.getBackpackTexture();
            if (backpackModel != null && backpackTexture != null) {
                submitNode.submitModel(
                        backpackModel, state, poseStack, RenderTypes.entityCutout(backpackTexture),
                        state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null
                );
            }
        });
    }
}