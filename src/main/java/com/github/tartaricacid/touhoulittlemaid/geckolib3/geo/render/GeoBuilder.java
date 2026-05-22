package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.Bone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.Cube;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.ModelProperties;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.tree.RawBoneGroup;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.tree.RawGeometryTree;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoBone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoMesh;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.VectorUtils;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.joml.Vector3f;
import org.joml.Math;

public class GeoBuilder {
    public static GeoModel constructGeoModel(RawGeometryTree geometryTree) {
        var boneMaps = new Int2ReferenceOpenHashMap<GeoBone>(geometryTree.flatBoneList().size());
        var flatBoneList = new ReferenceArrayList<GeoBone>(geometryTree.flatBoneList().size());

        for (var rawBone : geometryTree.flatBoneList()) {
            var geoBone = constructBone(rawBone, geometryTree.properties());
            flatBoneList.add(geoBone);
            boneMaps.put(geoBone.pooledName(), geoBone);
        }

        return new GeoModel(boneMaps, flatBoneList, geometryTree.properties());
    }

    public static GeoBone constructBone(RawBoneGroup boneNode, ModelProperties properties) {
        Bone rawBone = boneNode.bone;
        Vector3f rotation = VectorUtils.fromArray(rawBone.getRotation());
        Vector3f pivot = VectorUtils.fromArray(rawBone.getPivot());
        rotation.mul(-1, -1, 1);

        Cube[] cubes = rawBone.getCubes();
        GeoMesh.Builder meshBuilder = new GeoMesh.Builder(cubes == null ? 0 : cubes.length);
        if (cubes != null) {
            // 使用 For i 循环访问数组效率更高
            for (int i = 0; i < cubes.length; i++) {
                meshBuilder.addCube(cubes[i], properties, rawBone.getInflate() == null ? 0 : rawBone.getInflate(), rawBone.getMirror());
            }
        }
        GeoMesh mesh = meshBuilder.build();

        return new GeoBone(rawBone.getName(),
                new Vector3f(-pivot.x, pivot.y, pivot.z),
                new Vector3f(Math.toRadians(rotation.x()), Math.toRadians(rotation.y()), Math.toRadians(rotation.z())),
                mesh,
                boneNode.traverseOrder, boneNode.depth, boneNode.subTreeSize,
                boneNode.locatorType);
    }
}
