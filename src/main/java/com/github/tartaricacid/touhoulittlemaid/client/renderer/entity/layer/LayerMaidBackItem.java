package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;

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
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, EntityMaidRenderState state, float yRot, float xRot) {
        // TODO: 枪械额外渲染兼容
        // GunClientUtil.renderBackGun(matrixStack, bufferIn, packedLightIn, stack, maid);

        if (state.showBackpack && !state.backItem.isEmpty() && state.backpack != null) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.translate(0, 0.5, -0.25);
            state.backpack.offsetBackpackItem(poseStack);
            state.backItem.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }
    }
}