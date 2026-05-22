package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.point;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.AnimationContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.MolangContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import org.joml.Vector3f;

public class EndingTransitionPoint extends AnimationPoint {
    protected final Vector3f srcPoint;

    public EndingTransitionPoint(float currentTick, float transitionLength, Vector3f srcPoint, AnimationContext context) {
        super(currentTick, transitionLength, context);
        this.srcPoint = srcPoint;
    }

    @Override
    public float getPercentCompleted() {
        if (totalTick == 0) {
            return currentTick == 0 ? 0 : 1;
        }
        return currentTick / totalTick;
    }

    @Override
    public Vector3f getLerpPoint(ExpressionEvaluator<MolangContext<?>> evaluator) {
        if (lastLerpResult == null) {
            lastLerpResult = new Vector3f(srcPoint);
        } else {
            lastLerpResult.set(srcPoint);
        }
        return srcPoint;
    }
}
