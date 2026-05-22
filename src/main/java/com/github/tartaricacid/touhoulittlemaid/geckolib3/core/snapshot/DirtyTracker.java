/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor.IBoneView;

public class DirtyTracker {
    public IBoneView model;
    public boolean hasScaleChanged;
    public boolean hasPositionChanged;
    public boolean hasRotationChanged;

    public DirtyTracker(boolean hasScaleChanged, boolean hasPositionChanged, boolean hasRotationChanged, IBoneView model) {
        this.hasScaleChanged = hasScaleChanged;
        this.hasPositionChanged = hasPositionChanged;
        this.hasRotationChanged = hasRotationChanged;
        this.model = model;
    }
}
