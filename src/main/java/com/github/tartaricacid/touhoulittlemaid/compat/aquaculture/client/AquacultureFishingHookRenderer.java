package com.github.tartaricacid.touhoulittlemaid.compat.aquaculture.client;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.MaidFishingHookRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.MaidFishingHookRenderState;
import com.github.tartaricacid.touhoulittlemaid.compat.aquaculture.entity.AquacultureFishingHook;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.MaidFishingHook;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammetallurgy.aquaculture.Aquaculture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class AquacultureFishingHookRenderer extends MaidFishingHookRenderer<AquacultureFishingHook, AquacultureFishingHookRenderState> {
    private static final int DEFAULT_BOBBER_COLOR = 0xC12626;
    private static final Identifier BOBBER = Identifier.fromNamespaceAndPath(Aquaculture.MOD_ID, "textures/entity/rod/bobber/bobber.png");
    private static final Identifier BOBBER_OVERLAY = Identifier.fromNamespaceAndPath(Aquaculture.MOD_ID, "textures/entity/rod/bobber/bobber_overlay.png");
    private static final Identifier BOBBER_VANILLA = Identifier.fromNamespaceAndPath(Aquaculture.MOD_ID, "textures/entity/rod/bobber/bobber_vanilla.png");
    private static final Identifier HOOK = Identifier.fromNamespaceAndPath(Aquaculture.MOD_ID, "textures/entity/rod/hook/hook.png");
    private static final RenderType BOBBER_RENDER = RenderTypes.entityCutout(BOBBER);
    private static final RenderType BOBBER_OVERLAY_RENDER = RenderTypes.entityCutout(BOBBER_OVERLAY);
    private static final RenderType BOBBER_VANILLA_RENDER = RenderTypes.entityCutout(BOBBER_VANILLA);
    private static final RenderType HOOK_RENDER = RenderTypes.entityCutout(HOOK);

    public AquacultureFishingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public AquacultureFishingHookRenderState createRenderState() {
        return new AquacultureFishingHookRenderState();
    }

    @Override
    public void extractRenderState(AquacultureFishingHook entity, AquacultureFishingHookRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.hasBobber = entity.hasBobber();
        state.hasHook = entity.hasHook();
        state.hookTexture = state.hasHook ? entity.getHook().getTexture() : HOOK;

        float[] bobberColor = getColor(entity.getBobber(), DEFAULT_BOBBER_COLOR);
        state.bobberColorR = bobberColor[0];
        state.bobberColorG = bobberColor[1];
        state.bobberColorB = bobberColor[2];
    }

    @Override
    protected void renderBobber(@NotNull MaidFishingHookRenderState baseState, @NotNull PoseStack poseStack,
                                @NotNull SubmitNodeCollector submitNodeCollector, @NotNull CameraRenderState camera) {
        AquacultureFishingHookRenderState state = (AquacultureFishingHookRenderState) baseState;
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(camera.orientation);
        submitNodeCollector.submitCustomGeometry(poseStack, state.hasBobber ? BOBBER_OVERLAY_RENDER : BOBBER_VANILLA_RENDER, (pose, buffer) -> {
            vertex(buffer, pose, state.lightCoords, 0.0F, 0, 0, 1, state.bobberColorR, state.bobberColorG, state.bobberColorB);
            vertex(buffer, pose, state.lightCoords, 1.0F, 0, 1, 1, state.bobberColorR, state.bobberColorG, state.bobberColorB);
            vertex(buffer, pose, state.lightCoords, 1.0F, 1, 1, 0, state.bobberColorR, state.bobberColorG, state.bobberColorB);
            vertex(buffer, pose, state.lightCoords, 0.0F, 1, 0, 0, state.bobberColorR, state.bobberColorG, state.bobberColorB);
        });

        if (state.hasBobber) {
            submitNodeCollector.submitCustomGeometry(poseStack, BOBBER_RENDER, (pose, buffer) -> {
                vertex(buffer, pose, state.lightCoords, 0.0F, 0, 0, 1);
                vertex(buffer, pose, state.lightCoords, 1.0F, 0, 1, 1);
                vertex(buffer, pose, state.lightCoords, 1.0F, 1, 1, 0);
                vertex(buffer, pose, state.lightCoords, 0.0F, 1, 0, 0);
            });
        }

        RenderType hookRenderType = state.hasHook ? RenderTypes.entityCutout(state.hookTexture) : HOOK_RENDER;
        submitNodeCollector.submitCustomGeometry(poseStack, hookRenderType, (pose, buffer) -> {
            vertex(buffer, pose, state.lightCoords, 0.0F, 0, 0, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 0, 1, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 1, 1, 0);
            vertex(buffer, pose, state.lightCoords, 0.0F, 1, 0, 0);
        });
        poseStack.popPose();
    }

    @Override
    protected float @NotNull [] getLineColor(@NotNull MaidFishingHook fishingHook) {
        if (fishingHook instanceof AquacultureFishingHook hook) {
            return getColor(hook.getFishingLine(), 0);
        }
        return super.getLineColor(fishingHook);
    }

    private static float[] getColor(ItemStack stack, int defaultColor) {
        int color = defaultColor;
        if (!stack.isEmpty()) {
            DyedItemColor dyedItemColor = stack.get(DataComponents.DYED_COLOR);
            if (dyedItemColor != null) {
                color = dyedItemColor.rgb();
            }
        }
        return new float[]{
                ((color >> 16) & 255) / 255.0F,
                ((color >> 8) & 255) / 255.0F,
                (color & 255) / 255.0F
        };
    }
}
