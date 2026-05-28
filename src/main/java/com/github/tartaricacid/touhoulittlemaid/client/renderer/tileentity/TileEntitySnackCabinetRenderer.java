package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.block.BlockSnackCabinet;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.SnackCabinetRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntitySnackCabinet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TileEntitySnackCabinetRenderer implements BlockEntityRenderer<TileEntitySnackCabinet, SnackCabinetRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/block/snack_cabinet.png");
    private final SimpleBedrockModel<Unit> model;
    private final BedrockPart full;
    private final BedrockPart half;

    public TileEntitySnackCabinetRenderer(BlockEntityRendererProvider.Context context) {
        this.model = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.SNACK_CABINET);
        this.full = this.model.getPart("full");
        this.half = this.model.getPart("half");
    }

    @Override
    public SnackCabinetRenderState createRenderState() {
        return new SnackCabinetRenderState();
    }

    @Override
    public void extractRenderState(TileEntitySnackCabinet cabinet, SnackCabinetRenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(cabinet, state, partialTick, cameraPosition, breakProgress);
        state.facing = cabinet.getBlockState().getValue(BlockSnackCabinet.FACING);
        state.type = cabinet.getBlockState().getValue(BlockSnackCabinet.TYPE);
    }

    @Override
    public void submit(SnackCabinetRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - state.facing.get2DDataValue() * 90));
        submitNodeCollector.submitCustomGeometry(
                poseStack, RenderTypes.entityCutout(TEXTURE),
                (pose, buffer) -> {
                    poseStack.pushPose();
                    poseStack.last().set(pose);
                    this.model.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
                    poseStack.popPose();
                }
        );
        poseStack.popPose();
    }
}
