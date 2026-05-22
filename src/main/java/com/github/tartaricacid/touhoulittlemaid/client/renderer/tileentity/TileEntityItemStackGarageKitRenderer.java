package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.GarageKitRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.item.ItemGarageKit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader.STATUE_BASE;

/**
 * GarageKit 物品的特殊模型渲染器，替代旧版 BlockEntityWithoutLevelRenderer
 * <p>
 * 参考 PlayerDollItemRenderer 的 SpecialModelRenderer 模式实现。
 * 底座模型（STATUE_BASE）可直接通过 submitCustomGeometry 渲染。
 * 实体预览渲染暂未迁移，需要适配新的 SubmitNodeCollector API。
 */
public class TileEntityItemStackGarageKitRenderer implements SpecialModelRenderer<GarageKitRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/block/statue_base.png");
    private final @Nullable SimpleBedrockModel<EntityRenderState> baseModel;

    public TileEntityItemStackGarageKitRenderer() {
        this.baseModel = BedrockModelLoader.getModel(STATUE_BASE);
    }

    @Override
    public GarageKitRenderState extractArgument(ItemStack stack) {
        GarageKitRenderState state = new GarageKitRenderState();
        CustomData data = ItemGarageKit.getMaidData(stack);
        state.extraData = data.copyTag();
        return state;
    }

    @Override
    public void submit(
            GarageKitRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor
    ) {
        // 渲染底座模型
        if (baseModel != null) {
            poseStack.pushPose();
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.translate(1, 1.5, 1);
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
                baseModel.renderToBuffer(poseStack, buffer, lightCoords, overlayCoords);
            });
            poseStack.popPose();
        }

        // TODO: 实体预览渲染需要迁移
        // 旧代码使用 EntityRenderDispatcher.render() 渲染实体（Maid 或其他实体类型），
        // 但 SpecialModelRenderer.submit() 不提供 MultiBufferSource 参数，
        // 无法直接调用 EntityRenderDispatcher。
        // 需要将实体渲染改为通过 ItemStackRenderState 管道，
        // 参考 TileEntityGarageKitRenderer（Block Entity 版本）的 submit() 实现。
        if (state != null) {
            TouhouLittleMaid.LOGGER.debug("GarageKit item entity preview render not yet migrated");
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        // TODO: 需要从底座模型获取实际尺寸
        PoseStack poseStack = new PoseStack();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        // 暂无模型可供 getExtentsForGui 调用，待后续精修
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<GarageKitRenderState> {
        public static final Identifier ID = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "garage_kit");
        public static final MapCodec<TileEntityItemStackGarageKitRenderer.Unbaked> MAP_CODEC = MapCodec.unit(TileEntityItemStackGarageKitRenderer.Unbaked::new);

        @Override
        public MapCodec<TileEntityItemStackGarageKitRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public TileEntityItemStackGarageKitRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new TileEntityItemStackGarageKitRenderer();
        }
    }
}
