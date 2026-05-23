package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.compat.gun.common.GunClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class LayerMaidHeldItem extends RenderLayer<EntityMaidRenderState, EntityMaidModel> {
    public LayerMaidHeldItem(EntityMaidRenderer maidRenderer) {
        super(maidRenderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, EntityMaidRenderState state, float yRot, float xRot) {
        var model = getParentModel();
        if (!state.rightHandItemState.isEmpty() && model.hasRightArm()) {
            this.renderArmWithItem(state, state.rightHandItemState, state.rightHandItemStack, HumanoidArm.RIGHT, poseStack, submitNodeCollector);
        }
        if (!state.leftHandItemState.isEmpty() && model.hasLeftArm()) {
            this.renderArmWithItem(state, state.leftHandItemState, state.leftHandItemStack, HumanoidArm.LEFT, poseStack, submitNodeCollector);
        }
    }

    private void renderArmWithItem(EntityMaidRenderState state, ItemStackRenderState item, ItemStack itemStack, HumanoidArm handSide, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        poseStack.pushPose();
        boolean isLeft = handSide == HumanoidArm.LEFT;
        getParentModel().translateToHand(handSide, poseStack);
        if (getParentModel().hasArmPositioningModel(handSide)) {
            getParentModel().translateToPositioningHand(handSide, poseStack);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.translate(0, 0.125, -0.0625);
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.translate((isLeft ? -1 : 1) / 16.0, 0.125, -0.525);
        }
        GunClientUtil.addItemTranslate(poseStack, itemStack, isLeft);
        item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
}
