package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.extended.Matrix4fAccessor;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoBone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import org.joml.Matrix4f;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class GeoModelState {
    private GeoModel model;
    float[] data;
    final ShortArrayList renderBoneIndices;
    final ReferenceArrayList<ShortArrayList> activeLocatorGroups;

    public GeoModelState() {
        this.renderBoneIndices = new ShortArrayList(32);
        this.activeLocatorGroups = new ReferenceArrayList<>(GeoLocatorType.size());
        for (var i = 0; i < GeoLocatorType.size(); i++) {
            this.activeLocatorGroups.add(new ShortArrayList(1));
        }
    }

    public void init(GeoModel model) {
        var boneSize = model.flatBoneList().size();
        this.model = model;
        var size = boneSize * GeoModelStateExtractor.STRIDE;
        if (data == null || data.length != size) {
            data = new float[size];
            renderBoneIndices.size(boneSize);
            renderBoneIndices.trim();
        }
        renderBoneIndices.clear();
        for (var locators : activeLocatorGroups) {
            locators.clear();
        }
    }

    private void visit(ShortArrayList boneIndices, BiConsumer<GeoBone, Matrix4f> visitor) {
        var bones = model.flatBoneList();
        var transform = new Matrix4f();
        for (var boneIndex : boneIndices) {
            var bone = bones.get(Short.toUnsignedInt(boneIndex));
            var offset = bone.traverseOrder() * GeoModelStateExtractor.STRIDE;
            ((Matrix4fAccessor) transform).tlm$readTransform(data, offset);
            visitor.accept(bone, transform);
        }
    }

    public void visitRenderBones(PoseStack.Pose poseState, BiConsumer<GeoBone, PoseStack.Pose> visitor) {
        if (!renderBoneIndices.isEmpty()) {
            var poseStateCache = new PoseStack.Pose();
            visit(renderBoneIndices, (bone, transform) -> {
                poseStateCache.set(poseState);
                poseStateCache.mulPose(transform);
                visitor.accept(bone, poseStateCache);
            });
        }
    }

    public void visitLocatorGroup(GeoLocatorType type, PoseStack poseStack, Consumer<PoseStack> visitor) {
        var group = activeLocatorGroups.get(type.getSeq());
        if (!group.isEmpty()) {
            visit(renderBoneIndices, (_, transform) -> {
                poseStack.pushPose();
                poseStack.mulPose(transform);
                visitor.accept(poseStack);
                poseStack.popPose();
            });
        }
    }
}
