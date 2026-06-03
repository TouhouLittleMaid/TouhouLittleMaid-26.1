package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.MOD_ID;
import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry.MAID_BANNER;
import static net.minecraft.client.renderer.blockentity.BannerRenderer.submitPatterns;
import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;


public class LayerMaidBanner extends RenderLayer<EntityMaidRenderState, EntityMaidModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/bedrock/entity/maid_banner.png");
    private final SimpleBedrockModel<Unit> root;
    private final SpriteGetter sprites;
    private final BannerFlagModel flag;

    public LayerMaidBanner(EntityMaidRenderer renderer, EntityRendererProvider.Context context) {
        super(renderer);
        this.root = InternalBedrockModelRegistry.getModel(MAID_BANNER);
        this.flag = new BannerFlagModel(context.bakeLayer(ModelLayers.STANDING_BANNER_FLAG));
        this.sprites = context.getSprites();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNode, int light, EntityMaidRenderState state, float yRot, float xRot) {
        if (state.backBanner != null && state.modelInfo.isShowBackpack()) {
            poseStack.pushPose();

            // 依据 root 模型的位移对整体进行物品进行偏移、旋转和缩放
            if (this.getParentModel().root() instanceof BedrockPart part) {
                part.translateAndRotate(poseStack);
            }

            poseStack.translate(0, -0.25, 0.25);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.XN.rotationDegrees(5));

            // 杆子
            RenderType renderType = RenderTypes.entityCutout(TEXTURE);
            submitNode.submitModel(
                    root, Unit.INSTANCE, poseStack, renderType, light,
                    NO_OVERLAY, state.outlineColor, null
            );

            BannerPatternLayers patterns = state.backBanner.patterns;
            DyeColor baseColor = state.backBanner.baseColor;
            SpriteId sprite = Sheets.BANNER_BASE;

            // 旗帜图案
            poseStack.mulPose(Axis.YN.rotationDegrees(90));
            poseStack.translate(0.75, 0.2, 0.1);
            submitNode.submitModel(
                    flag, 0f, poseStack, light, NO_OVERLAY, -1,
                    sprite, sprites, state.outlineColor, null
            );
            submitPatterns(
                    sprites, poseStack, submitNode, light, NO_OVERLAY, flag,
                    0f, true, baseColor, patterns, null
            );

            poseStack.popPose();
        }
    }
}
