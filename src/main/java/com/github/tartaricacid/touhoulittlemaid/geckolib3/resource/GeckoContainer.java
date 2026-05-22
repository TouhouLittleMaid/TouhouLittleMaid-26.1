package com.github.tartaricacid.touhoulittlemaid.geckolib3.resource;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.controller.AnimationControllerData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.file.AnimationFile;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoModel;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public record GeckoContainer(
        GeoModel model,
        AnimationFile animation,
        Consumer<?> controllerFactory,
        Object2ReferenceMap<String, AnimationControllerData> animControllers,
        ConditionManager conditionManager,
        Identifier texture,
        GeckoAsset asset,
        Type type) {
    public enum Type {
        MAID,
        CHAIR,
    }
}
