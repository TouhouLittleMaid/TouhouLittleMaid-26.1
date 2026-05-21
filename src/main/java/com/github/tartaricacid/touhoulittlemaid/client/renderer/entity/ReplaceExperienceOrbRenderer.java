package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.VanillaConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;

public class ReplaceExperienceOrbRenderer extends EntityRenderer<ExperienceOrb, ExperienceOrbRenderState> {
    private static final Identifier POINT_ITEM_TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/entity/point_item.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entityCutout(POINT_ITEM_TEXTURE);
    private final ExperienceOrbRenderer vanillaRender;

    public ReplaceExperienceOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
        this.vanillaRender = new ExperienceOrbRenderer(context);
    }

    @Override
    public ExperienceOrbRenderState createRenderState() {
        return new ExperienceOrbRenderState();
    }

    @Override
    public void extractRenderState(ExperienceOrb entity, ExperienceOrbRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.icon = entity.getIcon();
    }

    @Override
    protected int getBlockLightLevel(ExperienceOrb entity, BlockPos pos) {
        return Mth.clamp(super.getBlockLightLevel(entity, pos) + 7, 0, 15);
    }

    @Override
    public void submit(ExperienceOrbRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (VanillaConfig.REPLACE_XP_TEXTURE.get()) {
            renderPointItem(state, poseStack, submitNodeCollector, camera);
        } else {
            vanillaRender.submit(state, poseStack, submitNodeCollector, camera);
        }
    }

    private void renderPointItem(ExperienceOrbRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        int icon = state.icon;
        float texU1 = (float) (icon % 4 * 16) / 64.0F;
        float texU2 = (float) (icon % 4 * 16 + 16) / 64.0F;
        float texV2 = (float) (icon / 4 * 16) / 64.0F;
        float texV1 = (float) (icon / 4 * 16 + 16) / 64.0F;
        poseStack.translate(0.0F, 0.1F, 0.0F);
        poseStack.mulPose(camera.orientation);
        poseStack.scale(0.3F, 0.3F, 0.3F);
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, buffer) -> {
            vertex(buffer, pose, -0.5F, -0.25F, 255, 255, 255, texU1, texV1, state.lightCoords);
            vertex(buffer, pose, 0.5F, -0.25F, 255, 255, 255, texU2, texV1, state.lightCoords);
            vertex(buffer, pose, 0.5F, 0.75F, 255, 255, 255, texU2, texV2, state.lightCoords);
            vertex(buffer, pose, -0.5F, 0.75F, 255, 255, 255, texU1, texV2, state.lightCoords);
        });
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, int r, int g, int b, float u, float v, int lightCoords) {
        buffer.addVertex(pose, x, y, 0.0F)
                .setColor(r, g, b, 128)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
