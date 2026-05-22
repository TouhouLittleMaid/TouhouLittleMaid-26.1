package com.github.tartaricacid.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.extended.Matrix4fAccessor;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Matrix4f.class)
public abstract class Matrix4fMixin implements Matrix4fAccessor {
    @Shadow
    int properties;

    @Shadow
    float m00;
    @Shadow
    float m01;
    @Shadow
    float m02;
    @Shadow
    float m03;
    @Shadow
    float m10;
    @Shadow
    float m11;
    @Shadow
    float m12;
    @Shadow
    float m13;
    @Shadow
    float m20;
    @Shadow
    float m21;
    @Shadow
    float m22;
    @Shadow
    float m23;
    @Shadow
    float m30;
    @Shadow
    float m31;
    @Shadow
    float m32;
    @Shadow
    float m33;

    @Unique
    @Override
    public void tlm$extractTransform(float[] data, int offset) {
        data[offset] = properties;

        data[offset + 1] = m00;
        data[offset + 2] = m01;
        data[offset + 3] = m02;

        data[offset + 4] = m10;
        data[offset + 5] = m11;
        data[offset + 6] = m12;

        data[offset + 7] = m20;
        data[offset + 8] = m21;
        data[offset + 9] = m22;

        data[offset + 10] = m30;
        data[offset + 11] = m31;
        data[offset + 12] = m32;
    }

    @Unique
    @Override
    public void tlm$readTransform(float[] data, int offset) {
        properties = (int) data[offset];

        m00 = data[offset + 1];
        m01 = data[offset + 2];
        m02 = data[offset + 3];
        m03 = 0;

        m10 = data[offset + 4];
        m11 = data[offset + 5];
        m12 = data[offset + 6];
        m13 = 0;

        m20 = data[offset + 7];
        m21 = data[offset + 8];
        m22 = data[offset + 9];
        m23 = 0;

        m30 = data[offset + 10];
        m31 = data[offset + 11];
        m32 = data[offset + 12];
        m33 = 1;
    }
}
