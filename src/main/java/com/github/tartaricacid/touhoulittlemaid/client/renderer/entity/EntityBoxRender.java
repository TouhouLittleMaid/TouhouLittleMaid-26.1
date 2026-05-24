package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockEntityModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityBoxRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBox;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry.CAKE_BOX;

public class EntityBoxRender extends EntityRenderer<EntityBox, EntityBoxRenderState> {
    private final List<Identifier> texturesGroup = Lists.newArrayList();
    private final SimpleBedrockEntityModel<EntityBoxRenderState> boxModel;

    public EntityBoxRender(EntityRendererProvider.Context manager) {
        super(manager);
        this.boxModel = InternalBedrockModelRegistry.getEntityModel(CAKE_BOX);
        IntStream.range(0, EntityBox.MAX_TEXTURE_SIZE).forEach(this::addBoxTexture);
    }

    @Override
    public EntityBoxRenderState createRenderState() {
        return new EntityBoxRenderState();
    }

    @Override
    public void extractRenderState(EntityBox entity, EntityBoxRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.textureIndex = entity.getTextureIndex();
        state.openStage = entity.getOpenStage();
        state.thirdStageTimeStamp = entity.thirdStageTimeStamp;
    }

    @Override
    public void submit(EntityBoxRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0, -1.501, 0.0);
        boxModel.setupAnim(state);
        Identifier texture = texturesGroup.get(state.textureIndex);
        RenderType renderType = RenderTypes.entityCutout(texture);
        submitNodeCollector.submitModel(boxModel, state, poseStack, renderType, state.lightCoords,
                OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
    }

    private void addBoxTexture(int index) {
        String fileName = "textures/bedrock/entity/cake_box/cake_box_%s.png".formatted(index);
        texturesGroup.add(Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, fileName));
    }
}
