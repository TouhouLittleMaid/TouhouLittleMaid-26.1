package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class LayerMaidBackpack extends RenderLayer<EntityMaidRenderState, EntityMaidModel> {
    public LayerMaidBackpack(EntityMaidRenderer renderer, EntityModelSet modelSet) {
        super(renderer);
        BackpackManager.initClient(modelSet);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNode, int light, EntityMaidRenderState state, float yRot, float xRot) {
        if (!state.showBackpack || state.backpack == null || !state.modelInfo.isShowBackpack()) {
            return;
        }

        poseStack.pushPose();
        EntityMaidModel parentModel = this.getParentModel();

        // 依据 root 模型的位移对整体进行物品进行偏移
        if (parentModel.root() instanceof BedrockPart part) {
            poseStack.translate(part.offsetX, part.offsetY, part.offsetZ);
        }

        // 稍微缩放，避免整数倍的 z-flight
        poseStack.scale(1.01f, 1.01f, 1.01f);
        if (parentModel.hasBackpackPositioningModel()) {
            BedrockPart renderer = parentModel.getBackpackPositioningModel();
            poseStack.translate(renderer.x * 0.0625, 0.0625 * (renderer.y - 23 + 8), 0.0625 * (renderer.z + 4));
        } else {
            poseStack.translate(0, -0.5, 0.25);
        }

        Identifier id = state.backpack.getId();
        BackpackManager.findBackpackModel(id).ifPresent(pair -> submitNode.submitModel(
                pair.getLeft(), state, poseStack, RenderTypes.entityCutout(pair.getRight()),
                state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null)
        );
        poseStack.popPose();
    }
}
