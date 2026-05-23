package com.github.tartaricacid.simplebedrockmodel.client.bedrock.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

public interface BedrockCube {
    int NUM_CUBE_FACES = 6;
    int VERTEX_X1_Y1_Z1 = 0,
            VERTEX_X2_Y1_Z1 = 1,
            VERTEX_X2_Y2_Z1 = 2,
            VERTEX_X1_Y2_Z1 = 3,
            VERTEX_X1_Y1_Z2 = 4,
            VERTEX_X2_Y1_Z2 = 5,
            VERTEX_X2_Y2_Z2 = 6,
            VERTEX_X1_Y2_Z2 = 7;
    int[][] VERTEX_ORDER = new int[][]{
            {VERTEX_X2_Y1_Z2, VERTEX_X1_Y1_Z2, VERTEX_X1_Y1_Z1, VERTEX_X2_Y1_Z1},
            {VERTEX_X2_Y2_Z1, VERTEX_X1_Y2_Z1, VERTEX_X1_Y2_Z2, VERTEX_X2_Y2_Z2},
            {VERTEX_X2_Y1_Z1, VERTEX_X1_Y1_Z1, VERTEX_X1_Y2_Z1, VERTEX_X2_Y2_Z1},
            {VERTEX_X1_Y1_Z2, VERTEX_X2_Y1_Z2, VERTEX_X2_Y2_Z2, VERTEX_X1_Y2_Z2},
            {VERTEX_X1_Y1_Z1, VERTEX_X1_Y1_Z2, VERTEX_X1_Y2_Z2, VERTEX_X1_Y2_Z1},
            {VERTEX_X2_Y1_Z2, VERTEX_X2_Y1_Z1, VERTEX_X2_Y2_Z1, VERTEX_X2_Y2_Z2},
    };

    /**
     * 编译 Cube 的顶点并将它们添加到提供的 VertexConsumer 中
     *
     * @param pose     当前渲染上下文的 pose
     * @param consumer 接收编译后顶点数据的顶点消费者
     * @param lightmap 光照贴图坐标（0xF000F0 表示全亮度）
     * @param overlay  叠加层颜色（通常为 0x00F000F0 表示无叠加）
     * @param color    顶点颜色（通常为 0xFFFFFFFF 表示白色）
     */
    void compile(PoseStack.Pose pose, Vector3f[] normals, VertexConsumer consumer, int lightmap, int overlay, int color);
}
