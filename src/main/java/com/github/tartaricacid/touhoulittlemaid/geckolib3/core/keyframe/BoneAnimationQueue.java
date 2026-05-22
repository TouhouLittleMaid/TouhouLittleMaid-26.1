/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.bone.BoneKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.point.AnimationPoint;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot.BoneSnapshot;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot.BoneTopLevelSnapshot;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.OrderedSegmentSearcher;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BoneAnimationQueue {
    public final BoneTopLevelSnapshot topLevelSnapshot;
    public final BoneSnapshot transitionOffset;

    @Nullable
    public OrderedSegmentSearcher<BoneKeyFrame> rotationKeyFrames;
    @Nullable
    public OrderedSegmentSearcher<BoneKeyFrame> positionKeyFrames;
    @Nullable
    public OrderedSegmentSearcher<BoneKeyFrame> scaleKeyFrames;

    private boolean active = false;
    private float blendWeight = 1;

    public Vector3f rotationOffset;
    public Vector3f positionOffset;
    public Vector3f scaleOffset;

    public boolean disableEndingTransition;

    public AnimationPoint rotation;
    public AnimationPoint position;
    public AnimationPoint scale;

    public BoneAnimationQueue(BoneTopLevelSnapshot snapshot) {
        topLevelSnapshot = snapshot;
        transitionOffset = new BoneSnapshot(snapshot.bone);
    }

    public void setActive(BoneAnimation animation, boolean disableEndingTransition) {
        if (!animation.rotationKeyFrames.isEmpty()) {
            rotationKeyFrames = new OrderedSegmentSearcher<>(animation.rotationKeyFrames, 0, BoneKeyFrame::getEndTick);
        } else {
            rotationKeyFrames = null;
        }
        if (!animation.positionKeyFrames.isEmpty()) {
            positionKeyFrames = new OrderedSegmentSearcher<>(animation.positionKeyFrames, 0, BoneKeyFrame::getEndTick);
        } else {
            positionKeyFrames = null;
        }
        if (!animation.scaleKeyFrames.isEmpty()) {
            scaleKeyFrames = new OrderedSegmentSearcher<>(animation.scaleKeyFrames, 0, BoneKeyFrame::getEndTick);
        } else {
            scaleKeyFrames = null;
        }
        transitionOffset.copyFrom(topLevelSnapshot.bone);
        active = true;
        this.disableEndingTransition = disableEndingTransition;
        resetQueues();
    }

    public BoneSnapshot transitionOffset() {
        return transitionOffset;
    }

    public AnimationPoint rotation() {
        return rotation;
    }

    public AnimationPoint position() {
        return position;
    }

    public AnimationPoint scale() {
        return scale;
    }

    /**
     * 该骨骼上是否有动画
     */
    public boolean isActive() {
        return active;
    }

    public void setInactive() {
        rotationKeyFrames = null;
        positionKeyFrames = null;
        scaleKeyFrames = null;

        rotationOffset = null;
        positionOffset = null;
        scaleOffset = null;

        active = false;
        resetQueues();
    }

    /**
     * 权重不小于 0
     */
    public float getBlendWeight() {
        return blendWeight;
    }

    public void setBlendWeight(float blendWeight) {
        this.blendWeight = blendWeight > 0 ? blendWeight : 0;   // bb 里就是这样的
    }

    public void resetQueues() {
        rotation = null;
        position = null;
        scale = null;
    }
}