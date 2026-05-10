package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityGomoku;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityMaidBed;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.AABB;

import java.util.function.Function;

public class TileEntityMaidBedRenderer implements BlockEntityRenderer<TileEntityMaidBed> {
    private final BlockEntityRendererProvider.Context context;
    private final Function<DyeColor, SimpleBedrockModel<?>> cacheModel = Util.memoize(color -> {
        Identifier id = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/block/maid_bed/" + color.getName());
        return BedrockModelLoader.getModel(id);
    });
    private final Function<DyeColor, Identifier> cacheTexture = Util.memoize(color ->
            Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/block/maid_bed/" + color.getName() + ".png"));

    public TileEntityMaidBedRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(TileEntityMaidBed bed, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        DyeColor dyeColor = bed.getColor();
        SimpleBedrockModel<?> model = cacheModel.apply(dyeColor);
        Identifier texture = cacheTexture.apply(dyeColor);

        poseStack.pushPose();
        int rotation = bed.getBlockState().getValue(HorizontalDirectionalBlock.FACING).get2DDataValue();
        poseStack.rotateAround(Axis.YN.rotationDegrees(rotation * 90), 0.5f, 0, 0.5f);
        poseStack.translate(0.5, 1.5, -0.5);
        poseStack.scale(-1, -1, 1);
        VertexConsumer vertexConsumer;
        if (dyeColor == DyeColor.BLUE) {
            vertexConsumer = buffer.getBuffer(RenderTypes.entityTranslucent(texture));
        } else {
            vertexConsumer = buffer.getBuffer(RenderTypes.entityCutoutNoCull(texture));
        }
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(TileEntityMaidBed pBlockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TileEntityMaidBed blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return RenderHelper.getAABB(pos.offset(-2, 0, -2), pos.offset(2, 1, 2));
    }
}
