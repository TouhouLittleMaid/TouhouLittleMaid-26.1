package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.compat.simplehats.SimpleHatsCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Util;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

import java.util.Objects;
import java.util.function.Function;

public class LayerMaidBipedHead extends RenderLayer<EntityMaidRenderState, EntityMaidModel> {
    private final Function<SkullBlock.Type, SkullModelBase> skullModels;
    private final PlayerSkinRenderCache playerSkinRenderCache;

    public LayerMaidBipedHead(EntityMaidRenderer maidRenderer, EntityRendererProvider.Context context) {
        super(maidRenderer);
        this.skullModels = Util.memoize(type -> Objects.requireNonNull(
                SkullBlockRenderer.createModel(context.getModelSet(), type)
        ));
        this.playerSkinRenderCache = context.getPlayerSkinRenderCache();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNode, int light, EntityMaidRenderState state, float yRot, float xRot) {
        EntityMaidModel parentModel = this.getParentModel();
        boolean allowRenderHead = state.modelInfo.isShowCustomHead() && parentModel.hasHead();
        if (!allowRenderHead) {
            return;
        }

        poseStack.pushPose();

        // 依据 root 模型的位移对整体进行物品进行偏移
        if (parentModel.root() instanceof BedrockPart part) {
            poseStack.translate(part.offsetX, part.offsetY, part.offsetZ);
        }

        if (state.wornHeadType != null) {
            poseStack.pushPose();
            parentModel.getHead().translateAndRotate(poseStack);
            poseStack.scale(1.1875F, 1.1875F, 1.1875F);

            SkullBlock.Type type = state.wornHeadType;
            SkullModelBase skullModel = this.skullModels.apply(type);
            RenderType renderType = this.resolveSkullRenderType(state, type);

            SkullBlockRenderer.submitSkull(
                    state.wornHeadAnimationPos, poseStack, submitNode, light,
                    skullModel, renderType, state.outlineColor, null
            );
            poseStack.popPose();
        }

        if (!state.headBlock.isEmpty()) {
            poseStack.pushPose();
            parentModel.getHead().translateAndRotate(poseStack);
            poseStack.scale(0.8F, -0.8F, -0.8F);
            poseStack.translate(-0.5, 0.625, -0.5);
            state.headBlock.submit(poseStack, submitNode, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }

        if (!state.simpleHat.isEmpty()) {
            SimpleHatsCompat.submit(state.simpleHat, poseStack, submitNode, state.camera);
        }

        poseStack.popPose();
    }

    private RenderType resolveSkullRenderType(LivingEntityRenderState state, SkullBlock.Type type) {
        if (type != SkullBlock.Types.PLAYER) {
            return SkullBlockRenderer.getSkullRenderType(type, null);
        }
        ResolvableProfile profile = state.wornHeadProfile;
        if (profile != null) {
            return this.playerSkinRenderCache.getOrDefault(profile).renderType();
        }
        return SkullBlockRenderer.getSkullRenderType(type, null);
    }
}
