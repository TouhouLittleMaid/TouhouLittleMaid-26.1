package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.util.StringPool;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class GeoBone {
    private static final String GLOWING_PREFIX = "ysmGlow";

    private final String name;
    private final int pooledName;

    private final Vector3f pivot;
    private final Vector3f initialRotation;
    private final GeoMesh cubes;

    private final int traverseOrder;
    private final int depth;
    private final int subTreeSize;
    private final GeoLocatorType locatorType;

    private final boolean glow;

    public GeoBone(String name, Vector3f pivot, Vector3f initialRotation, GeoMesh mesh,
                   int traverseOrder, int depth, int subTreeSize, @Nullable GeoLocatorType locatorType) {
        this.name = name;
        this.pooledName = StringPool.computeIfAbsent(name);

        this.pivot = pivot;
        this.initialRotation = initialRotation;
        this.cubes = mesh;

        this.traverseOrder = traverseOrder;
        this.depth = depth;
        this.subTreeSize = subTreeSize;
        this.locatorType = locatorType;

        this.glow = name.startsWith(GLOWING_PREFIX);
    }

    public GeoMesh cubes() {
        return cubes;
    }

    public String name() {
        return name;
    }

    public int pooledName() {
        return this.pooledName;
    }

    public Vector3f pivot() {
        return pivot;
    }

    public Vector3f initialRotation() {
        return initialRotation;
    }

    public boolean glow() {
        return glow;
    }

    public int traverseOrder() {
        return traverseOrder;
    }

    public int depth() {
        return depth;
    }

    public int subTreeSize() {
        return subTreeSize;
    }

    @Nullable
    public GeoLocatorType locatorType() {
        return locatorType;
    }
}
