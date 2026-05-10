package com.github.tartaricacid.touhoulittlemaid.compat.aquaculture.client;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.MaidFishingHookRenderer;
import com.github.tartaricacid.touhoulittlemaid.compat.aquaculture.entity.AquacultureFishingHook;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.teammetallurgy.aquaculture.Aquaculture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class AquacultureFishingHookRenderer extends MaidFishingHookRenderer<AquacultureFishingHook> {
    private static final Identifier BOBBER = Identifier.fromNamespaceAndPath(Aquaculture.MOD_ID, "textures/entity/rod/bobber/bobber.png");
    private static final Identifier BOBBER_OVERLAY = Identifier.fromNamespaceAndPath(Aquaculture.MOD_ID, "textures/entity/rod/bobber/bobber_overlay.png");
    private static final Identifier BOBBER_VANILLA = Identifier.fromNamespaceAndPath(Aquaculture.MOD_ID, "textures/entity/rod/bobber/bobber_vanilla.png");
    private static final Identifier HOOK = Identifier.fromNamespaceAndPath(Aquaculture.MOD_ID, "textures/entity/rod/hook/hook.png");
    private static final RenderTypes BOBBER_RENDER = RenderTypes.entityCutout(BOBBER);
    private static final RenderTypes BOBBER_OVERLAY_RENDER = RenderTypes.entityCutout(BOBBER_OVERLAY);
    private static final RenderTypes BOBBER_VANILLA_RENDER = RenderTypes.entityCutout(BOBBER_VANILLA);
    private static final RenderTypes HOOK_RENDER = RenderTypes.entityCutout(HOOK);

    public AquacultureFishingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderBobber(AquacultureFishingHook fishingHook, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        PoseStack.Pose lastedPose = poseStack.last();
        VertexConsumer consumer = fishingHook.hasBobber() ? buffer.getBuffer(BOBBER_OVERLAY_RENDER) : buffer.getBuffer(BOBBER_VANILLA_RENDER);

        // Bobber Overlay
        ItemStack bobberStack = fishingHook.getBobber();
        float bobberR = 1.0F;
        float bobberG = 1.0F;
        float bobberB = 1.0F;
        int colorInt = FastColor.ARGB32.color(193, 38, 38);
        if (!bobberStack.isEmpty()) {
            if (bobberStack.is(ItemTags.DYEABLE)) {
                DyedItemColor dyedItemColor = bobberStack.get(DataComponents.DYED_COLOR);
                if (dyedItemColor != null) {
                    colorInt = dyedItemColor.rgb();
                }
                bobberR = (float) (colorInt >> 16 & 255) / 255.0F;
                bobberG = (float) (colorInt >> 8 & 255) / 255.0F;
                bobberB = (float) (colorInt & 255) / 255.0F;
            }
        }
        vertex(consumer, lastedPose, packedLight, 0.0F, 0, 0, 1, bobberR, bobberG, bobberB);
        vertex(consumer, lastedPose, packedLight, 1.0F, 0, 1, 1, bobberR, bobberG, bobberB);
        vertex(consumer, lastedPose, packedLight, 1.0F, 1, 1, 0, bobberR, bobberG, bobberB);
        vertex(consumer, lastedPose, packedLight, 0.0F, 1, 0, 0, bobberR, bobberG, bobberB);

        // Bobber Background
        if (fishingHook.hasBobber()) {
            VertexConsumer bobberVertex = buffer.getBuffer(BOBBER_RENDER);
            renderPosTexture(bobberVertex, lastedPose, packedLight, 0.0F, 0, 0, 1);
            renderPosTexture(bobberVertex, lastedPose, packedLight, 1.0F, 0, 1, 1);
            renderPosTexture(bobberVertex, lastedPose, packedLight, 1.0F, 1, 1, 0);
            renderPosTexture(bobberVertex, lastedPose, packedLight, 0.0F, 1, 0, 0);
        }

        // Hook
        RenderTypes renderType = RenderTypes.entityCutout(fishingHook.getHook().getTexture());
        VertexConsumer hookVertex = fishingHook.hasHook() ? buffer.getBuffer(renderType) : buffer.getBuffer(HOOK_RENDER);
        renderPosTexture(hookVertex, lastedPose, packedLight, 0.0F, 0, 0, 1);
        renderPosTexture(hookVertex, lastedPose, packedLight, 1.0F, 0, 1, 1);
        renderPosTexture(hookVertex, lastedPose, packedLight, 1.0F, 1, 1, 0);
        renderPosTexture(hookVertex, lastedPose, packedLight, 0.0F, 1, 0, 0);

        poseStack.popPose();
    }

    @Override
    protected float[] getLineColor(AquacultureFishingHook fishingHook) {
        // Line color
        ItemStack line = fishingHook.getFishingLine();
        float r = 0;
        float g = 0;
        float b = 0;
        if (!line.isEmpty() && line.is(ItemTags.DYEABLE)) {
            DyedItemColor dyedItemColor = line.get(DataComponents.DYED_COLOR);
            if (dyedItemColor != null) {
                int colorInt = dyedItemColor.rgb();
                r = (float) (colorInt >> 16 & 255) / 255.0F;
                g = (float) (colorInt >> 8 & 255) / 255.0F;
                b = (float) (colorInt & 255) / 255.0F;
            }
        }
        return new float[]{r, g, b};
    }

    @Override
    @Nonnull
    public Identifier getTextureLocation(@Nonnull AquacultureFishingHook fishHook) {
        return BOBBER_VANILLA;
    }
}
