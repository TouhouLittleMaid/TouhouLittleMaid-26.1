package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.CodedAnimationController;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.RenderContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.model.provider.data.EntityModelData;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.NotNull;

public class AnimationEvent<T extends AnimatableEntity<?>>  {
    private final T animatable;
    private final float limbSwing;
    private final float limbSwingAmount;
    private final int entityTickCount;
    private final float partialTick;
    private final boolean isMoving;
    private final RenderContext renderContext;
    private float renderTicks;
    private final EntityModelData extraData;
    private final EntityRenderState renderState;
    protected CodedAnimationController<T> codedController;

    public AnimationEvent(T animatable,
                          float limbSwing, float limbSwingAmount,
                          int entityTickCount, float partialTick,
                          boolean isMoving,
                          RenderContext renderContext,
                          @NotNull EntityModelData extraData, EntityRenderState renderState) {
        this.animatable = animatable;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.entityTickCount = entityTickCount;
        this.partialTick = partialTick;
        this.renderState = renderState;
        this.renderTicks = entityTickCount + partialTick;
        this.isMoving = isMoving;
        this.renderContext = renderContext;
        this.extraData = extraData;
    }

    public float getRenderTicks() {
        return renderTicks;
    }

    public void setRenderTicks(float renderTicks) {
        this.renderTicks = renderTicks;
    }

    public T getAnimatableEntity() {
        return animatable;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public int getEntityTickCount() {
        return entityTickCount;
    }

    public float getRequestedPartialTick() {
        return renderState.partialTick;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public EntityRenderState getRenderState() {
        return renderState;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public RenderContext getRenderContext() {
        return renderContext;
    }

    public CodedAnimationController<T> getCodedController() {
        return codedController;
    }

    public void setCodedAnimationController(CodedAnimationController<T> controller) {
        this.codedController = controller;
    }

    @NotNull
    public EntityModelData getExtraData() {
        return extraData;
    }
}
