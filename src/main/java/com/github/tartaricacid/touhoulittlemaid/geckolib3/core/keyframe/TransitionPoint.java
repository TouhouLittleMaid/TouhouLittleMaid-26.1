package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.AnimationControllerContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.bone.BoneKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.AnimationContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import org.joml.Vector3f;

public class TransitionPoint extends AnimationPoint {
    private final Vector3f offsetPoint;
    private final BoneKeyFrame dstKeyframe;
    private Vector3f target;

    public TransitionPoint(double currentTick, double totalTick, Vector3f offsetPoint, BoneKeyFrame dstKeyframe, AnimationControllerContext context) {
        super(currentTick, totalTick, context);
        this.offsetPoint = offsetPoint;
        this.dstKeyframe = dstKeyframe;
    }

    @Override
    public void getLerpPoint(ExpressionEvaluator<AnimationContext<?>> evaluator, Vector3f dest) {
        setupControllerContext(evaluator);
        if (target == null) {
            target = dstKeyframe.eval(evaluator);
        }
        offsetPoint.lerp(target, (float) getPercentCompleted(), dest);
    }
}
