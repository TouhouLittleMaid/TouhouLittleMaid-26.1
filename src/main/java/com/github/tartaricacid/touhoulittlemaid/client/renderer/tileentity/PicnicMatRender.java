package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.block.BlockPicnicMat;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.PicnicMatRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityPicnicMat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PicnicMatRender implements BlockEntityRenderer<TileEntityPicnicMat, PicnicMatRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/block/picnic_mat.png");
    private final SimpleBedrockModel<Unit> model;
    private final ItemModelResolver itemModelResolver;

    public PicnicMatRender(BlockEntityRendererProvider.Context context) {
        this.model = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.PICNIC_MAT);
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public PicnicMatRenderState createRenderState() {
        return new PicnicMatRenderState();
    }

    @Override
    public void extractRenderState(TileEntityPicnicMat picnicMat, PicnicMatRenderState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(picnicMat, state, partialTick, cameraPos, breakProgress);
        state.isCenter = picnicMat.getBlockState().getValue(BlockPicnicMat.PART).isCenter();
        state.facing = picnicMat.getBlockState().getValue(BlockPicnicMat.FACING);
        for (int i = 0; i < 9; i++) {
            state.slotItems[i] = picnicMat.getStorageItem(i);
        }
    }

    @Override
    public void submit(PicnicMatRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.isCenter) {
            return;
        }

        Level level = Minecraft.getInstance().level;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - state.facing.get2DDataValue() * 90));

        // 根据槽位内容控制模型部件可见性
        {
            this.model.getPart("basketHide").visible = !state.slotItems[0].isEmpty();
            this.model.getPart("breadHide").visible = !state.slotItems[1].isEmpty();
            this.model.getPart("cakeHide").visible = !state.slotItems[2].isEmpty();
        }

        // 渲染食物物品
        {
            renderFoodItem(state.slotItems[3], -0.6f, -1.5f, 1.4125f, poseStack, collector, state, level);
            renderFoodItem(state.slotItems[4], 0.15f, -1.2f, 1.4125f, poseStack, collector, state, level);
            renderFoodItem(state.slotItems[5], 0.55f, -1.6f, 1.4125f, poseStack, collector, state, level);

            renderFoodItem(state.slotItems[6], -0.5f, 1.65f, 1.4125f, poseStack, collector, state, level);
            renderFoodItem(state.slotItems[7], 0.375f, 1.575f, 1.4125f, poseStack, collector, state, level);
            renderFoodItem(state.slotItems[8], -0.05f, 1.2f, 1.25f, poseStack, collector, state, level);
        }

        // 渲染底座模型
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            model.renderToBuffer(poseStack, buffer, state.lightCoords, 0);
        });
        poseStack.popPose();
    }

    private void renderFoodItem(ItemStack storageItem, float x, float y, float z,
                                PoseStack poseStack, SubmitNodeCollector collector,
                                PicnicMatRenderState state, @Nullable Level level) {
        if (!storageItem.isEmpty()) {
            int count = storageItem.getCount();
            poseStack.pushPose();
            poseStack.mulPose(Axis.XN.rotationDegrees(90));
            poseStack.translate(x, y, z);
            poseStack.scale(0.4f, 0.4f, 0.4f);

            ItemStackRenderState itemRenderState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(itemRenderState, storageItem, ItemDisplayContext.FIXED, level, null, 0);
            itemRenderState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            if (count >= 10) {
                int stackCount = count / 10;
                for (int i = 0; i < stackCount; i++) {
                    poseStack.translate(Math.sin(i) * 0.05, Math.cos(i) * 0.03, -0.07);
                    poseStack.mulPose(Axis.ZN.rotationDegrees((float) Math.cos(i) * 60));

                    ItemStackRenderState stackedRenderState = new ItemStackRenderState();
                    itemModelResolver.updateForTopItem(stackedRenderState, storageItem, ItemDisplayContext.FIXED, level, null, 0);
                    stackedRenderState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                }
            }
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
