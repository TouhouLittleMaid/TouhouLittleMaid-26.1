package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.compat.simplehats.SimpleHatsCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class LayerMaidBipedHead extends RenderLayer<EntityMaidRenderState, EntityMaidModel> {
    private final EntityMaidRenderer maidRenderer;
    private final BlockEntityRenderDispatcher beRenderDispatcher;

    public LayerMaidBipedHead(EntityMaidRenderer maidRenderer, BlockEntityRenderDispatcher beRenderDispatcher) {
        super(maidRenderer);
        this.maidRenderer = maidRenderer;
        this.beRenderDispatcher = beRenderDispatcher;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, EntityMaidRenderState state, float yRot, float xRot) {
        boolean allowRenderHead = state.mainInfo.isShowCustomHead() && getParentModel().hasHead();
        if (!allowRenderHead) {
            return;
        }

        if (state.headSkull != null) {
            poseStack.pushPose();
            this.getParentModel().getHead().translateAndRotate(poseStack);
            poseStack.scale(1.1875F, -1.1875F, -1.1875F);
            poseStack.translate(-0.5D, 0.0D, -0.5D);
            beRenderDispatcher.submit(state.headSkull, poseStack, submitNodeCollector, maidRenderer.getCameraRenderState());
        }
        if (!state.headBlock.isEmpty()) {
            poseStack.pushPose();
            this.getParentModel().getHead().translateAndRotate(poseStack);
            poseStack.scale(0.8F, -0.8F, -0.8F);
            poseStack.translate(-0.5, 0.625, -0.5);
            state.headBlock.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }

        if (!state.simpleHat.isEmpty()) {
            SimpleHatsCompat.submit(state.simpleHat, poseStack, submitNodeCollector, maidRenderer.getCameraRenderState());
        }
    }
}
