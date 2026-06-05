package com.github.tartaricacid.touhoulittlemaid.client.renderer.item;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.item.state.ChairRenderRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.item.ItemChair;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * 椅子物品的 SpecialModelRenderer（替代旧的 BlockEntityWithoutLevelRenderer）
 * <p>
 * 参考 {@link com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityChairRenderer} 的渲染模式实现。
 * extractArgument 仿照旧版 BlockEntityWithoutLevelRenderer 创建椅子实体预览，
 * submit 通过新版 EntityRenderDispatcher 提交实体渲染状态。
 */
public class ChairItemRenderer implements SpecialModelRenderer<ChairRenderRenderState> {
    public static final Identifier CHAIR_ITEM_RENDERER = IdentifierUtil.modLoc("chair_item");
    /**
     * 默认兜底模型 ID，与 {@code EntityChairRenderer.DEFAULT_CHAIR_ID} 保持一致
     */
    private static final String DEFAULT_CHAIR_ID = "touhou_little_maid:cushion";

    public ChairItemRenderer() {
    }

    /**
     * 仿照 {@code EntityChairRenderer.extractRenderState} 提取模型、纹理数据到渲染状态
     */
    @Override
    public ChairRenderRenderState extractArgument(ItemStack stack) {
        ChairRenderRenderState state = new ChairRenderRenderState();
        if (!(stack.getItem() instanceof ItemChair)) {
            return state;
        }

        ItemChair.Data data = ItemChair.getData(stack);
        String modelId = data.modelId();
        state.modelId = modelId;

        CustomPackLoader.CHAIR_MODELS.getInfo(modelId).ifPresent(
                info -> state.renderItemScale = info.getRenderItemScale()
        );

        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return state;
        }

        EntityChair chair = EntityCacheUtil.getChair(level, EntitySpawnReason.LOAD);
        chair.setModelId(modelId);

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        state.entityRenderState = dispatcher.extractEntity(chair, 0);

        return state;
    }

    /**
     * 仿照 {@code EntityChairRenderer.submitChair} 渲染逻辑
     */
    @Override
    public void submit(
            ChairRenderRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor
    ) {
        if (state == null) {
            return;
        }

        if (state.entityRenderState == null) {
            return;
        }

        // 缩放：优先 renderItemScale，兜底 1.0
        float scale = state.renderItemScale > 0 ? state.renderItemScale : 1.0f;

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        state.entityRenderState.lightCoords = lightCoords;
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        CameraRenderState camera = new CameraRenderState();
        dispatcher.submit(state.entityRenderState, camera, 1 / scale - 0.125, 0.25, 0.75, poseStack, collector);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        CustomPackLoader.CHAIR_MODELS.getModel(DEFAULT_CHAIR_ID).ifPresent(model ->
                model.root().getExtentsForGui(poseStack, output)
        );
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<ChairRenderRenderState> {
        public static final MapCodec<ChairItemRenderer.Unbaked> MAP_CODEC = MapCodec.unit(ChairItemRenderer.Unbaked::new);

        @Override
        public MapCodec<ChairItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ChairItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new ChairItemRenderer();
        }
    }
}
