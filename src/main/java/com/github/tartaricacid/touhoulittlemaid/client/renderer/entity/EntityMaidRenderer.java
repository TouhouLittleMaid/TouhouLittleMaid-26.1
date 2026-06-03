package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.api.event.client.AddMaidLayerEvent;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.ChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.EntityGraphics;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoEntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer.*;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.ModelType;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class EntityMaidRenderer extends MobRenderer<EntityMaid, EntityMaidRenderState, EntityMaidModel> {
    private static final Identifier DEFAULT_TEXTURE = IdentifierUtil.modLoc("textures/entity/empty.png");

    private final BlockModelResolver blockModelResolver;
    private final GeckoEntityMaidRenderer geckoRenderer;
    private final ChatBubbleRenderer chatBubbleRenderer;

    public EntityMaidRenderer(EntityRendererProvider.Context context) {
        super(context, new EntityMaidModel(), 0.5f);

        this.blockModelResolver = context.getBlockModelResolver();
        this.geckoRenderer = new GeckoEntityMaidRenderer(context);
        this.chatBubbleRenderer = new ChatBubbleRenderer(this);

        this.addLayer(new LayerMaidHeldItem(this));
        this.addLayer(new LayerMaidBipedHead(this, context));
        this.addLayer(new LayerMaidBackpack(this));
        this.addLayer(new LayerMaidBackItem(this));
        this.addLayer(new LayerMaidBanner(this, context));

        NeoForge.EVENT_BUS.post(new AddMaidLayerEvent.Legacy(context, this));
    }

    @Override
    public EntityMaidRenderState createRenderState() {
        return new EntityMaidRenderState();
    }

    @Override
    public void extractRenderState(EntityMaid maid, EntityMaidRenderState state, float partialTicks) {
        state.clear();
        super.extractRenderState(maid, state, partialTicks);
        HumanoidMobRenderer.extractHumanoidRenderState(maid, state, partialTicks, itemModelResolver);
        EntityMaidRenderState.extractRenderState(maid, state, partialTicks, blockModelResolver, itemModelResolver, getGeckoEntity(maid));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public GeckoMaidEntity<? extends EntityMaid> getGeckoEntity(Mob entity) {
        return entity.getData(GeckoMaidEntity.TYPE);
    }

    @Override
    public void submit(EntityMaidRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        // 一般不太可能触发
        if (state.modelType == ModelType.NONE) {
            return;
        }

        state.camera = camera;

        // 聊天气泡渲染
        if (state.showBubble && state.bubbleOffset != null) {
            poseStack.pushPose();

            double offsetY = state.bubbleOffset.y() + 0.5f;
            if (state.sitting) {
                offsetY -= 0.25f;
            }
            poseStack.translate(state.bubbleOffset.x, offsetY, state.bubbleOffset.z);
            poseStack.mulPose(camera.orientation);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.scale(-0.025F, -0.025F, 0.025F);

            EntityGraphics graphics = new EntityGraphics(submitNodeCollector, poseStack, state, state.lightCoords, state.partialTick);
            this.chatBubbleRenderer.submit(graphics);

            poseStack.popPose();
        }

        // GeckoLib 接管渲染
        if (state.modelType == ModelType.GECKO) {
            this.geckoRenderer.submit(state, poseStack, submitNodeCollector, camera);
            return;
        }

        // 普通模型渲染
        if (state.modelType == ModelType.SIMPLE_BEDROCK) {
            if (state.bedrockModel == null) {
                return;
            }
            this.model = state.bedrockModel;
            this.model.setAnimations(state.animations);
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }

    @Override
    protected void setupRotations(EntityMaidRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
        super.setupRotations(state, poseStack, bodyRot, entityScale);

        // 抱起女仆时的旋转
        if (state.playerVehicle && state.modelType != ModelType.GECKO) {
            poseStack.translate(-0.375, 0.8325, 0.375);
            poseStack.mulPose(Axis.ZN.rotationDegrees(65));
            poseStack.mulPose(Axis.YN.rotationDegrees(-80));
        }
    }

    @Override
    protected void scale(EntityMaidRenderState state, PoseStack poseStack) {
        var scale = state.modelInfo.getRenderEntityScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public Identifier getTextureLocation(EntityMaidRenderState state) {
        if (state.modelInfo == null) {
            return DEFAULT_TEXTURE;
        }
        return state.modelInfo.getTexture();
    }

    @Override
    public float getWhiteOverlayProgress(EntityMaidRenderState state) {
        return super.getWhiteOverlayProgress(state);
    }
}
