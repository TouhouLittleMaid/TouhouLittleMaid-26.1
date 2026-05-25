package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityFairyRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.monster.EntityFairy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import java.util.Locale;

public class EntityBabyFairyRenderer extends MobRenderer<EntityFairy, EntityFairyRenderState, EntityModel<EntityFairyRenderState>> {
    private static final Identifier[] TEXTURE = Util.make(new Identifier[18], array -> {
        for (int i = 0; i < array.length; i++) {
            String name = "textures/bedrock/entity/baby_maid_fairy/maid_fairy_%s.png".formatted(i);
            array[i] = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, name);
        }
    });

    private static final Identifier TEXTURE_RICK = Identifier.fromNamespaceAndPath(
            TouhouLittleMaid.MOD_ID, "textures/bedrock/entity/baby_maid_fairy/maid_fairy_rick.png"
    );

    public EntityBabyFairyRenderer(EntityRendererProvider.Context context) {
        super(context, InternalBedrockModelRegistry.getEntityModel(InternalBedrockModelRegistry.BABY_MAID_FAIRY), 0.5f);
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
        state.isRick = EntityFairy.RICK.equals(entity.getName().getString().toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Identifier getTextureLocation(EntityFairyRenderState state) {
        if (state.isRick) {
            return TEXTURE_RICK;
        }
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
