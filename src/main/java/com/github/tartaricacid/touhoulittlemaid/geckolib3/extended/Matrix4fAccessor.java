package com.github.tartaricacid.touhoulittlemaid.geckolib3.extended;

public interface Matrix4fAccessor {
    int STRIDE = 13;    // 应该没必要对齐

    void tlm$extractTransform(float[] data, int offset);

    void tlm$readTransform(float[] data, int offset);
}
