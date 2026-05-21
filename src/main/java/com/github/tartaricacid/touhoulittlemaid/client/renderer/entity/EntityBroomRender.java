package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityBroomRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBroom;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;

import java.util.Objects;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader.BROOM;

public class EntityBroomRender extends LivingEntityRenderer<EntityBroom, EntityBroomRenderState, EntityModel<EntityBroomRenderState>> {
    private static final Identifier BROOM_TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/entity/broom.png");

    public EntityBroomRender(EntityRendererProvider.Context context) {
        super(context, Objects.requireNonNull(BedrockModelLoader.getModel(BROOM)), 0.5f);
    }

    @Override
    public EntityBroomRenderState createRenderState() {
        return new EntityBroomRenderState();
    }

    @Override
    public void extractRenderState(EntityBroom entity, EntityBroomRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isVehicle = entity.isVehicle();
    }

    @Override
    protected boolean shouldShowName(EntityBroom entity, double distanceToCameraSq) {
        if (!super.shouldShowName(entity, distanceToCameraSq)) {
            return false;
        }
        if (entity.shouldShowName()) {
            return true;
        }
        return entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity;
    }

    @Override
    public Identifier getTextureLocation(EntityBroomRenderState state) {
        return BROOM_TEXTURE;
    }
}