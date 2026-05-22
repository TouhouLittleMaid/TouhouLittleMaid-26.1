/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */
package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.BoneAnimation;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.event.EventKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Animation(String animationName, float animationLength, LoopType loop, @Nullable IValue blendWeight,
                        List<BoneAnimation> boneAnimations, List<EventKeyFrame<String>> soundKeyFrames,
                        List<EventKeyFrame<IValue[]>> customInstructionKeyframes) {
    public boolean isEmpty() {
        return boneAnimations.isEmpty() && soundKeyFrames.isEmpty() && customInstructionKeyframes.isEmpty();
    }
}