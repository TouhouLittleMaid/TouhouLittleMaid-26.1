package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.bone;


import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.FloatValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.RotationValue;

@SuppressWarnings("FieldMayBeFinal,unused")
public class RawBoneKeyFrame {
    public float startTick;
    public EasingType easingType;

    public float preX;
    public IValue preXValue;
    public float preY;
    public IValue preYValue;
    public float preZ;
    public IValue preZValue;

    public float postX;
    public IValue postXValue;
    public float postY;
    public IValue postYValue;
    public float postZ;
    public IValue postZValue;

    public boolean contiguous = true;

    public Vector3v preValue;
    public Vector3v postValue;

    public RawBoneKeyFrame() {
    }

    private IValue getValue(IValue value, float primitive, boolean isRotation, boolean flip) {
        if (value == null) {
            if (isRotation) {
                return new FloatValue(RotationValue.processValue(primitive, flip));
            } else {
                return new FloatValue(primitive);
            }
        }
        if (isRotation) {
            return new RotationValue(value, flip);
        } else {
            return value;
        }
    }

    public void init(boolean isRotation) {
        if (preValue != null) {
            return;
        }

        preValue = new Vector3v(
                getValue(this.preXValue, this.preX, isRotation, true),
                getValue(this.preYValue, this.preY, isRotation, true),
                getValue(this.preZValue, this.preZ, isRotation, false));
        if (contiguous) {
            postValue = preValue;
        } else {
            postValue = new Vector3v(
                    getValue(this.postXValue, this.postX, isRotation, true),
                    getValue(this.postYValue, this.postY, isRotation, true),
                    getValue(this.postZValue, this.postZ, isRotation, false));
        }
        if (easingType == null) {
            easingType = EasingType.LINEAR;
        }
    }

    public float startTick() {
        return startTick;
    }

    public EasingType easingType() {
        return easingType;
    }

    public Vector3v preValue() {
        return preValue;
    }

    public Vector3v postValue() {
        return postValue;
    }
}
