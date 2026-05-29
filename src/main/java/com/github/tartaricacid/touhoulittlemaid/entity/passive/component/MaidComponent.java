package com.github.tartaricacid.touhoulittlemaid.entity.passive.component;

import java.util.Set;

public interface MaidComponent {
    default Set<Class<? extends MaidComponent>> dependsOn() {
        return Set.of();
    }

    default int priority() {
        return 0;
    }

    default void init(MaidComponents host) {
    }
}
