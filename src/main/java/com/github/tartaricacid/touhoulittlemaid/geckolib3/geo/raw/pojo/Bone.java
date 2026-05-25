package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Debug;

@Debug.Renderer(text = "name + \" <- \" + parent")
public class Bone {
    @SerializedName("cubes")
    private Cube[] cubes;
    @SerializedName("debug")
    private Boolean debug;
    @SerializedName("inflate")
    private Float inflate;
    @SerializedName("mirror")
    private Boolean mirror;
    @SerializedName("name")
    private String name;
    @SerializedName("parent")
    private String parent;
    @SerializedName("pivot")
    private float[] pivot = new float[]{0, 0, 0};
    @SerializedName("reset")
    private Boolean reset;
    @SerializedName("rotation")
    private float[] rotation = new float[]{0, 0, 0};

/*
    @SerializedName("bind_pose_rotation")
    private float[] bindPoseRotation;
    @SerializedName("locators")
    private Map<String, LocatorValue> locators;
    @SerializedName("neverRender")
    private Boolean neverRender;
    @SerializedName("poly_mesh")
    private PolyMesh polyMesh;
    @SerializedName("render_group_id")
    private Long renderGroupID;
    @SerializedName("texture_meshes")
    private TextureMesh[] textureMeshes;
*/

    public Cube[] getCubes() {
        return cubes;
    }

    public void setCubes(Cube[] value) {
        this.cubes = value;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean value) {
        this.debug = value;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String value) {
        this.parent = value;
    }

    public float[] getPivot() {
        return pivot;
    }

    public void setPivot(float[] value) {
        this.pivot = value;
    }

    public Boolean getReset() {
        return reset;
    }

    public void setReset(Boolean value) {
        this.reset = value;
    }

    public float[] getRotation() {
        return rotation;
    }

    public void setRotation(float[] value) {
        this.rotation = value;
    }

/*
    public float[] getBindPoseRotation() {
        return bindPoseRotation;
    }

    public void setBindPoseRotation(float[] value) {
        this.bindPoseRotation = value;
    }

    public Map<String, LocatorValue> getLocators() {
        return locators;
    }

    public void setLocators(Map<String, LocatorValue> value) {
        this.locators = value;
    }

    public Boolean getNeverRender() {
        return neverRender;
    }

    public void setNeverRender(Boolean value) {
        this.neverRender = value;
    }

    public PolyMesh getPolyMesh() {
        return polyMesh;
    }

    public void setPolyMesh(PolyMesh value) {
        this.polyMesh = value;
    }

    public Long getRenderGroupID() {
        return renderGroupID;
    }

    public void setRenderGroupID(Long value) {
        this.renderGroupID = value;
    }

    public TextureMesh[] getTextureMeshes() {
        return textureMeshes;
    }

    public void setTextureMeshes(TextureMesh[] value) {
        this.textureMeshes = value;
    }
*/
}
