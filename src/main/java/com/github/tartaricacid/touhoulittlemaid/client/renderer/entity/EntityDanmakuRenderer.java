package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityDanmakuRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityDanmaku;
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


public class EntityDanmakuRenderer extends EntityRenderer<EntityDanmaku, EntityDanmakuRenderState> {
    private static final Identifier DANMAKU_TEXTURE = IdentifierUtil.modLoc("textures/entity/danmaku.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entityTranslucentCullItemTarget(DANMAKU_TEXTURE);
    private static final int TEX_WIDTH = 416;
    private static final int TEX_HEIGHT = 128;
    private static final int CELL_SIZE = 32;

    public EntityDanmakuRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public EntityDanmakuRenderState createRenderState() {
        return new EntityDanmakuRenderState();
    }

    @Override
    public void extractRenderState(EntityDanmaku entity, EntityDanmakuRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.color = entity.getColor();
        state.type = entity.getDanmakuType();
    }

    @Override
    public void submit(EntityDanmakuRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.color != null && state.type != null) {
            // 依据类型颜色计算纹理 UV 坐标
            double startU = CELL_SIZE * state.color.ordinal();
            double startV = CELL_SIZE * state.type.ordinal();
            double size = state.type.getSize();

            poseStack.pushPose();
            poseStack.translate(0, 0.1, 0);
            poseStack.mulPose(camera.orientation);

            submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, buffer) -> {
                vertex(buffer, pose, state.lightCoords, -size, size, (startU + 0) / TEX_WIDTH, (startV + 0) / TEX_HEIGHT);
                vertex(buffer, pose, state.lightCoords, -size, -size, (startU + 0) / TEX_WIDTH, (startV + CELL_SIZE) / TEX_HEIGHT);
                vertex(buffer, pose, state.lightCoords, size, -size, (startU + CELL_SIZE) / TEX_WIDTH, (startV + CELL_SIZE) / TEX_HEIGHT);
                vertex(buffer, pose, state.lightCoords, size, size, (startU + CELL_SIZE) / TEX_WIDTH, (startV + 0) / TEX_HEIGHT);
            });

            poseStack.popPose();
        }
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, int lightCoords,
                               double x, double y, double texU, double texV) {
        buffer.addVertex(pose, (float) x, (float) y, 0.0F)
                .setColor(-1)
                .setUv((float) texU, (float) texV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
