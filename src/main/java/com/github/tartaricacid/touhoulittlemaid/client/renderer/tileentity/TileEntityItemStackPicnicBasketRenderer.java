package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.PicnicBasketRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * PicnicBasket 物品的特殊模型渲染器，替代旧版 BlockEntityWithoutLevelRenderer
 * <p>
 * 参考 TileEntityItemStackGarageKitRenderer 的 SpecialModelRenderer 模式实现。
 * 使用 submitCustomGeometry 渲染 Bedrock 基岩版模型。
 */
public class TileEntityItemStackPicnicBasketRenderer implements SpecialModelRenderer<PicnicBasketRenderState> {
    public static final Identifier PICNIC_BASKET_ITEM_RENDERER = IdentifierUtil.modLoc("picnic_basket_item");
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/picnic_basket.png");
    private final SimpleBedrockModel<Unit> model;

    public TileEntityItemStackPicnicBasketRenderer() {
        this.model = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.PICNIC_BASKET);
    }

    @Override
    public PicnicBasketRenderState extractArgument(ItemStack stack) {
        return new PicnicBasketRenderState();
    }

    @Override
    public void submit(
            PicnicBasketRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor
    ) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            model.renderToBuffer(poseStack, buffer, lightCoords, overlayCoords);
            poseStack.popPose();
        });
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(10,10,10));
        output.accept(new Vector3f(-10,-10,-10));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<PicnicBasketRenderState> {
        public static final MapCodec<TileEntityItemStackPicnicBasketRenderer.Unbaked> MAP_CODEC = MapCodec.unit(TileEntityItemStackPicnicBasketRenderer.Unbaked::new);

        @Override
        public MapCodec<TileEntityItemStackPicnicBasketRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public TileEntityItemStackPicnicBasketRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new TileEntityItemStackPicnicBasketRenderer();
        }
    }
}
