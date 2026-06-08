package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.block.BlockShrine;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityShrine;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.ShrineRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
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

public class ShrineRenderer implements BlockEntityRenderer<BlockEntityShrine, ShrineRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/shrine.png");

    private final SimpleBedrockModel<Unit> model;
    private final ItemModelResolver resolver;

    public ShrineRenderer(BlockEntityRendererProvider.Context context) {
        model = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.SHRINE);
        resolver = context.itemModelResolver();
    }

    @Override
    public ShrineRenderState createRenderState() {
        return new ShrineRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityShrine shrine, ShrineRenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(shrine, state, partialTick, cameraPosition, breakProgress);

        state.facing = shrine.getBlockState().getValue(BlockShrine.FACING);

        ItemStack stack = shrine.getStorageItem();
        state.hasItem = !stack.isEmpty();
        if (state.hasItem && shrine.getLevel() != null) {
            state.itemRenderState.clear();
            resolver.updateForTopItem(
                    state.itemRenderState, stack, ItemDisplayContext.GROUND,
                    shrine.getLevel(), null, (int) shrine.getBlockPos().asLong()
            );
            state.itemRotation = (shrine.getLevel().getGameTime() + partialTick) % 360;
        }
    }

    @Override
    public void submit(ShrineRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        Direction facing = state.facing;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - facing.get2DDataValue() * 90));
        collector.submitModel(
                model, Unit.INSTANCE, poseStack, RenderTypes.entityCutout(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress
        );
        poseStack.popPose();

        if (state.hasItem) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.625, 0.5);
            poseStack.mulPose(Axis.YN.rotationDegrees(state.itemRotation));
            state.itemRenderState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
