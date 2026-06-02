package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityChairRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.ChairRenderRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.item.ItemChair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * 椅子物品的 SpecialModelRenderer（替代旧的 BlockEntityWithoutLevelRenderer）
 * <p>
 * 参考 {@link com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityChairRenderer} 的渲染模式实现。
 * extractArgument 仿照 EntityChairRenderer.extractRenderState 提取模型、纹理数据，
 * submit 仿照 EntityChairRenderer.submitChair 渲染逻辑。
 */
public class TileEntityItemStackChairRenderer implements SpecialModelRenderer<ChairRenderRenderState> {
    public static final Identifier CHAIR_ITEM_RENDERER = IdentifierUtil.modLoc("chair_item");
    /**
     * 默认兜底模型 ID，与 {@code EntityChairRenderer.DEFAULT_CHAIR_ID} 保持一致
     */
    private static final String DEFAULT_CHAIR_ID = "touhou_little_maid:cushion";

    public TileEntityItemStackChairRenderer() {
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

        // 读取模型数据（仿照 EntityChairRenderer.extractRenderState）
        CustomPackLoader.CHAIR_MODELS.getModel(modelId).ifPresent(model -> state.bedrockModel = model);
        CustomPackLoader.CHAIR_MODELS.getInfo(modelId).ifPresent(info -> {
            state.chairInfo = info;
            state.texture = info.getTexture();
            state.renderItemScale = info.getRenderItemScale();
        });

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

        // 确保有可用模型：优先使用指定模型，找不到则兜底
        EntityChairModel model = state.bedrockModel;
        if (model == null) {
            model = CustomPackLoader.CHAIR_MODELS.getModel(DEFAULT_CHAIR_ID).orElse(null);
        }
        if (model == null) {
            return;
        }

        // 纹理：优先 chairInfo，兜底 empty
        Identifier texture = state.texture != null
                ? state.texture
                : EntityChairRenderer.DEFAULT_TEXTURE;

        // 缩放：优先 renderItemScale，兜底 1.0
        float scale = state.renderItemScale > 0 ? state.renderItemScale : 1.0f;

        // 仿照 EntityChairRenderer 的缩放逻辑
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.scale(scale, scale, scale);
        EntityChairModel finalModel = model;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(texture), (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);

            finalModel.renderToBuffer(poseStack, buffer, lightCoords, overlayCoords);
            poseStack.popPose();
        });
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
        public static final MapCodec<TileEntityItemStackChairRenderer.Unbaked> MAP_CODEC = MapCodec.unit(TileEntityItemStackChairRenderer.Unbaked::new);

        @Override
        public MapCodec<TileEntityItemStackChairRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public TileEntityItemStackChairRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new TileEntityItemStackChairRenderer();
        }
    }
}
