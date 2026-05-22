package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.instance.SoundInstanceManager;

public class ControllerContext {
    private final boolean hasSoundManager;
    private SoundInstanceManager soundManager;
    private boolean allAnimationsFinished;
    private boolean anyAnimationFinished;

    public ControllerContext(boolean hasSoundManager) {
        this.hasSoundManager = hasSoundManager;
    }

    public void setAllAnimationsFinished(boolean allAnimationsFinished) {
        this.allAnimationsFinished = allAnimationsFinished;
    }

    public void setAnyAnimationFinished(boolean anyAnimationFinished) {
        this.anyAnimationFinished = anyAnimationFinished;
    }

    public boolean isAllAnimationsFinished() {
        return allAnimationsFinished;
    }

    public boolean isAnyAnimationFinished() {
        return anyAnimationFinished;
    }

    public SoundInstanceManager soundManager() {
        if (!hasSoundManager) {
            return null;
        }
        if (soundManager == null) {
            soundManager = new SoundInstanceManager();
        }
        return soundManager;
    }
}
