/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.core;

public enum AnimationState {
    /**
     * 待机中
     */
    IDLE,
    /**
     * 开始前的过渡
     */
    BEGINNING_TRANSITION,
    /**
     * 播放中
     */
    RUNNING,
    /**
     * PLAY_ONCE 类型的动画结束后的过渡
     */
    ENDING_TRANSITION
}