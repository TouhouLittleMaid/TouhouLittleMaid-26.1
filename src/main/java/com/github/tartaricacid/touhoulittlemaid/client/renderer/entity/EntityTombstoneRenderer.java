package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityTombstoneRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityTombstone;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Objects;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.MOD_ID;
import static com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader.TOMBSTONE;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class EntityTombstoneRenderer extends EntityRenderer<EntityTombstone, EntityTombstoneRenderState> {
    private static final Identifier DEFAULT_TEXTURE = fromNamespaceAndPath(MOD_ID, "textures/bedrock/entity/tombstone/tombstone_overworld.png");
    private static final Identifier THE_NETHER_TEXTURE = fromNamespaceAndPath(MOD_ID, "textures/bedrock/entity/tombstone/tombstone_the_nether.png");
    private static final Identifier THE_END_TEXTURE = fromNamespaceAndPath(MOD_ID, "textures/bedrock/entity/tombstone/tombstone_the_end.png");
    private static final Identifier TWILIGHT_FOREST_TEXTURE = fromNamespaceAndPath(MOD_ID, "textures/bedrock/entity/tombstone/tombstone_twilight_forest.png");
    private static final Identifier AETHER_TEXTURE = fromNamespaceAndPath(MOD_ID, "textures/bedrock/entity/tombstone/tombstone_aether.png");

    private static final Identifier TWILIGHT_FOREST_LEVEL_ID = fromNamespaceAndPath("twilightforest", "twilight_forest");
    private final static Identifier AETHER_LEVEL_ID = fromNamespaceAndPath("aether", "the_aether");

    private static final int NAME_SHOW_DISTANCE = 64;
    private final SimpleBedrockModel<EntityTombstoneRenderState> tombstoneModel;

    public EntityTombstoneRenderer(EntityRendererProvider.Context manager) {
        super(manager);
        tombstoneModel = Objects.requireNonNull(BedrockModelLoader.getModel(TOMBSTONE));
    }

    @Override
    public EntityTombstoneRenderState createRenderState() {
        return new EntityTombstoneRenderState();
    }

    @Override
    public void extractRenderState(EntityTombstone entity, EntityTombstoneRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.dimension = entity.level().dimension();
        state.maidName = entity.getMaidName();
    }

    @Override
    protected boolean shouldShowName(EntityTombstone entity, double distanceToCameraSq) {
        return !entity.getMaidName().equals(Component.empty());
    }

    @Override
    public void submit(EntityTombstoneRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0, -1.501, 0.0);
        Identifier texture = this.getTombstoneTexture(state.dimension);
        RenderType renderType = RenderTypes.entityCutout(texture);

        submitNodeCollector.submitModel(this.tombstoneModel, state, poseStack, renderType, state.lightCoords,
                OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        poseStack.popPose();
        if (!state.maidName.equals(Component.empty())) {
            renderCustomNameTag(state, Component.translatable("entity.touhou_little_maid.tombstone.display").withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE),
                    1.6f, poseStack, submitNodeCollector, camera);
            renderCustomNameTag(state, state.maidName, 1.85f, poseStack, submitNodeCollector, camera);
        }
    }

    private void renderCustomNameTag(EntityTombstoneRenderState state, Component component, float yOffset, PoseStack poseStack,
                                     SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.distanceToCameraSq < (NAME_SHOW_DISTANCE * NAME_SHOW_DISTANCE)) {
            poseStack.pushPose();
            poseStack.translate(0.0F, yOffset, 0.0F);
            poseStack.mulPose(camera.orientation);
            poseStack.scale(-0.025F, -0.025F, 0.025F);
            Font font = this.getFont();
            float width = (float) (-font.width(component) / 2);
            submitNodeCollector.submitText(poseStack, width, 0, component.getVisualOrderText(),
                    false, Font.DisplayMode.NORMAL, state.lightCoords,
                    -1, 0, 0);
            poseStack.popPose();
        }
    }

    private Identifier getTombstoneTexture(ResourceKey<Level> dimension) {
        if (dimension.equals(Level.NETHER)) {
            return THE_NETHER_TEXTURE;
        }
        if (dimension.equals(Level.END)) {
            return THE_END_TEXTURE;
        }
        if (dimension.identifier().equals(TWILIGHT_FOREST_LEVEL_ID)) {
            return TWILIGHT_FOREST_TEXTURE;
        }
        if (dimension.identifier().equals(AETHER_LEVEL_ID)) {
            return AETHER_TEXTURE;
        }
        return DEFAULT_TEXTURE;
    }
}
