package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo;

import com.google.gson.annotations.SerializedName;


public class FaceUv {
    @SerializedName("material_instance")
    private String materialInstance;
    @SerializedName("uv")
    private float[] uv;
    @SerializedName("uv_size")
    private float[] uvSize;

    public String getMaterialInstance() {
        return materialInstance;
    }

    public void setMaterialInstance(String value) {
        this.materialInstance = value;
    }

    public float[] getUv() {
        return uv;
    }

    public void setUv(float[] value) {
        this.uv = value;
    }

    public float[] getUvSize() {
        return uvSize;
    }

    public void setUvSize(float[] value) {
        this.uvSize = value;
    }
}
