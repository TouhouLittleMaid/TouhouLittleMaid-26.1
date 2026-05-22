package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition.ConditionArmor;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.Animation;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.controller.AnimationControllerData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;

import java.util.List;

public record ControllerResource(
        Object2ReferenceMap<String, Animation> animations,
        ConditionArmor armorCondition,
        Object2ReferenceMap<String, AnimationControllerData> controllerData,
        Object2ReferenceMap<String, List<IValue>> eventHandlers) {
}
