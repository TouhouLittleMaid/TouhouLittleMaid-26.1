package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.transition;

public class LinearBlendTransition implements IBlendTransition {
    private final float ticks;

    
    @SuppressWarnings("unused")
    public LinearBlendTransition(float length) {
        this.ticks = length * 20;
    }

    @Override
    public float get(float tick) {
        return ticks != 0 ? tick / ticks : 1;
    }

    @Override
    public float length() {
        return ticks;
    }
}
