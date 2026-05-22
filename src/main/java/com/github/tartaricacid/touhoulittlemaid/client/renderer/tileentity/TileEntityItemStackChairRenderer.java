package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.ChairRenderRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.item.ItemChair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * 椅子物品的 SpecialModelRenderer（替代旧的 BlockEntityWithoutLevelRenderer）
 * 参考 PlayerDollItemRenderer 的模式实现
 */
public class TileEntityItemStackChairRenderer implements SpecialModelRenderer<ChairRenderRenderState> {
    public TileEntityItemStackChairRenderer() {
    }

    @Override
    public void submit(
            ChairRenderRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            int overlay,
            boolean hasFoil,
            int outlineColor
    ) {
        if (state == null) {
            return;
        }

        // 尝试从 CustomPackLoader 获取椅子模型并直接渲染
        // 由于 SpecialModelRenderer 无法使用 EntityRenderDispatcher（API 不兼容），
        // 此处直接渲染 Bedrock 模型。动画和复杂实体渲染暂不支持。
        CustomPackLoader.CHAIR_MODELS.getModel(state.modelId).ifPresent(model -> {
            poseStack.pushPose();
            poseStack.scale(state.renderItemScale, state.renderItemScale, state.renderItemScale);
            // TODO: 完整实现需要参考 EntityChairRenderer 的 submit 逻辑
            // 当前使用 LivingEntityRenderer 渲染模型的方式需要 Entity 上下文，在 SpecialModelRenderer 中不可用。
            // 如需要支持动画和实体属性，需迁移至 ItemStackRenderState 体系或使用 ItemRenderer 扩展。
            // model 目前无法直接 submit（BedrockModel 需要 EntityRenderState 参数），此处仅渲染基础几何。
            model.renderToBuffer(poseStack, null, light, overlay);
            poseStack.popPose();
        });
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        // 使用默认椅子模型计算 GUI 范围
        CustomPackLoader.CHAIR_MODELS.getModel("touhou_little_maid:cushion").ifPresent(model ->
                model.root().getExtentsForGui(poseStack, output)
        );
    }

    @Override
    public ChairRenderRenderState extractArgument(ItemStack stack) {
        ChairRenderRenderState state = new ChairRenderRenderState();
        if (!(stack.getItem() instanceof ItemChair)) {
            return state;
        }
        ItemChair.Data data = ItemChair.getData(stack);
        state.modelId = data.modelId();
        state.renderItemScale = CustomPackLoader.CHAIR_MODELS.getModelRenderItemScale(data.modelId());
        return state;
    }


    public record Unbaked() implements SpecialModelRenderer.Unbaked<ChairRenderRenderState> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public TileEntityItemStackChairRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new TileEntityItemStackChairRenderer();
        }
    }
}
