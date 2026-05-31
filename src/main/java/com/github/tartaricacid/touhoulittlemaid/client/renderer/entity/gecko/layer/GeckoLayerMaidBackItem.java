package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.MaidBackpackRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import static com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager.RENDER_DATA_CACHE;

public class GeckoLayerMaidBackItem implements GeoLayerRenderer<EntityMaidRenderState, GeckoMaidRenderData> {
    @Override
    public void submit(SubmitNodeCollector submitNode, PoseStack poseStack, EntityMaidRenderState state,
                       GeckoMaidRenderData data, CameraRenderState camera) {
        if (state.backItem.isEmpty() || state.backpack == null) {
            return;
        }
        if (data.modelState.locatorGroupSize(GeoLocatorType.BACKPACK) > 0) {
            data.modelState.visitLocatorGroup(GeoLocatorType.BACKPACK, poseStack,
                    locator -> renderBackItem(submitNode, locator, state));
        } else {
            renderBackItem(submitNode, poseStack, state);
        }
    }

    public void renderBackItem(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, EntityMaidRenderState state) {
        assert state.backpack != null;

        Identifier id = state.backpack.getId();
        MaidBackpackRenderData renderData = RENDER_DATA_CACHE.apply(id);

        poseStack.translate(0, 1, 0.25);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.translate(0, 0.5, -0.25);
        renderData.offsetBackpackItem(poseStack);
        state.backItem.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }
}
