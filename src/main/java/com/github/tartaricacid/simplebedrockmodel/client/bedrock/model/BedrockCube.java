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
     * Compiles the cube's vertices and adds them to the provided vertex consumer
     * <p>
     * 编译 Cube 的顶点并将它们添加到提供的 VertexConsumer 中
     *
     * @param pose     the current pose of the rendering context
     * @param consumer the vertex consumer to which the compiled vertices are added
     * @param lightmap the lightmap coordinates (usually 0xF000F0 for full brightness)
     * @param overlay  the overlay color (usually 0x00F000F0 for no overlay)
     * @param color    the vertex color (usually 0xFFFFFFFF for white)
     */
    void compile(PoseStack.Pose pose, Vector3f[] normals, VertexConsumer consumer, int lightmap, int overlay, int color);
}
