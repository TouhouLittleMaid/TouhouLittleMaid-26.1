/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.AnimationControllerContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.AnimationContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import org.joml.Vector3f;

public abstract class AnimationPoint {
    /**
     * 当前关键帧播放进度
     */
    public double currentTick;
    /**
     * 当前关键帧总长度
     */
    public final double totalTick;
    /**
     * 与动画控制器相关的 molang 上下文
     */
    private final AnimationControllerContext context;

    public AnimationPoint(double currentTick, double totalTick, AnimationControllerContext context) {
        this.totalTick = totalTick;
        this.context = context;
        this.currentTick = currentTick;
    }

    protected double getPercentCompleted() {
        return totalTick == 0 ? 1 : (currentTick / totalTick);
    }

    protected void setupControllerContext(ExpressionEvaluator<AnimationContext<?>> evaluator) {
        evaluator.entity().setAnimationControllerContext(context);
    }

    public abstract void getLerpPoint(ExpressionEvaluator<AnimationContext<?>> evaluator, Vector3f dest);
}
