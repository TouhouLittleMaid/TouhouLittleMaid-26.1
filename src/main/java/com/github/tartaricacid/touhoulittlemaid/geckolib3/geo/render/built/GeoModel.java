package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.ModelProperties;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.jetbrains.annotations.NotNull;

public record GeoModel(Int2ReferenceOpenHashMap<GeoBone> boneMaps,
                       ReferenceArrayList<GeoBone> flatBoneList,
                       ReferenceArrayList<ReferenceArrayList<GeoBone>> locatorMap,
                       ModelProperties properties) {
    @NotNull
    public ReferenceArrayList<GeoBone> locatorGroup(GeoLocatorType type) {
        return locatorMap.get(type.getSeq());
    }
}
