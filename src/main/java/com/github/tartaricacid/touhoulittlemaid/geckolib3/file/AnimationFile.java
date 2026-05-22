package com.github.tartaricacid.touhoulittlemaid.geckolib3.file;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.Animation;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;


public class AnimationFile {
    private final Object2ReferenceMap<String, Animation> animations = new Object2ReferenceOpenHashMap<>();

    public Object2ReferenceMap<String, Animation> animations() {
        return this.animations;
    }
}