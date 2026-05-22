package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.extended.Matrix4fAccessor;
import com.mojang.blaze3d.vertex.PoseStack;

public final class GeoModelStateExtractor {
    static final int STRIDE = Matrix4fAccessor.STRIDE;
    private static final ThreadLocal<PoseStack> POSE_STACK = ThreadLocal.withInitial(PoseStack::new);

    public static void extract(AnimatedGeoModel model, GeoModelState state) {
        state.init(model.geoModel());

        // 低版本移植注意：此处若有性能问题，需要额外实现一套基于数组+索引的 stack。可参考 26.1 实现
        var poseStack = getPoseStack();
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
            if (((scale.x == 0 ? 1 : 0) + (scale.y == 0 ? 1 : 0) + (scale.z == 0 ? 1 : 0)) <= 1) {
                var renderCubes = !boneState.areCubesHidden() && bone.cubes().getCubeCount() > 0;
                var renderLocator = !boneState.areCubesHidden() && bone.locatorType() != null;
                var renderChildren = !boneState.areChildrenHidden() && bone.subTreeSize() > 0;

                if (renderCubes || renderLocator || renderChildren) {
                    extractRenderBone(poseStack, boneState, state);
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
    }

    private static void extractRenderBone(PoseStack poseStack, AnimatedGeoBone boneState, GeoModelState state) {
        var offset = boneState.geoBone().traverseOrder() * STRIDE;
        ((Matrix4fAccessor) poseStack.last().pose()).tlm$extractTransform(state.data, offset);
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
