package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.tree;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.ModelProperties;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.RawGeoModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

public record RawGeometryTree(ReferenceArrayList<RawBoneGroup> flatBoneList,
                              ReferenceArrayList<RawBoneGroup> topLevelBones,
                              Int2ReferenceOpenHashMap<RawBoneGroup> boneMap,
                              ReferenceArrayList<ReferenceArrayList<RawBoneGroup>> locatorMap,
                              ModelProperties properties,
                              int height) {
    public static RawGeometryTree build(RawGeoModel model) {
        var flatBoneList = new ReferenceArrayList<RawBoneGroup>();
        var topLevelBones = new ReferenceArrayList<RawBoneGroup>();
        var boneMap = new Int2ReferenceOpenHashMap<RawBoneGroup>();
        var locatorMap = new ReferenceArrayList<ReferenceArrayList<RawBoneGroup>>(GeoLocatorType.size());
        var nodeQueue = new ReferenceArrayList<RawBoneGroup>();
        var geo = model.getMinecraftGeometry()[0];
        var treeHeight = 0;

        for (var i = 0; i < GeoLocatorType.size(); i++) {
            locatorMap.add(new ReferenceArrayList<>(1));
        }

        for (var bone : geo.getBones()) {
            var node = new RawBoneGroup(bone);
            boneMap.put(node.pooledName, node);
        }
        for (var boneNode : boneMap.values()) {
            var parentName = boneNode.pooledParentName;
            if (parentName == 0) {
                topLevelBones.add(boneNode);
                continue;
            }
            var parentNode = boneMap.get(parentName);
            if (parentNode == null) {
                throw new RuntimeException("Invalid geo model");
            }

            parentNode.children.add(boneNode);
            boneNode.parent = parentNode;
        }

        flatBoneList.ensureCapacity(boneMap.size());
        nodeQueue.ensureCapacity(16);
        nodeQueue.addAll(topLevelBones);
        while (!nodeQueue.isEmpty()) {
            var node = nodeQueue.pop();
            if (!node.children.isEmpty()) {
                node.subTreeSize = node.children.size();
                var parent = node.parent;
                while (parent != null) {
                    parent.subTreeSize += node.children.size();
                    parent = parent.parent;
                }

                var childDepth = node.depth + 1;
                if (treeHeight < childDepth) {
                    treeHeight = childDepth;
                }

                for (var child : node.children) {
                    child.depth = childDepth;
                    nodeQueue.add(child);
                }
            }
            node.traverseOrder = flatBoneList.size();
            flatBoneList.add(node);

            node.locatorType = GeoLocatorType.getByName(stripNumSuffix(node.bone.getName()));
            if (node.locatorType != null) {
                locatorMap.get(node.locatorType.getSeq()).add(node);
            }
        }

        return new RawGeometryTree(flatBoneList, topLevelBones, boneMap, locatorMap, geo.getProperties(), treeHeight);
    }

    private static String stripNumSuffix(String input) {
        if (!Character.isDigit(input.charAt(input.length() - 1))) {
            return input;
        }
        for (int i = input.length() - 2; i >= 0; i--) {
            if (!Character.isDigit(input.charAt(i))) {
                return input.substring(0, i + 1);
            }
        }
        return input;
    }
}
