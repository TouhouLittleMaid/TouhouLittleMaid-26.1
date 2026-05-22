/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */
package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder;

import org.jetbrains.annotations.Nullable;

public class AnimationBuilder {
    private String animationName;
    @Nullable
    private LoopType loopType;

    public AnimationBuilder addAnimation(String animationName, LoopType loopType) {
        this.animationName = animationName;
        this.loopType = loopType;
        return this;
    }

    public AnimationBuilder addAnimation(String animationName) {
        this.animationName = animationName;
        this.loopType = null;
        return this;
    }

    public AnimationBuilder playOnce(String animationName) {
        return this.addAnimation(animationName, LoopType.PLAY_ONCE);
    }

    public AnimationBuilder loop(String animationName) {
        return this.addAnimation(animationName, LoopType.LOOP);
    }

    public AnimationBuilder playAndHold(String animationName) {
        return this.addAnimation(animationName, LoopType.HOLD_ON_LAST_FRAME);
    }

    public AnimationBuilder clearAnimations() {
        this.animationName = null;
        this.loopType = null;
        return this;
    }

    public boolean isEmpty() {
        return animationName != null;
    }

    public String animationName() {
        return animationName;
    }

    public @Nullable LoopType loopType() {
        return loopType;
    }
}
