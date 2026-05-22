package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.ModelProperties;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

public record GeoModel(Int2ReferenceOpenHashMap<GeoBone> boneMaps,
                       ReferenceArrayList<GeoBone> flatBoneList,
                       ModelProperties properties) {
}
