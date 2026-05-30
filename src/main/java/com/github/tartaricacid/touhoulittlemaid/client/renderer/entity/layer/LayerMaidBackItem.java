package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class LayerMaidBackItem extends RenderLayer<EntityMaidRenderState, EntityMaidModel> {
    public LayerMaidBackItem(EntityMaidRenderer renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNode, int light, EntityMaidRenderState state, float yRot, float xRot) {
        if (!state.backItem.isEmpty() && state.backpack != null) {
            poseStack.pushPose();
            EntityMaidModel parentModel = this.getParentModel();

            // 依据 root 模型的位移对整体进行物品进行偏移
            if (parentModel.root() instanceof BedrockPart part) {
                poseStack.translate(part.offsetX, part.offsetY, part.offsetZ);
            }

            // 如果有背包，那么和背包适配位移
            if (parentModel.hasBackpackPositioningModel()) {
                BedrockPart renderer = parentModel.getBackpackPositioningModel();
                poseStack.translate(renderer.x * 0.0625, 0.0625 * (renderer.y - 23 + 8), 0.0625 * (renderer.z + 4));
            } else {
                poseStack.translate(0, -0.5, 0.25);
            }

            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.translate(0, 0.5, -0.25);
            state.backpack.offsetBackpackItem(poseStack);
            state.backItem.submit(poseStack, submitNode, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

            poseStack.popPose();
        }
    }
}