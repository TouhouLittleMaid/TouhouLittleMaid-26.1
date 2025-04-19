package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.AnimationControllerContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.bone.BoneKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.AnimationContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import org.joml.Vector3f;

public class KeyFramePoint extends AnimationPoint {
    public final BoneKeyFrame keyframe;
    private int lastTick = Integer.MIN_VALUE;
    private Vector3f lastValue;
    private Vector3f nextValue;

    public KeyFramePoint(double currentTick, BoneKeyFrame keyframe, AnimationControllerContext context) {
        super(currentTick, keyframe.getTotalTick(), context);
        this.keyframe = keyframe;
        updateTick(currentTick);
    }

    public void updateTick(double currentTick) {
        this.currentTick = currentTick;
    }

    @Override
    public void getLerpPoint(ExpressionEvaluator<AnimationContext<?>> evaluator, Vector3f dest) {
        setupControllerContext(evaluator);
        if (totalTick == 0) {
            if (lastTick == 0) {
                dest.set(lastValue);
                return;
            }
            lastValue = keyframe.getLerpPoint(evaluator, 1);
            lastTick = 0;
            dest.set(lastValue);
            return;
        }
        int floorTick = (int) Math.floor(currentTick);
        float fract =  (float)currentTick - floorTick;

        if (floorTick != lastTick) {
            lastValue = nextValue != null ? nextValue : keyframe.getLerpPoint(evaluator, (double) floorTick / totalTick);
            nextValue = keyframe.getLerpPoint(evaluator, (double) (floorTick + 1) / totalTick);
            lastTick = floorTick;
        }

        lastValue.lerp(nextValue, fract, dest);
    }
}
