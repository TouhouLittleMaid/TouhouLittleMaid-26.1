package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.block.BlockGomoku;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.ShrineRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityShrine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BlockEntityShrineRenderer implements BlockEntityRenderer<BlockEntityShrine, ShrineRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/shrine.png");
    private final SimpleBedrockModel<Unit> model;
    private final ItemModelResolver itemModelResolver;

    public BlockEntityShrineRenderer(BlockEntityRendererProvider.Context context) {
        model = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.SHRINE);
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ShrineRenderState createRenderState() {
        return new ShrineRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityShrine shrine, ShrineRenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(shrine, state, partialTick, cameraPosition, breakProgress);
        state.facing = shrine.getBlockState().getValue(BlockGomoku.FACING);
        ItemStack stack = shrine.getStorageItem();
        state.hasItem = !stack.isEmpty();
        if (state.hasItem && shrine.getLevel() != null) {
            state.itemRenderState.clear();
            itemModelResolver.updateForTopItem(state.itemRenderState, stack, ItemDisplayContext.GROUND,
                    shrine.getLevel(), null, (int) shrine.getBlockPos().asLong());
            state.itemRotation = (shrine.getLevel().getGameTime() + partialTick) % 360;
        }
    }

    @Override
    public void submit(ShrineRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Direction facing = state.facing;
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - facing.get2DDataValue() * 90));
        RenderType renderType = RenderTypes.entityCutout(TEXTURE);
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            model.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        });
        poseStack.popPose();

        if (state.hasItem) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.85, 0.5);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.YN.rotationDegrees(state.itemRotation));
            state.itemRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
