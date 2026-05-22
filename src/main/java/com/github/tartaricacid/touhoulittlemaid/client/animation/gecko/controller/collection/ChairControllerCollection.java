package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.collection;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.AnimationManager;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerCollection;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerResource;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.discovery.ParallelControllerDiscovery;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoChairEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.HybridAnimationController;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.Consumer;

public class ChairControllerCollection {
    private static final ControllerCollection<GeckoChairEntity> COLLECTION = new ControllerCollection<>();
    private static final String CATEGORY = "chair";

    @SuppressWarnings("rawtypes,unchecked,deprecation")
    private static void init() {
        parallel("parallel", (name, entity, anim) ->
                new HybridAnimationController(entity, name, 0, anim != null ?
                        e -> AnimationManager.predicateParallel(e, anim) :
                        AnimationManager.empty(), true));
    }

    public static Consumer<GeckoChairEntity> build(ControllerResource resource) {
        if (COLLECTION.isEmpty()) {
            init();
        }
        return COLLECTION.build(resource);
    }

    private static void parallel(String name, TriFunction<String, GeckoChairEntity, String, IAnimationController<GeckoChairEntity>> controllerFunc) {
        COLLECTION.add(new ParallelControllerDiscovery<>(CATEGORY, name, true, controllerFunc));
    }
}
