package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor.IBoneView;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoBone;
import org.joml.Vector3f;

public class AnimatedGeoBone implements IBoneView {
    private final GeoBone geoBone;

    private boolean cubesHidden = false;
    private boolean childrenHidden = false;
    private boolean tracking = false;

    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f position = new Vector3f();
    private final Vector3f rotation = new Vector3f();
    private final Vector3f globalPivot = new Vector3f();

    public AnimatedGeoBone(GeoBone geoBone) {
        this.geoBone = geoBone;
        this.setRotation(geoBone.initialRotation());
    }

    public GeoBone geoBone() {
        return geoBone;
    }

    @Override
    public String getName() {
        return geoBone.name();
    }

    @Override
    public int getPooledName() {
        return geoBone.pooledName();
    }

    @Override
    public Vector3f getPivot() {
        return geoBone.pivot();
    }

    public Vector3f getInitialRotation() {
        return geoBone.initialRotation();
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f value) {
        rotation.set(value);
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f value) {
        position.set(value);
    }

    @Override
    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f value) {
        scale.set(value);
    }

    @Override
    public boolean areCubesHidden() {
        return cubesHidden;
    }

    public void setHidden(boolean hidden) {
        setHidden(hidden, hidden);
    }

    @Override
    public boolean areChildrenHidden() {
        return childrenHidden;
    }

    public void setHidden(boolean cubesHidden, boolean childrenHidden) {
        this.cubesHidden = cubesHidden;
        this.childrenHidden = childrenHidden;
    }

    public boolean isTracking() {
        return tracking;
    }

    @Override
    public void setTracking(boolean value) {
        tracking = value;
    }

    @Override
    public Vector3f getGlobalPivot() {
        return globalPivot;
    }

    public void setGlobalPivot(Vector3f value) {
        globalPivot.set(value);
    }
}
