package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.animation.script.GlWrapper;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoEntityChairRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.ModelType;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class EntityChairRenderer extends LivingEntityRenderer<EntityChair, EntityChairRenderState, BedrockModel<EntityChairRenderState>> {
    public static final Identifier DEFAULT_TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/entity/empty.png");
    private static final String DEFAULT_CHAIR_ID = "touhou_little_maid:cushion";
    public static boolean renderHitBox = true;
    private final GeckoEntityChairRenderer geckoEntityChairRenderer;

    public EntityChairRenderer(EntityRendererProvider.Context rendererManager) {
        super(rendererManager, new BedrockModel<>(), 0);
        this.geckoEntityChairRenderer = new GeckoEntityChairRenderer(rendererManager);
    }

    @Override
    public EntityChairRenderState createRenderState() {
        return new EntityChairRenderState();
    }

    @Override
    public void extractRenderState(EntityChair entity, EntityChairRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.modelId = entity.getModelId();
        state.chair = entity;
    }

    @Override
    public EntityChairRenderState createRenderState() {
        return new EntityChairRenderState();
    }

    @Override
    public void extractRenderState(EntityChair chair, EntityChairRenderState state, float partialTicks) {
        state.clear();
        super.extractRenderState(chair, state, partialTicks);

        // 读取默认模型，用于清除不存在模型的缓存残留
        CustomPackLoader.CHAIR_MODELS.getModel(DEFAULT_CHAIR_ID).ifPresent(model -> state.bedrockModel = model);
        CustomPackLoader.CHAIR_MODELS.getInfo(DEFAULT_CHAIR_ID).ifPresent(info -> state.chairInfo = info);

        // 通过模型 id 获取对应数据
        CustomPackLoader.CHAIR_MODELS.getModel(chair.getModelId()).ifPresent(model -> state.bedrockModel = model);
        CustomPackLoader.CHAIR_MODELS.getInfo(chair.getModelId()).ifPresent(info -> state.chairInfo = info);
        CustomPackLoader.CHAIR_MODELS.getAnimation(chair.getModelId()).ifPresent(animations -> state.chairAnimations = animations);

        var player = Minecraft.getInstance().player;
        if (canShowHitBox(player) && renderHitBox) {
            state.hitbox = chair.getBoundingBox().move(-state.x, -state.y, -state.z);
        }

        if (state.chairInfo.isGeckoModel()) {
            state.modelType = ModelType.GECKO;
        } else {
            state.modelType = ModelType.SIMPLE_BEDROCK;
        }
    }

    @Override
    public void submit(EntityChairRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (state.hitbox != null) {
            submitHitBox(state.hitbox, poseStack, submitNodeCollector);
        } else {
            submitChair(state, poseStack, submitNodeCollector, camera);
        }
    }

    private boolean canShowHitBox(@Nullable Player player) {
        if (player != null && player.isShiftKeyDown()) {
            return player.getMainHandItem().getItem() == InitItems.CHAIR_SHOW.get();
        }
        return false;
    }

    private void submitHitBox(AABB hitbox, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        LevelRenderer.renderLineBox(poseStack, bufferIn.getBuffer(RenderTypes.lines()), hitbox, 1.0F, 0, 0, 1.0F);
    }

    private void submitChair(EntityChairRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        // GeckoLib 接管渲染
        if (state.modelType == ModelType.GECKO) {
            this.geckoEntityChairRenderer.submit(state, poseStack, submitNodeCollector, camera);
            return;
        }

        if (state.modelType == ModelType.SIMPLE_BEDROCK) {
            this.model = state.bedrockModel;
            // 模型动画设置
            this.model.setAnimations(state.chairAnimations);

            GlWrapper.setPoseStack(poseStack);
            super.submit(state, poseStack, submitNodeCollector, camera);
            GlWrapper.clearPoseStack();
        }
    }

    @Override
    protected void scale(EntityChairRenderState state, PoseStack poseStack) {
        float scale = state.chairInfo.getRenderEntityScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public Identifier getTextureLocation(EntityChairRenderState state) {
        if (state.chairInfo == null) {
            return DEFAULT_TEXTURE;
        }
        return state.chairInfo.getTexture();
    }

    @Override
    protected void setupRotations(EntityChairRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - state.yRot));
    }

    @Override
    protected boolean shouldShowName(EntityChair entity, double distanceToCameraSq) {
        return entity.shouldShowName();
    }
}
