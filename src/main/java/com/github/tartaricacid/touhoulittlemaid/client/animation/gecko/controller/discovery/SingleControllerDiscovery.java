package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.discovery;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerFactory;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerResource;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;

import java.util.function.BiFunction;

public class SingleControllerDiscovery<T extends AnimatableEntity<?>> implements ControllerDiscovery<T> {
    private final String controllerName;
    private final String scriptName;
    private final String[] animationNames;
    private final boolean hybrid;
    private final BiFunction<String, T, IAnimationController<T>> controllerFunc;

    public SingleControllerDiscovery(String category, String name, String[] animationNames, boolean hybrid, BiFunction<String, T, IAnimationController<T>> controllerFunc) {
        this.controllerName = String.format("%s.%s", category, name);
        this.scriptName = String.format("%s_ctrl_%s", category, name);
        this.animationNames = animationNames;
        this.hybrid = hybrid;
        this.controllerFunc = controllerFunc;
    }

    @Override
    public ControllerFactory<T> process(ControllerResource resource) {
        var accept = false;
        if (hybrid && resource.controllerData().containsKey(controllerName)) {
            accept = true;
        } else if (resource.eventHandlers().containsKey(scriptName)) {
            accept = true;
        } else if (animationNames != null) {
            for (String animationName : animationNames) {
                var anim = resource.animations().get(animationName);
                if (anim != null && !anim.isEmpty()) {
                    accept = true;
                    break;
                }
            }
        }
        if (accept) {
            return ((animatable, consumer) -> {
                consumer.accept(controllerFunc.apply(controllerName, animatable));
            });
        } else {
            return ((animatable, consumer) -> {});
        }
    }
}
