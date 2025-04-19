/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot.BoneSnapshot;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot.BoneTopLevelSnapshot;
import org.jetbrains.annotations.Nullable;

public class BoneAnimationQueue {
    public final BoneTopLevelSnapshot topLevelSnapshot;
    public final BoneSnapshot controllerSnapshot;
    @Nullable
    public BoneAnimation animation;

    public AnimationPoint rotation = null;
    public AnimationPoint position = null;
    public AnimationPoint scale = null;

    public BoneAnimationQueue(BoneTopLevelSnapshot snapshot) {
        topLevelSnapshot = snapshot;
        controllerSnapshot = new BoneSnapshot(snapshot);
    }

    public BoneSnapshot snapshot() {
        return controllerSnapshot;
    }

    public void updateSnapshot() {
        controllerSnapshot.copyFrom(topLevelSnapshot);
    }

    public void resetQueues() {
        rotation = null;
        position = null;
        scale = null;
    }
}