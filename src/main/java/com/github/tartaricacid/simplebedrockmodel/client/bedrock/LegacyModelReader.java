package com.github.tartaricacid.simplebedrockmodel.client.bedrock;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockCubeBox;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BonesItem;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.CubesItem;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;

import static com.github.tartaricacid.simplebedrockmodel.client.bedrock.BedrockModelUtil.*;


public final class LegacyModelReader {
    public static Pair<HashMap<String, BedrockPart>, AABB> load(BedrockModelPOJO pojo, BedrockPart root) {
        HashMap<String, BedrockPart> modelMap = new HashMap<>();
        HashMap<String, BonesItem> indexBones = new HashMap<>();

        assert pojo.getGeometryModelLegacy() != null;
        pojo.getGeometryModelLegacy().deco();

        // 材质的长度、宽度
        int texWidth = pojo.getGeometryModelLegacy().getTextureWidth();
        int texHeight = pojo.getGeometryModelLegacy().getTextureHeight();

        float[] offset = pojo.getGeometryModelLegacy().getVisibleBoundsOffset();
        float offsetX = offset[0];
        float offsetY = offset[1];
        float offsetZ = offset[2];
        float width = pojo.getGeometryModelLegacy().getVisibleBoundsWidth() / 2.0f;
        float height = pojo.getGeometryModelLegacy().getVisibleBoundsHeight() / 2.0f;

        AABB renderBoundingBox = new AABB(
                offsetX - width, offsetY - height, offsetZ - width,
                offsetX + width, offsetY + height, offsetZ + width
        );

        // 往 indexBones 里面注入数据，为后续坐标转换做参考
        for (BonesItem bones : pojo.getGeometryModelLegacy().getBones()) {
            // 塞索引，这是给后面坐标转换用的
            indexBones.put(bones.getName(), bones);
            // 塞入新建的空 ModelRenderer 实例
            // 因为后面添加 parent 需要，所以先塞空对象，然后二次遍历再进行数据存储
            modelMap.put(bones.getName(), new BedrockPart());
        }

        // 开始往 ModelRenderer 实例里面塞数据
        for (BonesItem bones : pojo.getGeometryModelLegacy().getBones()) {
            // 骨骼名称，注意因为后面动画的需要，头部、手部、腿部等骨骼命名必须是固定死的
            String name = bones.getName();
            // 旋转点，可能为空
            @Nullable float[] rotation = bones.getRotation();
            // 父骨骼的名称，可能为空
            @Nullable String parent = bones.getParent();
            // 塞进 HashMap 里面的模型对象
            BedrockPart model = modelMap.get(name);

            // 镜像参数
            model.mirror = bones.isMirror();

            // 旋转点
            model.setPos(
                    convertPivot(indexBones, bones, 0),
                    convertPivot(indexBones, bones, 1),
                    convertPivot(indexBones, bones, 2)
            );

            // Nullable 检查，设置旋转角度
            if (rotation != null) {
                setRotationAngle(model,
                        convertRotation(rotation[0]),
                        convertRotation(rotation[1]),
                        convertRotation(rotation[2])
                );
            }

            // 保存自己的初始位置和旋转角度，动画需要
            model.setInitialPose(PartPose.offsetAndRotation(
                    model.x, model.y, model.z,
                    model.xRot, model.yRot, model.zRot
            ));

            // Null 检查，进行父骨骼绑定
            if (parent != null) {
                modelMap.get(parent).addChild(name, model);
            } else {
                // 没有父骨骼的模型才进行渲染
                root.addChild(name, model);
            }

            // 我的天，Cubes 还能为空……
            if (bones.getCubes() == null) {
                continue;
            }

            // 塞入 Cube List
            for (CubesItem cube : bones.getCubes()) {
                float[] uv = cube.getUv();
                float[] size = cube.getSize();
                boolean mirror = cube.isMirror();
                float inflate = cube.getInflate();

                model.addCube(new BedrockCubeBox(uv[0], uv[1],
                        convertOrigin(bones, cube, 0),
                        convertOrigin(bones, cube, 1),
                        convertOrigin(bones, cube, 2),
                        size[0], size[1], size[2], inflate, mirror,
                        texWidth, texHeight)
                );
            }
        }

        return Pair.of(modelMap, renderBoundingBox);
    }
}
