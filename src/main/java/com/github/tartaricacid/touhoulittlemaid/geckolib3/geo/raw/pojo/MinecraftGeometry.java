package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

public class MinecraftGeometry {
    @SerializedName("bones")
    private ReferenceArrayList<Bone> bones;
    @SerializedName("cape")
    private String cape;
    @SerializedName("description")
    private ModelProperties modelProperties;

    public ReferenceArrayList<Bone> getBones() {
        return bones;
    }

    public void setBones(ReferenceArrayList<Bone> value) {
        this.bones = value;
    }

    public String getCape() {
        return cape;
    }

    public void setCape(String value) {
        this.cape = value;
    }

    public ModelProperties getProperties() {
        return modelProperties;
    }

    public void setProperties(ModelProperties value) {
        this.modelProperties = value;
    }
}
