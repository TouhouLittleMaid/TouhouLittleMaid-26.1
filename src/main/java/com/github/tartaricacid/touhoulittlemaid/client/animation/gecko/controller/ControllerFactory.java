package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;

import java.util.function.Consumer;

public interface ControllerFactory<T extends AnimatableEntity<?>> {
    void create(T animatable, Consumer<IAnimationController<T>> consumer);
}
