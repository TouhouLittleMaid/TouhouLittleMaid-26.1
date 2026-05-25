package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.tree;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.util.StringPool;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.Bone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.Debug;


@Debug.Renderer(text = "bone.name + \" <- \" + bone.parent")
public class RawBoneGroup {
    public ReferenceArrayList<RawBoneGroup> children = new ReferenceArrayList<>();
    public RawBoneGroup parent;

    public final Bone bone;
    public final int pooledName;
    public final int pooledParentName;
    public int traverseOrder;
    public int depth;
    public int subTreeSize;
    public GeoLocatorType locatorType;

    public RawBoneGroup(Bone bone) {
        this.bone = bone;
        this.pooledName = StringPool.computeIfAbsent(bone.getName());
        this.pooledParentName = StringUtil.isNullOrEmpty(bone.getParent()) ? 0 : StringPool.computeIfAbsent(bone.getParent());
    }
}