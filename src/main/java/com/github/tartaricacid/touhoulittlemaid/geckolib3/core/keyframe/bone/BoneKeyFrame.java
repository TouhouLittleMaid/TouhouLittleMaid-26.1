package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.bone;

import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import org.joml.Vector3f;

public abstract class BoneKeyFrame {
    protected final float startTick;
    protected final float totalTick;
    protected final float endTick;
    protected final Vector3v beginPoint;

    public BoneKeyFrame(float startTick, float totalTick, Vector3v beginPoint) {
        this.startTick = startTick;
        this.totalTick = totalTick;
        this.endTick = startTick + totalTick;
        this.beginPoint = beginPoint;
    }

    public float getStartTick() {
        return startTick;
    }

    public float getTotalTick() {
        return totalTick;
    }

    public float getEndTick() {
        return endTick;
    }

    public abstract Vector3f getLerpPoint(ExpressionEvaluator<?> evaluator, float percentCompleted);

    protected static boolean isBegin(float percentCompleted) {
        return percentCompleted < 0.00001f;
    }

    protected static boolean isEnd(float percentCompleted) {
        return percentCompleted > 0.99999f;
    }
}
