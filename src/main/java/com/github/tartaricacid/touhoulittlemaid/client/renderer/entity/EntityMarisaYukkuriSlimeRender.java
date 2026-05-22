package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.VanillaConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MagmaCubeRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MagmaCube;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.BedrockModelLoader.MARISA_YUKKURI;

public class EntityMarisaYukkuriSlimeRender extends MobRenderer<MagmaCube, SlimeRenderState, EntityModel<SlimeRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/entity/marisa_yukkuri.png");
    private final MagmaCubeRenderer vanillaRender;

    public EntityMarisaYukkuriSlimeRender(EntityRendererProvider.Context context) {
        super(context, BedrockModelLoader.getEntityModel(MARISA_YUKKURI), 0.25F);
        this.vanillaRender = new MagmaCubeRenderer(context);
    }

    @Override
    protected int getBlockLightLevel(MagmaCube entity, BlockPos pos) {
        return 15;
    }

    @Override
    public Identifier getTextureLocation(SlimeRenderState state) {
        return TEXTURE;
    }

    @Override
    public SlimeRenderState createRenderState() {
        return new SlimeRenderState();
    }

    @Override
    public void extractRenderState(MagmaCube slime, SlimeRenderState state, float partialTicks) {
        super.extractRenderState(slime, state, partialTicks);
        state.squish = Mth.lerp(partialTicks, slime.oSquish, slime.squish);
        state.size = slime.getSize();
    }

    @Override
    protected float getShadowRadius(SlimeRenderState state) {
        return state.size * 0.25F;
    }

    @Override
    public void submit(SlimeRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (VanillaConfig.REPLACE_SLIME_MODEL.get()) {
            super.submit(state, poseStack, collector, cameraState);
        } else {
            vanillaRender.submit(state, poseStack, collector, cameraState);
        }
    }

    @Override
    protected void scale(SlimeRenderState state, PoseStack poseStack) {
        int size = state.size;
        float ss = state.squish / (size * 0.5F + 1.0F);
        float w = 1.0F / (ss + 1.0F);
        poseStack.scale(w * size, 1.0F / w * size, w * size);
    }
}
