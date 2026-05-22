package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.LoopType;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import net.minecraft.util.Mth;

import java.util.function.BiPredicate;

public class AnimationState {
    private final String animationName;
    private final LoopType loopType;
    private final int priority;
    private final BiPredicate<IMaid, AnimationEvent<?>> predicate;

    public AnimationState(String animationName, LoopType loopType, int priority, BiPredicate<IMaid, AnimationEvent<?>> predicate) {
        this.animationName = animationName;
        this.loopType = loopType;
        this.priority = Mth.clamp(priority, Priority.HIGHEST, Priority.LOWEST);
        this.predicate = predicate;
    }

    public BiPredicate<IMaid, AnimationEvent<?>> getPredicate() {
        return predicate;
    }

    public String getAnimationName() {
        return animationName;
    }

    public LoopType getLoopType() {
        return loopType;
    }

    public int getPriority() {
        return priority;
    }
}
