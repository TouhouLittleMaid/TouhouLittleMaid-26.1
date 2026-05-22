package com.github.tartaricacid.touhoulittlemaid.geckolib3.resource;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.data.SoundData;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;

import java.util.List;

public record GeckoAsset(Object2ReferenceMap<String, SoundData> sounds,
                         Object2ReferenceMap<String, IValue> userFunctions,
                         Object2ReferenceMap<String, List<IValue>> eventHandlers) {
}
