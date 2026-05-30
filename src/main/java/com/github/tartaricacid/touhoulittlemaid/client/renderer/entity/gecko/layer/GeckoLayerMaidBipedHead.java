package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.compat.simplehats.SimpleHatsCompat;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Util;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

import java.util.Objects;
import java.util.function.Function;

public class GeckoLayerMaidBipedHead implements GeoLayerRenderer<EntityMaidRenderState, GeckoMaidRenderData> {
    private final Function<SkullBlock.Type, SkullModelBase> skullModels;
    private final PlayerSkinRenderCache playerSkinRenderCache;

    public GeckoLayerMaidBipedHead(EntityRendererProvider.Context context) {
        this.skullModels = Util.memoize(type -> Objects.requireNonNull(
                SkullBlockRenderer.createModel(context.getModelSet(), type)
        ));
        this.playerSkinRenderCache = context.getPlayerSkinRenderCache();
    }

    @Override
    public void submit(SubmitNodeCollector submitNode, PoseStack poseStack, EntityMaidRenderState state, GeckoMaidRenderData data, CameraRenderState camera) {
        var headSkull = state.wornHeadType != null;
        var headBlock = !state.headBlock.isEmpty();
        var simpleHat = !state.simpleHat.isEmpty();

        if (!headSkull && !headBlock && !simpleHat) {
            return;
        }

        data.modelState.visitLocatorGroup(GeoLocatorType.HEAD, poseStack, locator -> {
            if (headSkull) {
                poseStack.scale(-1.1875F, -1.1875F, 1.1875F);

                SkullBlock.Type type = state.wornHeadType;
                SkullModelBase skullModel = this.skullModels.apply(type);
                RenderType renderType = this.resolveSkullRenderType(state, type);

                SkullBlockRenderer.submitSkull(
                        state.wornHeadAnimationPos, poseStack, submitNode, state.lightCoords,
                        skullModel, renderType, state.outlineColor, null
                );
            }

            if (headBlock) {
                locator.scale(-0.8F, 0.8F, -0.8F);
                locator.translate(-0.5, 0.625, -0.5);
                state.headBlock.submit(locator, submitNode, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            }

            if (simpleHat) {
                SimpleHatsCompat.submit(state.simpleHat, locator, submitNode, camera);
            }
        });
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