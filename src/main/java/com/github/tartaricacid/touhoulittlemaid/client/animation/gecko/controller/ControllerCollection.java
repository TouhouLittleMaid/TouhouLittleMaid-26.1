package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import java.util.function.Consumer;

public class ControllerCollection<T extends AnimatableEntity<?>> {
    private final ReferenceArrayList<ControllerDiscovery<T>> discoveries = new ReferenceArrayList<>();

    public boolean isEmpty() {
        return discoveries.isEmpty();
    }

    public Consumer<T> build(ControllerResource model) {
        var factories = new ReferenceArrayList<ControllerFactory<T>>(discoveries.size());
        for (var discovery : discoveries) {
            factories.add(discovery.process(model));
        }
        return animatable -> {
            Consumer<IAnimationController<T>> consumer = animatable::addAnimationController;
            for (var factory : factories) {
                factory.create(animatable, consumer);
            }
        };
    }

    public ControllerDiscovery<T> add(ControllerDiscovery<T> factory) {
        discoveries.add(factory);
        return factory;
    }
}
