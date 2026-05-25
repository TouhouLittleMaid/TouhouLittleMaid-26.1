package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoModel;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.NotNull;

public class AnimatedGeoModel {
    private final GeoModel geoModel;
    private final ReferenceArrayList<AnimatedGeoBone> flatBoneList;
    private final Int2ReferenceOpenHashMap<AnimatedGeoBone> boneMap;
    private final ReferenceArrayList<ReferenceArrayList<AnimatedGeoBone>> locatorMap;

    public AnimatedGeoModel(GeoModel model) {
        this.geoModel = model;
        this.flatBoneList = ReferenceArrayList.wrap(model.flatBoneList().stream()
                .map(AnimatedGeoBone::new)
                .toArray(AnimatedGeoBone[]::new));
        this.boneMap = new Int2ReferenceOpenHashMap<>();
        for (var bone : this.flatBoneList) {
            boneMap.put(bone.getPooledName(), bone);
        }
        this.locatorMap = new ReferenceArrayList<>(model.locatorMap().size());
        for (var rawGroup : model.locatorMap()) {
            var group = new ReferenceArrayList<AnimatedGeoBone>(rawGroup.size());
            for (var rawBone : rawGroup) {
                group.add(this.flatBoneList.get(rawBone.traverseOrder()));
            }
            this.locatorMap.add(group);
        }
    }

    public GeoModel geoModel() {
        return geoModel;
    }

    public ReferenceArrayList<AnimatedGeoBone> flatBoneList() {
        return flatBoneList;
    }

    public Int2ReferenceOpenHashMap<AnimatedGeoBone> boneMap() {
        return boneMap;
    }

    @NotNull
    public ReferenceArrayList<AnimatedGeoBone> locatorGroup(GeoLocatorType type) {
        return locatorMap.get(type.getSeq());
    }
}
