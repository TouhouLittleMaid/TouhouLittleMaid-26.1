package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor;

import org.joml.Vector3f;

public interface IBoneView {
    String getName();
    int getPooledName();

    Vector3f getPivot();
    Vector3f getPosition();
    Vector3f getRotation();
    Vector3f getScale();

    boolean areCubesHidden();
    boolean areChildrenHidden();

    void setTracking(boolean value);
    Vector3f getGlobalPivot();
}
