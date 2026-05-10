package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class EntitySitRenderer extends EntityRenderer<EntitySit> {
    private static final Identifier EMPTY = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/entity/empty.png");

    public EntitySitRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntitySit entitySit, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
    }

    @Override
    public Identifier getTextureLocation(EntitySit entitySit) {
        return EMPTY;
    }
}
