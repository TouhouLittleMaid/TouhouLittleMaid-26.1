package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class GeoModelStateExtractor {
    static final int STRIDE = 16;
    private static final ThreadLocal<PoseStack> POSE_STACK = ThreadLocal.withInitial(PoseStack::new);

    public static void extract(AnimatedGeoModel model, GeoModelState state) {
        state.init(model.geoModel());

        // 低版本移植注意：此处若有性能问题，需要额外实现一套基于数组+索引的 stack。可参考 26.1 实现
        var poseStack = getPoseStack();
        var tempMat = new Matrix4f();
        var poseStackDepth = 0;
        for (var i = 0; i < model.flatBoneList().size(); i++) {
            var boneState = model.flatBoneList().get(i);
            var bone = boneState.geoBone();

            while (poseStackDepth > bone.depth()) {
                poseStack.popPose();
                --poseStackDepth;
            }
            poseStack.pushPose();
            ++poseStackDepth;

            // scale 任意两个分量为 0 的骨骼不渲染，包括定位组
            var scale = boneState.getScale();
            var scale0 = ((scale.x == 0 ? 1 : 0) + (scale.y == 0 ? 1 : 0) + (scale.z == 0 ? 1 : 0)) > 1;
            var renderCubes = !scale0 && !boneState.areCubesHidden() && bone.cubes().getCubeCount() > 0;
            var renderLocator = !scale0 && !boneState.areCubesHidden() && bone.locatorType() != null;
            var renderChildren = !scale0 && !boneState.areChildrenHidden() && bone.subTreeSize() > 0;

            if (renderCubes || renderLocator || renderChildren) {
                extractBone(poseStack.last().pose(), tempMat, boneState, state);
            }
            if (renderCubes) {
                state.renderBoneIndices.add((short) bone.traverseOrder());
            }
            if (renderLocator) {
                state.activeLocatorGroups.get(bone.locatorType().getSeq()).add((short) bone.traverseOrder());
            }
            if (!renderChildren) {
                i += bone.subTreeSize();
                poseStack.popPose();
                --poseStackDepth;
            }
        }
    }

    private static void extractBone(Matrix4f pose, Matrix4f temp, AnimatedGeoBone boneState, GeoModelState state) {
        var pos = boneState.getPosition();
        var pivot = boneState.getPivot();
        var scale = boneState.getScale();
        var rot = boneState.getRotation();

        var quat = new Quaternionf();
        if (rot.x != 0 || rot.y != 0 || rot.z != 0) {
            quat.rotateZYX(rot.z, rot.y, rot.x);
        }

        pose.mulAffine(temp.translationRotateScale(
                        (pivot.x - pos.x) / 16f, (pivot.y + pos.y) / 16f, (pivot.z + pos.z) / 16f,
                        quat.x, quat.y, quat.z, quat.w,
                        scale.x, scale.y, scale.z))
                .translate(-pivot.x / 16, -pivot.y / 16, -pivot.z / 16)
                .get(state.data, boneState.geoBone().traverseOrder() * STRIDE);
    }

    private static PoseStack getPoseStack() {
        var poseStack = POSE_STACK.get();
        poseStack.setIdentity();
        while (!poseStack.isEmpty()) {
            poseStack.popPose();
        }
        return poseStack;
    }
}
