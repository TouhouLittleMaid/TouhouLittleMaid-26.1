package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.AltarRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityAltar;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class AltarRenderer implements BlockEntityRenderer<BlockEntityAltar, AltarRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/altar.png");
    private final SimpleBedrockModel<Unit> model;
    private final ItemModelResolver itemModelResolver;

    public AltarRenderer(BlockEntityRendererProvider.Context context) {
        this.model = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.ALTAR);
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public AltarRenderState createRenderState() {
        return new AltarRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityAltar te, AltarRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
        state.renderModel = te.isRender();
        state.direction = te.getDirection();
        state.canPlaceItem = te.isCanPlaceItem();
        ItemStack stack = state.canPlaceItem ? te.getStorageItem() : ItemStack.EMPTY;
        state.hasItem = !stack.isEmpty();
        if (state.hasItem) {
            state.itemRenderState.clear();
            itemModelResolver.updateForTopItem(state.itemRenderState, stack, ItemDisplayContext.GROUND,
                    te.getLevel(), null, (int) te.getBlockPos().asLong());
        }
    }

    @Override
    public void submit(AltarRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.renderModel) {
            poseStack.pushPose();
            this.setTranslateAndPose(state.direction, poseStack);
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            RenderType renderType = RenderTypes.entityTranslucent(TEXTURE);
            submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                poseStack.pushPose();
                poseStack.last().set(pose);
                model.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            });
            poseStack.popPose();
        }

        if (state.hasItem) {
            poseStack.pushPose();
            double time = (System.currentTimeMillis() + state.blockPos.asLong()) % 3600;
            poseStack.translate(0.5, 1.25 + Math.sin(time / 1800 * Math.PI) * 0.1, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) time / 10));
            state.itemRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    private void setTranslateAndPose(Direction direction, PoseStack poseStack) {
        switch (direction) {
            case SOUTH:
                poseStack.translate(1, -1.5, -3);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                break;
            case EAST:
                poseStack.translate(-3, -1.5, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(270));
                break;
            case WEST:
                poseStack.translate(4, -1.5, 1);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                break;
            case NORTH:
            default:
                poseStack.translate(0, -1.5, 4);
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BlockEntityAltar te) {
        return RenderHelper.getAABB(
                te.getBlockPos().offset(-9, -5, -9),
                te.getBlockPos().offset(9, 5, 9)
        );
    }
}
