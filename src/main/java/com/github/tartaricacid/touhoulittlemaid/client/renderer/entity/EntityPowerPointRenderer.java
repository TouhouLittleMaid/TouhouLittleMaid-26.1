package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityPowerPointRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class EntityPowerPointRenderer extends EntityRenderer<EntityPowerPoint, EntityPowerPointRenderState> {
    private static final Identifier POWER_POINT_TEXTURES = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/entity/power_point.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entityTranslucentCullItemTarget(POWER_POINT_TEXTURES);

    public EntityPowerPointRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public EntityPowerPointRenderState createRenderState() {
        return new EntityPowerPointRenderState();
    }

    @Override
    public void extractRenderState(EntityPowerPoint entity, EntityPowerPointRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.iconIndex = entity.getIcon();
    }

    @Override
    public void submit(EntityPowerPointRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        int texIndex = state.iconIndex;
        int remainder = texIndex % 4;
        int quotient = texIndex / 4;
        double texPos1 = remainder * 16 / 64.0;
        double texPos2 = (remainder * 16 + 16) / 64.0;
        double texPos3 = quotient * 16 / 64.0;
        double texPos4 = (quotient * 16 + 16) / 64.0;

        poseStack.pushPose();
        poseStack.translate(0, 0.1, 0);
        poseStack.mulPose(camera.orientation);
        poseStack.scale(0.3F, 0.3F, 0.3F);

        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, buffer) -> {
            vertex(buffer, pose, -1, -0.25, texPos1, texPos4, state.lightCoords);
            vertex(buffer, pose, 1, -0.25, texPos2, texPos4, state.lightCoords);
            vertex(buffer, pose, 1, 1.75, texPos2, texPos3, state.lightCoords);
            vertex(buffer, pose, -1, 1.75, texPos1, texPos3, state.lightCoords);
        });
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, double x, double y, double texU, double texV, int lightCoords) {
        buffer.addVertex(pose, (float) x, (float) y, 0.0F)
                .setColor(255, 255, 255, 128)
                .setUv((float) texU, (float) texV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
