package com.github.tartaricacid.touhoulittlemaid.geckolib3.resource;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.file.AnimationFile;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoModel;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public record GeckoContainer(GeoModel model, AnimationFile animation, Consumer<?> controllerFactory, ConditionManager conditionManager, Identifier texture, Type type) {
    public enum Type {
        MAID,
        CHAIR,
    }
}
