/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.point;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.AnimationContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.MolangContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import org.joml.Vector3f;

public abstract class AnimationPoint {
    /**
     * 当前关键帧播放进度
     */
    public final float currentTick;
    /**
     * 当前关键帧总长度
     */
    public final float totalTick;
    /**
     * 与动画相关的 molang 上下文
     */
    private final AnimationContext context;

    public Vector3f lastLerpResult;

    public AnimationPoint(float currentTick, float totalTick, AnimationContext context) {
        this.currentTick = currentTick;
        this.totalTick = totalTick;
        this.context = context;
    }

    public float getPercentCompleted() {
        return totalTick == 0 ? 1 : (currentTick / totalTick);
    }

    protected void setupAnimationContext(ExpressionEvaluator<MolangContext<?>> evaluator) {
        evaluator.entity().setAnimationContext(context);
    }

    public abstract Vector3f getLerpPoint(ExpressionEvaluator<MolangContext<?>> evaluator);
}
