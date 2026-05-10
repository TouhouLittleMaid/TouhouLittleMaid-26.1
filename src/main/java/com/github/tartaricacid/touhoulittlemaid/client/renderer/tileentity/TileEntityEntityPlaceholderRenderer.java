package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.EntityPlaceholderModel;
import com.github.tartaricacid.touhoulittlemaid.item.ItemEntityPlaceholder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

public class TileEntityEntityPlaceholderRenderer extends BlockEntityWithoutLevelRenderer {
    private static final EntityPlaceholderModel BASE_MODEL = new EntityPlaceholderModel();
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/item/entity_placeholder.png");

    private final Function<Identifier, ModelResourceLocation> recipeToModel = Util.memoize(recipeId -> {
        Path path = Paths.get(recipeId.getPath());
        String namespace = recipeId.getNamespace();
        Identifier loc = Identifier.fromNamespaceAndPath(namespace, "item/%s".formatted(path.getFileName()));
        return ModelResourceLocation.standalone(loc);
    });

    private final Function<Identifier, Identifier> recipeToTexture = Util.memoize(recipeId -> {
        Path path = Paths.get(recipeId.getPath());
        String namespace = recipeId.getNamespace();
        return Identifier.fromNamespaceAndPath(namespace, String.format("textures/item/%s.png", path.getFileName().toString()));
    });


    public TileEntityEntityPlaceholderRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        Identifier recipeId = ItemEntityPlaceholder.getRecipeId(stack);
        BakedModel bakedModel = getBakedModel(recipeId);
        poseStack.pushPose();
        if (bakedModel != null) {
            // 先尝试获取物品材质进行渲染
            RenderTypes renderType = Sheets.translucentItemSheet();
            VertexConsumer buffer = bufferIn.getBuffer(renderType);
            Minecraft.getInstance().getItemRenderer().renderModelLists(bakedModel, stack, combinedLight, combinedOverlay, poseStack, buffer);
        } else {
            // 否则渲染贴图
            Identifier texture = getTexture(recipeId);
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            VertexConsumer buffer = bufferIn.getBuffer(RenderTypes.entityCutoutNoCull(texture));
            BASE_MODEL.renderToBuffer(poseStack, buffer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        poseStack.popPose();
    }

    @Nullable
    private BakedModel getBakedModel(@Nullable Identifier recipeId) {
        if (recipeId == null) {
            return null;
        }
        ModelManager manager = Minecraft.getInstance().getModelManager();
        ModelResourceLocation modelId = recipeToModel.apply(recipeId);
        BakedModel model = manager.getModel(modelId);
        if (model == manager.getMissingModel()) {
            return null;
        }
        return model;
    }

    private Identifier getTexture(@Nullable Identifier recipeId) {
        if (recipeId == null) {
            return TEXTURE;
        }
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Identifier texture = recipeToTexture.apply(recipeId);
        if (manager.getResource(texture).isPresent()) {
            return texture;
        }
        return TEXTURE;
    }
}
