package com.github.tartaricacid.simplebedrockmodel.client.bedrock;


import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockCube;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockCubeBox;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockCubePerFace;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BonesItem;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.CubesItem;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.FaceItem;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.FaceUVsItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

public final class BedrockModelUtil {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CubesItem.class, new CubesItem.Deserializer())
            .create();

    public static FaceUVsItem singleSouthFace() {
        return new FaceUVsItem(emptyFace(), emptyFace(), emptyFace(), single16xFace(), emptyFace(), emptyFace());
    }

    public static FaceItem single16xFace() {
        return new FaceItem(new float[]{0, 0}, new float[]{16, 16});
    }

    public static FaceItem emptyFace() {
        return new FaceItem(new float[]{0, 0}, new float[]{0, 0});
    }

    /**
     * 基岩版的旋转中心计算方式和 Java 版不太一样，需要进行转换
     * <p>
     * 如果有父模型
     * <li>x，z 方向：本模型坐标 - 父模型坐标
     * <li>y 方向：父模型坐标 - 本模型坐标
     * <p>
     * 如果没有父模型
     * <li>x，z 方向不变
     * <li>y 方向：24 - 本模型坐标
     *
     * @param index 是 xyz 的哪一个，x 是 0，y 是 1，z 是 2
     */
    public static float convertPivot(HashMap<String, BonesItem> indexBones, BonesItem bones, int index) {
        if (bones.getParent() != null) {
            if (index == 1) {
                return indexBones.get(bones.getParent()).getPivot()[index] - bones.getPivot()[index];
            } else {
                return bones.getPivot()[index] - indexBones.get(bones.getParent()).getPivot()[index];
            }
        } else {
            if (index == 1) {
                return 24 - bones.getPivot()[index];
            } else {
                return bones.getPivot()[index];
            }
        }
    }

    public static float convertPivot(BonesItem parent, CubesItem cube, int index) {
        assert cube.getPivot() != null;
        if (index == 1) {
            return parent.getPivot()[index] - cube.getPivot()[index];
        } else {
            return cube.getPivot()[index] - parent.getPivot()[index];
        }
    }

    /**
     * 基岩版和 Java 版本的方块起始坐标也不一致，Java 是相对坐标，而且 y 值方向不一致。
     * 基岩版是绝对坐标，而且 y 方向朝上。
     * 其实两者规律很简单，但是我找了一下午，才明白咋回事。
     * <li>如果是 x，z 轴，那么只需要方块起始坐标减去旋转点坐标
     * <li>如果是 y 轴，旋转点坐标减去方块起始坐标，再减去方块的 y 长度
     *
     * @param index 是 xyz 的哪一个，x 是 0，y 是 1，z 是 2
     */
    public static float convertOrigin(BonesItem bone, CubesItem cube, int index) {
        if (index == 1) {
            return bone.getPivot()[index] - cube.getOrigin()[index] - cube.getSize()[index];
        } else {
            return cube.getOrigin()[index] - bone.getPivot()[index];
        }
    }

    public static float convertOrigin(CubesItem cube, int index) {
        assert cube.getPivot() != null;
        if (index == 1) {
            return cube.getPivot()[index] - cube.getOrigin()[index] - cube.getSize()[index];
        } else {
            return cube.getOrigin()[index] - cube.getPivot()[index];
        }
    }

    /**
     * 基岩版用的是度，Java 版用的是弧度，这个转换很简单
     */
    public static float convertRotation(float degree) {
        return (float) (degree * Math.PI / 180);
    }

    public static void setRotationAngle(BedrockPart part, float x, float y, float z) {
        part.xRot = x;
        part.yRot = y;
        part.zRot = z;
    }

    public static BedrockCube createCubeBox(float texOffX, float texOffY, float x, float y, float z, float width, float height, float depth,
                                            float delta, boolean mirror, float texWidth, float texHeight) {
        return new BedrockCubeBox(texOffX, texOffY, x, y, z, width, height, depth, delta, mirror, texWidth, texHeight);
    }

    public static BedrockCube createCubePerFace(float x, float y, float z, float width, float height, float depth, float delta,
                                                float texWidth, float texHeight, FaceUVsItem faces) {
        return new BedrockCubePerFace(x, y, z, width, height, depth, delta, texWidth, texHeight, faces);
    }
}
