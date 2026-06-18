package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoChairEntity;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoEntityChairRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.ModelType;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.GeckoUpdateTask;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.Collections;

public class EntityChairRenderer extends LivingEntityRenderer<EntityChair, EntityChairRenderState, EntityChairModel> {
    public static final Identifier DEFAULT_TEXTURE = IdentifierUtil.modLoc("textures/entity/empty.png");
    private static final String DEFAULT_CHAIR_ID = "touhou_little_maid:cushion";
    public static boolean renderHitBox = true;
    private final GeckoEntityChairRenderer geckoEntityChairRenderer;

    public EntityChairRenderer(EntityRendererProvider.Context rendererManager) {
        super(rendererManager, new EntityChairModel(), 0);
        this.geckoEntityChairRenderer = new GeckoEntityChairRenderer(rendererManager);
    }

    @Override
    public EntityChairRenderState createRenderState() {
        return new EntityChairRenderState();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void extractRenderState(EntityChair chair, EntityChairRenderState state, float partialTicks) {
        state.clear();
        super.extractRenderState(chair, state, partialTicks);

        state.hasPassenger = chair.hasPassenger();
        state.passengerYRot = chair.getPassengerYaw();
        state.passengerXRot = chair.getPassengerPitch();

        // 读取默认模型，用于清除不存在模型的缓存残留
        CustomPackLoader.CHAIR_MODELS.getModel(DEFAULT_CHAIR_ID).ifPresent(model -> state.bedrockModel = model);
        CustomPackLoader.CHAIR_MODELS.getInfo(DEFAULT_CHAIR_ID).ifPresent(info -> state.chairInfo = info);

        // 通过模型 id 获取对应数据
        CustomPackLoader.CHAIR_MODELS.getModel(chair.getModelId()).ifPresent(model -> state.bedrockModel = model);
        CustomPackLoader.CHAIR_MODELS.getInfo(chair.getModelId()).ifPresent(info -> state.chairInfo = info);

        if (state.chairInfo != null) {
            state.chairAnimations = CustomPackLoader.CHAIR_MODELS.getAnimation(state.chairInfo.getModelId().toString())
                    .orElse(Collections.emptyList());
        }

        var player = Minecraft.getInstance().player;
        if (canShowHitBox(player) && renderHitBox) {
            state.hitbox = chair.getBoundingBox().move(-state.x, -state.y, -state.z);
        }

        if (state.chairInfo != null && state.chairInfo.isGeckoModel()) {
            state.modelType = ModelType.GECKO;
            var geckoEntity = getGeckoEntity(chair);
            if (geckoEntity != null) {
                geckoEntity.setChair(state.chairInfo);
                state.geckoUpdateTask = (GeckoUpdateTask<GeckoRenderData>) geckoEntity.createUpdateTask(state);
            }
        } else {
            state.modelType = ModelType.SIMPLE_BEDROCK;
        }
    }

    @Nullable
    public GeckoChairEntity getGeckoEntity(EntityChair entity) {
        return entity.getAnimatableEntity();
    }

    @Override
    public void submit(EntityChairRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, buffer) -> {
            Gizmos.cuboid(hitbox, GizmoStyle.fill(ARGB.colorFromFloat(1.0F, 1.0F, 0, 0)));
        });
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
            this.model.setupAnim(state);
            super.submit(state, poseStack, submitNodeCollector, camera);
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
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - bodyRot));
    }

    @Override
    protected boolean shouldShowName(EntityChair entity, double distanceToCameraSq) {
        return entity.shouldShowName();
    }
}
