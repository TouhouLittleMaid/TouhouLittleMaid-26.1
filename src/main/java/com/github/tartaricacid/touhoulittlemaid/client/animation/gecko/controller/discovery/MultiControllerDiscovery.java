package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.discovery;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerFactory;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerResource;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MultiControllerDiscovery<T extends AnimatableEntity<?>> implements ControllerDiscovery<T> {
    private final Predicate<String> controllerNamePattern;
    private final Predicate<String> eventNamePattern;
    private final BiFunction<String, T, IAnimationController<T>> controllerFunc;

    public MultiControllerDiscovery(String category, String pattern, BiFunction<String, T, IAnimationController<T>> controllerFunc) {
        this.controllerNamePattern = Pattern.compile(String.format("^%s\\.%s(_.+){0,1}$", category, pattern)).asMatchPredicate();
        this.eventNamePattern = Pattern.compile(String.format("^%s_ctrl_%s(_.+){0,1}$", category, pattern)).asMatchPredicate();
        this.controllerFunc = controllerFunc;
    }

    @Override
    public ControllerFactory<T> process(ControllerResource resource) {
        var names = new ObjectRBTreeSet<String>();
        Object2ReferenceMaps.fastForEach(resource.controllerData(), entry -> {
            if (controllerNamePattern.test(entry.getKey())) {
                names.add(entry.getKey());
            }
        });
        Object2ReferenceMaps.fastForEach(resource.eventHandlers(), entry -> {
            if (eventNamePattern.test(entry.getKey())) {
                var controllerName = entry.getKey().replace("_ctrl_", ".");
                names.add(controllerName);
            }
        });
        return (animatable, consumer) -> {
            for (String name : names) {
                consumer.accept(controllerFunc.apply(name, animatable));
            }
        };
    }
}
