package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.util.MathUtil;
import org.joml.Vector3f;

public class AnimationVec3 extends Vector3f {
    public float endingTransitionPercentProgress = 1f;

    public AnimationVec3() {
    }

    public AnimationVec3(float x, float y, float z) {
        super(x, y, z);
    }

    public AnimationVec3(Vector3f point) {
        super(point);
    }

    public void setEndingTransitionPercentProgressIfLess(float endingTransitionPercentProgress) {
        if (endingTransitionPercentProgress < this.endingTransitionPercentProgress) {
            this.endingTransitionPercentProgress = endingTransitionPercentProgress;
        }
    }

    public void apply(Vector3f dst) {
        var endingTransitionPercentProgress = this.endingTransitionPercentProgress;
        if (endingTransitionPercentProgress == 0) {
            dst.set(this);
        } else {
            MathUtil.lerpValues(endingTransitionPercentProgress, this, dst, dst);
        }
    }

    public void applyRotation(Vector3f dst, Vector3f initRot) {
        var endingTransitionPercentProgress = this.endingTransitionPercentProgress;
        if (endingTransitionPercentProgress == 0) {
            dst.set(this);
        } else {
            MathUtil.lerpRotationValues(endingTransitionPercentProgress, this, dst, initRot, dst);
        }
    }
}
