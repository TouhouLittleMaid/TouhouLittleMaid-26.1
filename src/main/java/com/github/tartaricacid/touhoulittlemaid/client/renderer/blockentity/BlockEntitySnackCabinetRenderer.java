package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.block.BlockSnackCabinet;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.SnackCabinetRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntitySnackCabinet;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BlockEntitySnackCabinetRenderer implements BlockEntityRenderer<BlockEntitySnackCabinet, SnackCabinetRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/snack_cabinet.png");
    private final SimpleBedrockModel<Unit> model;
    private final BedrockPart full;
    private final BedrockPart half;

    public BlockEntitySnackCabinetRenderer(BlockEntityRendererProvider.Context context) {
        this.model = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.SNACK_CABINET);
        this.full = this.model.getPart("full");
        this.half = this.model.getPart("half");
    }

    @Override
    public SnackCabinetRenderState createRenderState() {
        return new SnackCabinetRenderState();
    }

    @Override
    public void extractRenderState(BlockEntitySnackCabinet cabinet, SnackCabinetRenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(cabinet, state, partialTick, cameraPosition, breakProgress);
        state.facing = cabinet.getBlockState().getValue(BlockSnackCabinet.FACING);
        state.type = cabinet.getBlockState().getValue(BlockSnackCabinet.TYPE);
    }

    @Override
    public void submit(SnackCabinetRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - state.facing.get2DDataValue() * 90));
        submitNodeCollector.submitCustomGeometry(
                poseStack, RenderTypes.entityCutout(TEXTURE),
                (pose, buffer) -> {
                    // 动画需要在这里，否则会因为渲染延迟问题导致出错
                    this.hideAnimation(state);

                    poseStack.pushPose();
                    poseStack.last().set(pose);
                    this.model.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
                    poseStack.popPose();
                }
        );
        poseStack.popPose();
    }

    private void hideAnimation(SnackCabinetRenderState state) {
        if (state.type == BlockSnackCabinet.TYPE_FULL) {
            this.full.visible = true;
            this.half.visible = false;
        } else if (state.type == BlockSnackCabinet.TYPE_HALF) {
            this.full.visible = false;
            this.half.visible = true;
        } else {
            this.full.visible = false;
            this.half.visible = false;
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BlockEntitySnackCabinet blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return RenderHelper.getAABB(pos.offset(0, 0, 0), pos.offset(1, 2, 1));
    }
}
