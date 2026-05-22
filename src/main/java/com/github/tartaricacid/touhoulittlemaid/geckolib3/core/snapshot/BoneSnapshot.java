/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoBone;
import org.joml.Vector3f;

public class BoneSnapshot {
    public final int name;

    public final Vector3f position = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    public final Vector3f scale = new Vector3f(1, 1, 1);

    public boolean cubesHidden;
    public boolean childrenHidden;

    public BoneSnapshot(AnimatedGeoBone bone) {
        copyFrom(bone);
        this.name = bone.getPooledName();
    }

    public void copyFrom(AnimatedGeoBone bone) {
        var initRot = bone.getInitialRotation();

        position.set(bone.getPosition());
        bone.getRotation().sub(initRot, rotation);
        scale.set(bone.getScale());

        cubesHidden = bone.areCubesHidden();
        childrenHidden = bone.areChildrenHidden();
    }

    public void copyFrom(BoneSnapshot snapshot) {
        position.set(snapshot.position);
        rotation.set(snapshot.rotation);
        scale.set(snapshot.scale);

        cubesHidden = snapshot.cubesHidden;
        childrenHidden = snapshot.childrenHidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if(o instanceof BoneSnapshot) {
            BoneSnapshot that = (BoneSnapshot) o;
            return name == that.name;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name;
    }
}
