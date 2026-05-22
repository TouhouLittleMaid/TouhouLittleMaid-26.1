package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.collection;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.AnimationManager;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerCollection;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerResource;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.discovery.ArmorControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.discovery.MultiControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.discovery.ParallelControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.discovery.SingleControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.HybridAnimationController;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;
import net.minecraft.world.entity.EquipmentSlot;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class MaidControllerCollection {
    private static final ControllerCollection<GeckoMaidEntity<?>> COLLECTION = new ControllerCollection<>();
    private static final String CATEGORY = "maid";

    @SuppressWarnings("rawtypes,unchecked,deprecation")
    private static void init() {
        parallel("pre_parallel", (name, entity, anim) ->
                new HybridAnimationController(entity, name, 0, anim != null ? (e -> AnimationManager.predicateParallel(e, anim)) : AnimationManager.empty()));

        multi("pre_main", (name, entity) -> new HybridAnimationController(entity, name, 0, AnimationManager.empty()));
        simple("main", (name, entity) -> new HybridAnimationController(entity, name, 0.1f, AnimationManager::predicateMain));
        multi("post_main", (name, entity) -> new HybridAnimationController(entity, name, 0, AnimationManager.empty()));

        multi("pre_hold", (name, entity) -> new HybridAnimationController(entity, name, 0, AnimationManager.empty()));
        simple("hold_offhand", (name, entity) -> new HybridAnimationController(entity, name, 0.1f, AnimationManager::predicateOffhandHold));
        simple("hold_mainhand", (name, entity) -> new HybridAnimationController(entity, name, 0.1f, AnimationManager::predicateMainhandHold));
        multi("post_hold", (name, entity) -> new HybridAnimationController(entity, name, 0, AnimationManager.empty()));

        multi("pre_swing", (name, entity) -> new HybridAnimationController(entity, name, 0, AnimationManager.empty()));
        simple("swing", (name, entity) -> new HybridAnimationController(entity, name, 0, AnimationManager::predicateSwing));
        multi("post_swing", (name, entity) -> new HybridAnimationController(entity, name, 0, AnimationManager.empty()));

        multi("pre_use", (name, entity) -> new HybridAnimationController(entity, name, 0, AnimationManager.empty()));
        simple("use", (name, entity) -> new HybridAnimationController(entity, name, 0.1f, AnimationManager::predicateUse));
        multi("post_use", (name, entity) -> new HybridAnimationController(entity, name, 0, AnimationManager.empty()));

        simple("passenger", (name, entity) -> new HybridAnimationController(entity, name, 0.1f, AnimationManager::predicatePassengerAnimation));

        parallel("parallel", (name, entity, anim) ->
                new HybridAnimationController(entity, name, 0, anim != null ? e -> AnimationManager.predicateParallel(e, anim) : AnimationManager.empty(), true));

        armor("armor", (name, entity, slot) -> new HybridAnimationController(entity, name, 0, e -> AnimationManager.predicateArmor(e, slot)));
    }

    public static Consumer<GeckoMaidEntity<?>> build(ControllerResource resource) {
        if (COLLECTION.isEmpty()) {
            init();
        }
        return COLLECTION.build(resource);
    }

    private static void simple(String name, BiFunction<String, GeckoMaidEntity<?>, IAnimationController<GeckoMaidEntity<?>>> simpleFactory) {
        simple(name, false, simpleFactory);
    }

    private static void simple(String name, boolean guiOnly, BiFunction<String, GeckoMaidEntity<?>, IAnimationController<GeckoMaidEntity<?>>> simpleFactory) {
        var controllerName = String.format("%s.%s", CATEGORY, name);
        ControllerDiscovery<GeckoMaidEntity<?>> discovery =model -> (animatable, consumer) -> {
            consumer.accept(simpleFactory.apply(controllerName, animatable));
        };
        if (guiOnly) {
            discovery = discovery.withCondition(AnimatableEntity::isPreviewEntity);
        }
        COLLECTION.add(discovery);
    }

    private static void multi(String regex, BiFunction<String, GeckoMaidEntity<?>, IAnimationController<GeckoMaidEntity<?>>> controllerFunc) {
        COLLECTION.add(new MultiControllerDiscovery<>(CATEGORY, regex, controllerFunc));
    }

    private static void single(String name, String[] animations, boolean hybrid, BiFunction<String, GeckoMaidEntity<?>, IAnimationController<GeckoMaidEntity<?>>> controllerFunc) {
        COLLECTION.add(new SingleControllerDiscovery<>(CATEGORY, name, animations, hybrid, controllerFunc));
    }

    private static void parallel(String name, TriFunction<String, GeckoMaidEntity<?>, String, IAnimationController<GeckoMaidEntity<?>>> controllerFunc) {
        COLLECTION.add(new ParallelControllerDiscovery<>(CATEGORY, name, true, controllerFunc));
    }

    private static void armor(String name, TriFunction<String, GeckoMaidEntity<?>, EquipmentSlot, IAnimationController<GeckoMaidEntity<?>>> controllerFunc) {
        COLLECTION.add(new ArmorControllerDiscovery<>(CATEGORY, name, controllerFunc));
    }
}
