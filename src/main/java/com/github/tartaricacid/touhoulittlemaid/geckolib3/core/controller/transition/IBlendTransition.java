package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.transition;

public interface IBlendTransition {
    float get(float tick);

    /**
     * Tick
     */
    float length();

    default IBlendTransition startNew() {
        return this;
    }
}
