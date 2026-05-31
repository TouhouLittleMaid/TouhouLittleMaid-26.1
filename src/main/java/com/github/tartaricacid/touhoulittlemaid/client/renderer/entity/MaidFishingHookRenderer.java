package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.MaidFishingHookRenderState;
import com.github.tartaricacid.touhoulittlemaid.compat.oculus.OculusCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.MaidFishingHook;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MaidFishingHookRenderer<T extends MaidFishingHook, S extends MaidFishingHookRenderState> extends EntityRenderer<T, S> {
    private static final Identifier TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/fishing/fishing_hook.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entityCutoutCull(TEXTURE_LOCATION);

    public MaidFishingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public S createRenderState() {
        return (S) new MaidFishingHookRenderState();
    }

    @Override
    public void extractRenderState(T entity, S state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        EntityMaid maid = entity.getMaidOwner();
        if (maid == null) {
            return;
        }

        // 计算女仆手部位置
        float lerpBodyRot;
        if (maid.getVehicle() instanceof LivingEntity vehicle) {
            lerpBodyRot = Mth.lerp(partialTicks, vehicle.yBodyRotO, vehicle.yBodyRot) * ((float) Math.PI / 180F);
        } else {
            lerpBodyRot = Mth.lerp(partialTicks, maid.yBodyRotO, maid.yBodyRot) * ((float) Math.PI / 180F);
        }
        double sin = Mth.sin(lerpBodyRot);
        double cos = Mth.cos(lerpBodyRot);

        double x1 = Mth.lerp(partialTicks, maid.xo, maid.getX()) - cos * 0.35D - sin * 0.8D;
        double y1 = Mth.lerp(partialTicks, maid.yo, maid.getY()) + maid.getEyeHeight() - 0.45D;
        double z1 = Mth.lerp(partialTicks, maid.zo, maid.getZ()) - sin * 0.35D + cos * 0.8D;

        // 钓鱼竿位置（已由 super 处理 lerp）
        double x2 = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double y2 = Mth.lerp(partialTicks, entity.yo, entity.getY()) + 0.25D;
        double z2 = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        float offsetX = (float) (x1 - x2);
        float offsetY = (float) (y1 - y2) - 0.1875F;
        float offsetZ = (float) (z1 - z2);
        state.lineOriginOffset = new Vec3(offsetX, offsetY, offsetZ);

        // 鱼线颜色
        float[] colors = getLineColor(entity);
        state.lineColorR = colors[0];
        state.lineColorG = colors[1];
        state.lineColorB = colors[2];
    }

    @Override
    public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.lineOriginOffset.equals(Vec3.ZERO) && state.lineColorR == 0 && state.lineColorG == 0 && state.lineColorB == 0) {
            return;
        }
        poseStack.pushPose();
        renderBobber(state, poseStack, submitNodeCollector, camera);
        renderFishingLine(state, poseStack, submitNodeCollector);
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    protected void renderBobber(MaidFishingHookRenderState state, PoseStack poseStack,
                                SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(camera.orientation);
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, buffer) -> {
            vertex(buffer, pose, state.lightCoords, 0.0F, 0, 0, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 0, 1, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 1, 1, 0);
            vertex(buffer, pose, state.lightCoords, 0.0F, 1, 0, 0);
        });
        poseStack.popPose();
    }

    protected float[] getLineColor(MaidFishingHook fishingHook) {
        return new float[]{0f, 0f, 0f};
    }

    protected void renderFishingLine(MaidFishingHookRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        float xa = (float) state.lineOriginOffset.x;
        float ya = (float) state.lineOriginOffset.y;
        float za = (float) state.lineOriginOffset.z;
        float[] colors = new float[]{state.lineColorR, state.lineColorG, state.lineColorB};
        float width = Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth;

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, buffer) -> {
            for (int i = 0; i <= 16; ++i) {
                float fraction1 = fraction(i);
                float fraction2 = fraction(i + 1);
                stringVertex(xa, ya, za, width, buffer, pose, fraction1, fraction2, colors[0], colors[1], colors[2]);
                stringVertex(xa, ya, za, width, buffer, pose, fraction2, fraction1, colors[0], colors[1], colors[2]);
            }
            if (OculusCompat.isOculusInstalled()) {
                buffer.addVertex(pose, 0.0f, 0.0f, 0.0f)
                        .setColor(0, 0, 0, 255)
                        .setNormal(pose, 0.0F, 0.0F, 0.0F);
            }
        });
    }

    protected float fraction(int numerator) {
        return (float) numerator / (float) 16;
    }

    protected static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int lightMapUV,
                                 float pX, int pY, int pU, int pV) {
        consumer.addVertex(pose, pX - 0.5F, pY - 0.5F, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv((float) pU, (float) pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightMapUV)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    protected static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int lightMapUV,
                                 float pX, int pY, int pU, int pV,
                                 float r, float g, float b) {
        consumer.addVertex(pose, pX - 0.5F, pY - 0.5F, 0.0F)
                .setColor(r, g, b, 1.0F)
                .setUv((float) pU, (float) pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightMapUV)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    protected static void stringVertex(float pX, float pY, float pZ, float width,
                                       VertexConsumer consumer, PoseStack.Pose pose,
                                       float fraction1, float fraction2,
                                       float r, float g, float b) {
        float x = pX * fraction1;
        float y = pY * (fraction1 * fraction1 + fraction1) * 0.5F + 0.25F;
        float z = pZ * fraction1;

        float nx = pX * fraction2 - x;
        float ny = pY * (fraction2 * fraction2 + fraction2) * 0.5F + 0.25F - y;
        float nz = pZ * fraction2 - z;
        float sqrt = Mth.sqrt(nx * nx + ny * ny + nz * nz);

        nx /= sqrt;
        ny /= sqrt;
        nz /= sqrt;

        consumer.addVertex(pose, x, y, z)
                .setLineWidth(width)
                .setColor(r, g, b, 1.0F)
                .setNormal(pose, nx, ny, nz);
    }
}
