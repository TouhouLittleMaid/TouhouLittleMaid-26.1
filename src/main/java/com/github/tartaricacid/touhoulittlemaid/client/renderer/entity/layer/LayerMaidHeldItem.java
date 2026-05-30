package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;

public class LayerMaidHeldItem extends RenderLayer<EntityMaidRenderState, EntityMaidModel> {
    public LayerMaidHeldItem(EntityMaidRenderer maidRenderer) {
        super(maidRenderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNode, int light, EntityMaidRenderState state, float yRot, float xRot) {
        var model = getParentModel();
        if (!state.rightHandItemState.isEmpty() && model.hasRightArm()) {
            this.renderArmWithItem(state, state.rightHandItemState, HumanoidArm.RIGHT, poseStack, submitNode);
        }
        if (!state.leftHandItemState.isEmpty() && model.hasLeftArm()) {
            this.renderArmWithItem(state, state.leftHandItemState, HumanoidArm.LEFT, poseStack, submitNode);
        }
    }

    private void renderArmWithItem(EntityMaidRenderState state, ItemStackRenderState itemRender,
                                   HumanoidArm handSide, PoseStack poseStack, SubmitNodeCollector submitNode) {
        poseStack.pushPose();
        EntityMaidModel parentModel = this.getParentModel();

        // 依据 root 模型的位移对整体进行物品进行偏移
        if (parentModel.root() instanceof BedrockPart part) {
            poseStack.translate(part.offsetX, part.offsetY, part.offsetZ);
        }

        parentModel.translateToHand(handSide, poseStack);
        if (parentModel.hasArmPositioningModel(handSide)) {
            parentModel.translateToPositioningHand(handSide, poseStack);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.translate(0, 0.125, -0.0625);
        } else {
            boolean isLeft = handSide == HumanoidArm.LEFT;
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.translate((isLeft ? -1 : 1) / 16.0, 0.125, -0.525);
        }

        itemRender.submit(poseStack, submitNode, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
}
