package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.MaidBedRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityMaidBed;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class TileEntityMaidBedRenderer implements BlockEntityRenderer<TileEntityMaidBed, MaidBedRenderState> {
    private final Function<DyeColor, SimpleBedrockModel<Unit>> cacheModel = Util.memoize(color -> {
        Identifier id = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/block/maid_bed/" + color.getName());
        return InternalBedrockModelRegistry.getModel(id);
    });
    private final Function<DyeColor, Identifier> cacheTexture = Util.memoize(color ->
            Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/block/maid_bed/" + color.getName() + ".png"));

    public TileEntityMaidBedRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public MaidBedRenderState createRenderState() {
        return new MaidBedRenderState();
    }

    @Override
    public void extractRenderState(TileEntityMaidBed bed, MaidBedRenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(bed, state, partialTick, cameraPosition, breakProgress);
        state.dyeColor = bed.getColor();
        state.rotation = bed.getBlockState().getValue(HorizontalDirectionalBlock.FACING).get2DDataValue();
    }

    @Override
    public void submit(MaidBedRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        DyeColor dyeColor = state.dyeColor;
        SimpleBedrockModel<Unit> model = cacheModel.apply(dyeColor);
        Identifier texture = cacheTexture.apply(dyeColor);

        poseStack.pushPose();
        poseStack.rotateAround(Axis.YN.rotationDegrees(state.rotation * 90), 0.5f, 0, 0.5f);
        poseStack.translate(0.5, 1.5, -0.5);
        poseStack.scale(-1, -1, 1);
        RenderType renderType;
        if (dyeColor == DyeColor.BLUE) {
            renderType = RenderTypes.entityTranslucent(texture);
        } else {
            renderType = RenderTypes.entityCutout(texture);
        }
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) ->
                model.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY));
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TileEntityMaidBed blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return RenderHelper.getAABB(pos.offset(-2, 0, -2), pos.offset(2, 1, 2));
    }
}
