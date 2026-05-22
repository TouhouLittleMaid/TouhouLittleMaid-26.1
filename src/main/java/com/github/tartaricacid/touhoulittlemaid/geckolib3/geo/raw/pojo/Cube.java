package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo;

import com.google.gson.annotations.SerializedName;

public class Cube {
    @SerializedName("inflate")
    private Float inflate;
    @SerializedName("mirror")
    private Boolean mirror;
    @SerializedName("origin")
    private float[] origin = new float[]{0, 0, 0};
    @SerializedName("pivot")
    private float[] pivot = new float[]{0, 0, 0};
    @SerializedName("rotation")
    private float[] rotation = new float[]{0, 0, 0};
    @SerializedName("size")
    private float[] size = new float[]{1, 1, 1};
    @SerializedName("uv")
    private UvUnion uv;

    public Float getInflate() {
        return inflate;
    }

    public void setInflate(Float value) {
        this.inflate = value;
    }

    public Boolean getMirror() {
        return mirror;
    }

    public void setMirror(Boolean value) {
        this.mirror = value;
    }

    public float[] getOrigin() {
        return origin;
    }

    public void setOrigin(float[] value) {
        this.origin = value;
    }

    public float[] getPivot() {
        return pivot;
    }

    public void setPivot(float[] value) {
        this.pivot = value;
    }

    public float[] getRotation() {
        return rotation;
    }

    public void setRotation(float[] value) {
        this.rotation = value;
    }

    public float[] getSize() {
        return size;
    }

    public void setSize(float[] value) {
        this.size = value;
    }

    public UvUnion getUv() {
        return uv;
    }

    public void setUv(UvUnion value) {
        this.uv = value;
    }
}
