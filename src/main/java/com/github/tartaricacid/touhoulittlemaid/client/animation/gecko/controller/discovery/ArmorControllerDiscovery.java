package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.discovery;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerFactory;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerResource;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;
import net.minecraft.world.entity.EquipmentSlot;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ArmorControllerDiscovery<T extends AnimatableEntity<?>> implements ControllerDiscovery<T> {
    private final String category;
    private final String name;
    private final TriFunction<String, T, EquipmentSlot, IAnimationController<T>> controllerFunc;

    public ArmorControllerDiscovery(String category, String name, TriFunction<String, T, EquipmentSlot, IAnimationController<T>> controllerFunc) {
        this.category = category;
        this.name = name;
        this.controllerFunc = controllerFunc;
    }

    @Override
    public ControllerFactory<T> process(ControllerResource resource) {
        List<Pair<String, EquipmentSlot>> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            var controllerName = String.format("%s.%s_%s", category, name, slot.getName());
            if (resource.controllerData().containsKey(controllerName)) {
                slots.add(Pair.of(controllerName, slot));
                continue;
            }
            if (resource.eventHandlers().containsKey(String.format("%s_ctrl_%s_%s", category, name, slot.getName()))) {
                slots.add(Pair.of(controllerName, slot));
                continue;
            }
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && (resource.armorCondition().hasTest(slot) || resource.animations().containsKey(slot.getName() + ":default"))) {
                slots.add(Pair.of(controllerName, slot));
                continue;
            }
        }
        return (animatable, consumer) -> {
            if (!animatable.isPreviewEntity()) {
                for (var slot : slots) {
                    consumer.accept(controllerFunc.apply(slot.getLeft(), animatable, slot.getRight()));
                }
            }
        };
    }
}
