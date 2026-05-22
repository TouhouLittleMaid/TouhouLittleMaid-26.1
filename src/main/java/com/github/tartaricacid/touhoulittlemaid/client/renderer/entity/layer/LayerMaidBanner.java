//package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer;
//
//import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
//import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
//import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
//import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
//import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
//import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
//import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
//import com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.math.Axis;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.Sheets;
//import net.minecraft.client.renderer.SubmitNodeCollector;
//import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
//import net.minecraft.client.renderer.entity.layers.RenderLayer;
//import net.minecraft.client.renderer.entity.state.EntityRenderState;
//import net.minecraft.client.renderer.rendertype.RenderTypes;
//import net.minecraft.client.renderer.texture.OverlayTexture;
//import net.minecraft.client.resources.model.Material;
//import net.minecraft.client.resources.model.ModelBakery;
//import net.minecraft.core.component.DataComponents;
//import net.minecraft.resources.Identifier;
//import net.minecraft.util.ARGB;
//import net.minecraft.world.item.DyeColor;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.AbstractBannerBlock;
//import net.minecraft.world.level.block.entity.BannerPatternLayers;
//
//import java.util.Objects;
//
//import static com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader.MAID_BANNER;
//
//public class LayerMaidBanner extends RenderLayer<EntityMaidRenderState, BedrockModel<EntityMaidRenderState>> {
//    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/entity/maid_banner.png");
//    private final EntityMaidRenderer renderer;
//    private final SimpleBedrockModel<BannerRenderState> bannerModel;
//
//    public LayerMaidBanner(EntityMaidRenderer renderer) {
//        super(renderer);
//        this.renderer = renderer;
//        this.bannerModel = Objects.requireNonNull(BedrockModelLoader.getModel(MAID_BANNER));
//    }
//
//    @Override
//    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, EntityMaidRenderState state, float yRot, float xRot) {
//        IMaid maid = IMaid.convert(mob);
//        if (maid == null) {
//            return;
//        }
//        ItemStack stack = maid.getBackpackShowItem();
//        if (state.backBanner != null && state.mainInfo.isShowBackpack()) {
//            poseStack.pushPose();
//            poseStack.translate(0, 0.5, 0.025);
//            poseStack.scale(0.5F, 0.5F, 0.5F);
//            poseStack.mulPose(Axis.XN.rotationDegrees(5));
//            // TODO
//            this.bannerModel.renderToBuffer(matrixStack, buffer, packedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
//            BannerPatternLayers patterns = maid.getBackpackShowItem().get(DataComponents.BANNER_PATTERNS);
//            DyeColor dyeColor = ((AbstractBannerBlock) bannerItem.getBlock()).getColor();
//            if (patterns != null) {
//                renderPatterns(matrixStack, bufferIn, packedLightIn, bannerModel.getPart("banner"), patterns, dyeColor);
//            }
//            poseStack.popPose();
//        }
//    }
//
//    private void renderPatterns(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
//                                BedrockPart banner, BannerPatternLayers patterns, DyeColor dyeColor) {
//        banner.render(poseStack, ModelBakery.BANNER_BASE.buffer(bufferSource, RenderTypes::entitySolid, false),
//                packedLight, OverlayTexture.NO_OVERLAY);
//        renderPatternLayer(poseStack, bufferSource, packedLight, banner, Sheets.BANNER_BASE, dyeColor);
//        for (int i = 0; i < 16 && i < patterns.layers().size(); ++i) {
//            BannerPatternLayers.Layer layer = patterns.layers().get(i);
//            Material material = Sheets.getBannerMaterial(layer.pattern());
//            renderPatternLayer(poseStack, bufferSource, packedLight, banner, material, layer.color());
//        }
//    }
//
//    private void renderPatternLayer(PoseStack poseStack, MultiBufferSource buffer, int packedLight, BedrockPart banner, Material material, DyeColor color) {
//        int packedColor = color.getTextureDiffuseColor();
//        float red = ARGB.red(packedColor) / 255f;
//        float green = ARGB.green(packedColor) / 255f;
//        float blue = ARGB.blue(packedColor) / 255f;
//        banner.render(poseStack, material.buffer(buffer, RenderTypes::entityNoOutline), packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
//    }
//}
