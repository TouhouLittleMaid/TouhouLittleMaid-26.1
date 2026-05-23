package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.awt.*;

public class LayerMaidBackpack extends RenderLayer<EntityMaidRenderState, EntityMaidModel> {
    public LayerMaidBackpack(EntityMaidRenderer renderer, EntityModelSet modelSet) {
        super(renderer);
        BackpackManager.initClient(modelSet);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, EntityMaidRenderState state, float yRot, float xRot) {
        if (state.backpack != null && state.mainInfo.isShowBackpack()) {
            poseStack.pushPose();
            // 稍微缩放，避免整数倍的 z-flight
            poseStack.scale(1.01f, 1.01f, 1.01f);
            // [-13, 41, 15]
            if (getParentModel().hasBackpackPositioningModel()) {
                BedrockPart renderer = getParentModel().getBackpackPositioningModel();
                poseStack.translate(renderer.x * 0.0625, 0.0625 * (renderer.y - 23 + 8), 0.0625 * (renderer.z + 4));
            } else {
                poseStack.translate(0, -0.5, 0.25);
            }
            BackpackManager.findBackpackModel(state.backpack.getId()).ifPresent(pair ->{
                submitNodeCollector.submitModel(
                        pair.getLeft(),
                        state,
                        poseStack,
                        RenderTypes.entityCutout(pair.getRight()),
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        state.outlineColor,
                        null);
            });
            poseStack.popPose();
        }
    }
}
