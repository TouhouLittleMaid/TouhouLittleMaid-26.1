package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityMaidBed;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.MaidBedRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
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
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class MaidBedRenderer implements BlockEntityRenderer<BlockEntityMaidBed, MaidBedRenderState> {
    private final Function<DyeColor, SimpleBedrockModel<Unit>> cacheModel = Util.memoize(color -> {
        String path = "bedrock/block/maid_bed/%s".formatted(color.getName());
        Identifier id = IdentifierUtil.modLoc(path);
        return InternalBedrockModelRegistry.getModel(id);
    });

    private final Function<DyeColor, Identifier> cacheTexture = Util.memoize(color -> {
        String path = "textures/bedrock/block/maid_bed/%s.png".formatted(color.getName());
        return IdentifierUtil.modLoc(path);
    });

    public MaidBedRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public MaidBedRenderState createRenderState() {
        return new MaidBedRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityMaidBed bed, MaidBedRenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(bed, state, partialTick, cameraPosition, breakProgress);
        state.dyeColor = bed.getColor();
        state.rotation = bed.getBlockState()
                .getValue(HorizontalDirectionalBlock.FACING)
                .get2DDataValue();
    }

    @Override
    public void submit(MaidBedRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        DyeColor dyeColor = state.dyeColor;
        SimpleBedrockModel<Unit> model = cacheModel.apply(dyeColor);
        Identifier texture = cacheTexture.apply(dyeColor);

        poseStack.pushPose();
        poseStack.rotateAround(Axis.YN.rotationDegrees(state.rotation * 90), 0.5f, 0, 0.5f);
        poseStack.translate(0.5, 1.5, -0.5);
        poseStack.scale(-1, -1, 1);

        collector.submitModel(
                model, Unit.INSTANCE, poseStack, getRenderType(dyeColor, texture),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress
        );

        poseStack.popPose();
    }

    private RenderType getRenderType(DyeColor dyeColor, Identifier texture) {
        RenderType renderType;
        // 蓝色床是半透明材质，需要额外判断
        if (dyeColor == DyeColor.BLUE) {
            renderType = RenderTypes.entityTranslucent(texture);
        } else {
            renderType = RenderTypes.entityCutout(texture);
        }
        return renderType;
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BlockEntityMaidBed bed) {
        return RenderHelper.getAABB(
                bed.getBlockPos().offset(-2, 0, -2),
                bed.getBlockPos().offset(2, 1, 2)
        );
    }
}
