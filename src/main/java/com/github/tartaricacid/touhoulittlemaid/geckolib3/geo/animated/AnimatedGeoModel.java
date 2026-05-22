package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.util.StringPool;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoModel;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.Nullable;

public class AnimatedGeoModel {
    private static final int HEAD_NAME = StringPool.computeIfAbsent("Head");

    private final GeoModel geoModel;
    private final ReferenceArrayList<AnimatedGeoBone> flatBoneList;
    private final Int2ReferenceOpenHashMap<AnimatedGeoBone> boneMap;

    @Nullable
    private final AnimatedGeoBone head;

    public AnimatedGeoModel(GeoModel model) {
        this.geoModel = model;
        this.flatBoneList = ReferenceArrayList.wrap(model.flatBoneList().stream()
                .map(AnimatedGeoBone::new)
                .toArray(AnimatedGeoBone[]::new));
        this.boneMap = new Int2ReferenceOpenHashMap<>();
        for (var bone : this.flatBoneList) {
            boneMap.put(bone.getPooledName(), bone);
        }
        this.head = this.boneMap.get(HEAD_NAME);
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

    @Nullable
    public AnimatedGeoBone head() {
        return head;
    }
}
