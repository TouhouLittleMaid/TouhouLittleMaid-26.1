package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.tree;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.Bone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

public class RawBoneGroup {
    public ReferenceArrayList<RawBoneGroup> children = new ReferenceArrayList<>();
    public RawBoneGroup parent;

    public Bone bone;
    public int traverseOrder;
    public int depth;
    public int subTreeSize;
    public GeoLocatorType locatorType;

    public RawBoneGroup(Bone bone) {
        this.bone = bone;
    }
}