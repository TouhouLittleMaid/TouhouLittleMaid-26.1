package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoBone;
import org.joml.Vector3f;

/**
 * 同一个 AnimationProcessor 内每个 bone 的 BoneTopLevelSnapshot 是唯一的
 */
public class BoneTopLevelSnapshot extends BoneSnapshot {
    public final AnimatedGeoBone bone;

    // 历史遗留问题，CodedAnimationController 的并行动画控制器需要缓存旋转参数
    @Deprecated
    public final Vector3f cachedPointData = new Vector3f();

    public boolean hasAnimation = false;
    public boolean isCurrentlyRunningRotationAnimation = true;
    public boolean isCurrentlyRunningPositionAnimation = true;
    public boolean isCurrentlyRunningScaleAnimation = true;

    public float lastRotationUpdateTime;
    public float lastPositionUpdateTime;
    public float lastScaleUpdateTime;

    public Vector3f rotationOffset;
    public Vector3f positionOffset;
    public Vector3f scaleOffset;

    public BoneTopLevelSnapshot(AnimatedGeoBone bone) {
        super(bone);
        this.bone = bone;
    }

    public void commit() {
        bone.setHidden(cubesHidden, childrenHidden);

        var initRot = bone.getInitialRotation();
        rotation.add(initRot, bone.getRotation());
        bone.setPosition(position);
        bone.setScale(scale);

        cachedPointData.set(0, 0, 0);
    }
}
