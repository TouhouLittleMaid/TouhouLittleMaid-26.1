package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;

import java.util.function.Predicate;

public interface ControllerDiscovery<T extends AnimatableEntity<?>> {
    ControllerFactory<T> process(ControllerResource model);

    default ControllerDiscovery<T> withCondition(Predicate<T> test) {
        return (model) -> {
            var collection = process(model);
            return (animatable, consumer) -> {
                if (test.test(animatable)) {
                    collection.create(animatable, consumer);
                }
            };
        };
    }
}
