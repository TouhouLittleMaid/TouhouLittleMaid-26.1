package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityFairyRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.monster.EntityFairy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class EntityFairyRenderer extends MobRenderer<EntityFairy, EntityFairyRenderState, EntityModel<EntityFairyRenderState>> {
    private static final Identifier[] TEXTURE = Util.make(new Identifier[18], array -> {
        for (int i = 0; i < array.length; i++) {
            String name = "textures/bedrock/entity/maid_fairy/maid_fairy_%s.png".formatted(i);
            array[i] = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, name);
        }
    });

    private final NewEntityFairyRenderer newEntityFairyRenderer;
    private final EntityBabyFairyRenderer babyFairyRenderer;

    public EntityFairyRenderer(EntityRendererProvider.Context context) {
        super(context, BedrockModelLoader.getEntityModel(BedrockModelLoader.MAID_FAIRY), 0.5f);
        this.newEntityFairyRenderer = new NewEntityFairyRenderer(context);
        this.babyFairyRenderer = new EntityBabyFairyRenderer(context);
    }

    @Override
    public EntityFairyRenderState createRenderState() {
        return new EntityFairyRenderState();
    }

    @Override
    public void extractRenderState(EntityFairy entity, EntityFairyRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.fairyTypeOrdinal = entity.getFairyTypeOrdinal();
        state.isOnGround = entity.onGround();
    }

    @Override
    public void submit(EntityFairyRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (MiscConfig.USE_NEW_MAID_FAIRY_MODEL.get()) {
            if (state.isBaby) {
                babyFairyRenderer.submit(state, poseStack, submitNodeCollector, camera);
            } else {
                newEntityFairyRenderer.submit(state, poseStack, submitNodeCollector, camera);
            }
        } else {
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }

    @Override
    public Identifier getTextureLocation(EntityFairyRenderState state) {
        int index = Mth.clamp(state.fairyTypeOrdinal, 0, TEXTURE.length - 1);
        return TEXTURE[index];
    }

    @Override
    protected void setupRotations(EntityFairyRenderState state, PoseStack poseStack, float bodyRot, float scale) {
        super.setupRotations(state, poseStack, bodyRot, scale);
        if (!state.isOnGround) {
            poseStack.mulPose(Axis.XN.rotation(8 * (float) Math.PI / 180.0f));
        }
    }
}
