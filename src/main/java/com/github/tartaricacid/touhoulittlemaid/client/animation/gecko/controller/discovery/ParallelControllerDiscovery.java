package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.discovery;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerFactory;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerResource;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceRBTreeMap;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ParallelControllerDiscovery<T extends AnimatableEntity<?>> implements ControllerDiscovery<T> {
    private final String category;
    private final String name;
    private final Predicate<String> controllerNamePattern;
    private final Predicate<String> eventNamePattern;
    private final Predicate<String> animationNamePattern;
    private final TriFunction<String, T, String, IAnimationController<T>> controllerFunc;

    public ParallelControllerDiscovery(String category, String name, boolean multi, TriFunction<String, T, String, IAnimationController<T>> controllerFunc) {
        this.category = category;
        this.name = name;
        if (multi) {
            this.controllerNamePattern = Pattern.compile(String.format("^%s\\.%s_.+", category, name)).asMatchPredicate();
            this.eventNamePattern = Pattern.compile(String.format("^%s_ctrl_%s_.+", category, name)).asMatchPredicate();
        } else {
            this.controllerNamePattern = Pattern.compile(String.format("^%s\\.%s_[0-7]$", category, name)).asMatchPredicate();
            this.eventNamePattern = Pattern.compile(String.format("^%s_ctrl_%s_[0-7]$", category, name)).asMatchPredicate();
        }
        this.animationNamePattern = Pattern.compile(String.format("^%s[0-7]$", name)).asMatchPredicate();
        this.controllerFunc = controllerFunc;
    }

    @Override
    public ControllerFactory<T> process(ControllerResource resource) {
        var names = new Object2ReferenceRBTreeMap<String, String>();
        Object2ReferenceMaps.fastForEach(resource.controllerData(), entry -> {
            if (controllerNamePattern.test(entry.getKey())) {
                names.put(entry.getKey(), null);
            }
        });
        Object2ReferenceMaps.fastForEach(resource.eventHandlers(), entry -> {
            if (eventNamePattern.test(entry.getKey())) {
                var controllerName = entry.getKey().replace("_ctrl_", ".");
                try {
                    var animationSuffix = controllerName.substring(category.length() + name.length() + 2);
                    var index = Integer.parseInt(animationSuffix);
                    if (index >= 0 && index <= 7) {
                        var animationName = name + animationSuffix;
                        if (resource.animations().containsKey(animationName)) {
                            names.put(controllerName, animationName);
                            return;
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
                names.put(controllerName, null);
            }
        });
        Object2ReferenceMaps.fastForEach(resource.animations(), entry -> {
            if (!entry.getValue().isEmpty() && animationNamePattern.test(entry.getKey())) {
                var controllerName = String.format("%s.%s_%s", category, name, entry.getKey().substring(name.length()));
                names.put(controllerName, entry.getKey());
            }
        });
        return ((animatable, consumer) -> {
            Object2ReferenceMaps.fastForEach(names, entry -> {
                consumer.accept(controllerFunc.apply(entry.getKey(), animatable, entry.getValue()));
            });
        });
    }
}
