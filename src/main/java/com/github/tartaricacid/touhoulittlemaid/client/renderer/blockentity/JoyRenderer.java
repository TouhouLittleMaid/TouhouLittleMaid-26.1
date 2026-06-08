package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.block.BlockJoy;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityJoy;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.JoyRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class JoyRenderer<T extends BlockEntityJoy> implements BlockEntityRenderer<T, JoyRenderState> {
    private final SimpleBedrockModel<Unit> model;
    private final Identifier texture;

    public JoyRenderer(Identifier model, Identifier texture) {
        this.model = InternalBedrockModelRegistry.getModel(model);
        this.texture = texture;
    }

    @Override
    public JoyRenderState createRenderState() {
        return new JoyRenderState();
    }

    @Override
    public void extractRenderState(T entity, JoyRenderState state, float partialTick, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
        state.facing = entity.getBlockState().getValue(BlockJoy.FACING);
    }

    @Override
    public void submit(JoyRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - state.facing.get2DDataValue() * 90));
        collector.submitModel(
                this.model, Unit.INSTANCE, poseStack, RenderTypes.entityCutout(this.texture),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress
        );
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(T te) {
        return RenderHelper.getAABB(
                te.getBlockPos().offset(-2, 0, -2),
                te.getBlockPos().offset(2, 1, 2)
        );
    }
}
