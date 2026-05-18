package com.github.tartaricacid.touhoulittlemaid.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public final class ShapeDraw {
    public static void drawSector(int x, int y, int r, double startAngle, double endAngle, int precision, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        RenderType renderType = RenderTypes.debugTriangleFan();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(renderType.mode(), renderType.format());
        bufferbuilder.addVertex(x, y, 0).setColor(red, green, blue, alpha);
        double precisionAngle = 2 * Math.PI / precision;
        for (int i = (int) (endAngle / precisionAngle); i >= (int) (startAngle / precisionAngle); i--) {
            bufferbuilder.addVertex((float) (x + r * Math.cos(i * precisionAngle)),
                    (float) (y + r * Math.sin(i * precisionAngle)),
                    0).setColor(red, green, blue, alpha);
        }
        try (MeshData meshData = bufferbuilder.buildOrThrow()) {
            renderType.draw(meshData);
        }
    }

    public static void drawCircle(int x, int y, int r, int precision, int color) {
        drawSector(x, y, r, 0, 2 * Math.PI, precision, color);
    }
}
