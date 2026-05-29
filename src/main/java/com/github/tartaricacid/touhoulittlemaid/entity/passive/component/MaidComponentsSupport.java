package com.github.tartaricacid.touhoulittlemaid.entity.passive.component;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle.AiStepComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle.BaseTickComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle.SaveComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MaidComponentsSupport {
    private MaidComponentsSupport() {
    }

    public record LifecycleLists(
            List<BaseTickComponent> baseTickComponents,
            List<AiStepComponent> aiStepComponents,
            List<SaveComponent> saveComponents
    ) {
    }

    public static List<MaidComponent> sortComponents(List<MaidComponent> components) {
        Map<Class<? extends MaidComponent>, MaidComponent> byConcrete = new HashMap<>();
        for (MaidComponent component : components) {
            byConcrete.put(component.getClass(), component);
        }

        List<MaidComponent> sorted = new ArrayList<>();
        Map<Class<? extends MaidComponent>, Boolean> visited = new HashMap<>();
        Map<Class<? extends MaidComponent>, Boolean> visiting = new HashMap<>();

        List<MaidComponent> byPriority = new ArrayList<>(components);
        byPriority.sort(Comparator.comparingInt(MaidComponent::priority));

        for (MaidComponent component : byPriority) {
            visit(component, byConcrete, sorted, visited, visiting);
        }
        return sorted;
    }

    private static void visit(MaidComponent component,
                              Map<Class<? extends MaidComponent>, MaidComponent> byConcrete,
                              List<MaidComponent> sorted,
                              Map<Class<? extends MaidComponent>, Boolean> visited,
                              Map<Class<? extends MaidComponent>, Boolean> visiting) {
        Class<? extends MaidComponent> type = component.getClass();
        if (Boolean.TRUE.equals(visited.get(type))) {
            return;
        }
        if (Boolean.TRUE.equals(visiting.get(type))) {
            throw new IllegalStateException("Circular component dependency: " + type.getName());
        }
        visiting.put(type, true);
        for (Class<? extends MaidComponent> dependency : component.dependsOn()) {
            MaidComponent dependencyComponent = byConcrete.get(dependency);
            if (dependencyComponent != null) {
                visit(dependencyComponent, byConcrete, sorted, visited, visiting);
            }
        }
        visiting.put(type, false);
        visited.put(type, true);
        sorted.add(component);
    }

    public static LifecycleLists classifyLifecycleComponents(List<MaidComponent> ordered) {
        List<BaseTickComponent> baseTickComponents = ordered.stream()
                .filter(BaseTickComponent.class::isInstance)
                .map(BaseTickComponent.class::cast)
                .toList();
        List<AiStepComponent> aiStepComponents = ordered.stream()
                .filter(AiStepComponent.class::isInstance)
                .map(AiStepComponent.class::cast)
                .toList();
        List<SaveComponent> saveComponents = ordered.stream()
                .filter(SaveComponent.class::isInstance)
                .map(SaveComponent.class::cast)
                .toList();
        return new LifecycleLists(baseTickComponents, aiStepComponents, saveComponents);
    }
}
