package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.point;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.bone.TransitionKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.AnimationContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.MolangContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.util.MathUtil;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import org.joml.Vector3f;

public class BeginningTransitionPoint extends AnimationPoint {
    protected final float transitionPercentProgress;
    protected final Vector3f offsetPoint;
    protected final TransitionKeyFrame dstKeyframe;

    public BeginningTransitionPoint(float currentTick, float transitionPercentProgress, float transitionLength, Vector3f offsetPoint, TransitionKeyFrame dstKeyframe, AnimationContext context) {
        super(currentTick, transitionLength, context);
        this.transitionPercentProgress = transitionPercentProgress;
        this.offsetPoint = offsetPoint;
        this.dstKeyframe = dstKeyframe;
    }

    @Override
    public Vector3f getLerpPoint(ExpressionEvaluator<MolangContext<?>> evaluator) {
        setupAnimationContext(evaluator);
        var dst = dstKeyframe.getTransitionDst(evaluator);
        MathUtil.lerpValues(transitionPercentProgress, offsetPoint, dst, dst);
        if (lastLerpResult == null) {
            lastLerpResult = new Vector3f(dst);
        } else {
            lastLerpResult.set(dst);
        }
        return dst;
    }

    public Vector3f getTransitionDst(ExpressionEvaluator<MolangContext<?>> evaluator) {
        setupAnimationContext(evaluator);
        return dstKeyframe.getTransitionDst(evaluator);
    }

    public Vector3f getTransitionOffset() {
        return offsetPoint;
    }

    public float getTransitionPercentProgress() {
        return transitionPercentProgress;
    }
}
