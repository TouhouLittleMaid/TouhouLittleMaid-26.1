package com.github.tartaricacid.touhoulittlemaid.util;

import com.mojang.math.Divisor;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class GuiTools {
    public static final Button.OnPress NO_ACTION = (button) -> {
    };

    public static void blitNineSliced(GuiGraphicsExtractor graphics, Identifier atlasLocation, int x, int y, int width, int height, int sliceWidth, int sliceHeight, int uWidth, int vHeight, int textureX, int textureY) {
        blitNineSliced(graphics, atlasLocation, x, y, width, height, sliceWidth, sliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, textureX, textureY);
    }

    public static void blitNineSliced(GuiGraphicsExtractor graphics, Identifier atlasLocation, int x, int y, int width, int height, int leftSliceWidth, int topSliceHeight, int rightSliceWidth, int bottomSliceHeight, int uWidth, int vHeight, int textureX, int textureY) {
        leftSliceWidth = Math.min(leftSliceWidth, width / 2);
        rightSliceWidth = Math.min(rightSliceWidth, width / 2);
        topSliceHeight = Math.min(topSliceHeight, height / 2);
        bottomSliceHeight = Math.min(bottomSliceHeight, height / 2);
        if (width == uWidth && height == vHeight) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y, (float) textureX, (float) textureY, width, height, 256, 256);
        } else if (height == vHeight) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y, (float) textureX, (float) textureY, leftSliceWidth, height, 256, 256);
            blitRepeating(graphics, atlasLocation, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, height, textureX + leftSliceWidth, textureY, uWidth - rightSliceWidth - leftSliceWidth, vHeight);
            graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x + width - rightSliceWidth, y, (float) (textureX + uWidth - rightSliceWidth), (float) textureY, rightSliceWidth, height, 256, 256);
        } else if (width == uWidth) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y, (float) textureX, (float) textureY, width, topSliceHeight, 256, 256);
            blitRepeating(graphics, atlasLocation, x, y + topSliceHeight, width, height - bottomSliceHeight - topSliceHeight, textureX, textureY + topSliceHeight, uWidth, vHeight - bottomSliceHeight - topSliceHeight);
            graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y + height - bottomSliceHeight, (float) textureX, (float) (textureY + vHeight - bottomSliceHeight), width, bottomSliceHeight, 256, 256);
        } else {
            graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y, (float) textureX, (float) textureY, leftSliceWidth, topSliceHeight, 256, 256);
            blitRepeating(graphics, atlasLocation, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, topSliceHeight, textureX + leftSliceWidth, textureY, uWidth - rightSliceWidth - leftSliceWidth, topSliceHeight);
            graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x + width - rightSliceWidth, y, (float) (textureX + uWidth - rightSliceWidth), (float) textureY, rightSliceWidth, topSliceHeight, 256, 256);
            graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y + height - bottomSliceHeight, (float) textureX, (float) (textureY + vHeight - bottomSliceHeight), leftSliceWidth, bottomSliceHeight, 256, 256);
            blitRepeating(graphics, atlasLocation, x + leftSliceWidth, y + height - bottomSliceHeight, width - rightSliceWidth - leftSliceWidth, bottomSliceHeight, textureX + leftSliceWidth, textureY + vHeight - bottomSliceHeight, uWidth - rightSliceWidth - leftSliceWidth, bottomSliceHeight);
            graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x + width - rightSliceWidth, y + height - bottomSliceHeight, (float) (textureX + uWidth - rightSliceWidth), (float) (textureY + vHeight - bottomSliceHeight), rightSliceWidth, bottomSliceHeight, 256, 256);
            blitRepeating(graphics, atlasLocation, x, y + topSliceHeight, leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX, textureY + topSliceHeight, leftSliceWidth, vHeight - bottomSliceHeight - topSliceHeight);
            blitRepeating(graphics, atlasLocation, x + leftSliceWidth, y + topSliceHeight, width - rightSliceWidth - leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX + leftSliceWidth, textureY + topSliceHeight, uWidth - rightSliceWidth - leftSliceWidth, vHeight - bottomSliceHeight - topSliceHeight);
            blitRepeating(graphics, atlasLocation, x + width - rightSliceWidth, y + topSliceHeight, leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX + uWidth - rightSliceWidth, textureY + topSliceHeight, rightSliceWidth, vHeight - bottomSliceHeight - topSliceHeight);
        }
    }

    public static void blitRepeating(GuiGraphicsExtractor graphics, Identifier atlasLocation, int x, int y, int width, int height, int uOffset, int vOffset, int sourceWidth, int sourceHeight) {
        blitRepeating(graphics, atlasLocation, x, y, width, height, uOffset, vOffset, sourceWidth, sourceHeight, 256, 256);
    }

    public static void blitRepeating(GuiGraphicsExtractor graphics, Identifier atlasLocation, int x, int y, int width, int height, int uOffset, int vOffset, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight) {
        int drawX = x;
        int sliceWidth;
        for (IntIterator widthIterator = slices(width, sourceWidth); widthIterator.hasNext(); drawX += sliceWidth) {
            sliceWidth = widthIterator.nextInt();
            int uPad = (sourceWidth - sliceWidth) / 2;
            int drawY = y;
            int sliceHeight;
            for (IntIterator heightIterator = slices(height, sourceHeight); heightIterator.hasNext(); drawY += sliceHeight) {
                sliceHeight = heightIterator.nextInt();
                int vPad = (sourceHeight - sliceHeight) / 2;
                graphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, drawX, drawY, (float) (uOffset + uPad), (float) (vOffset + vPad), sliceWidth, sliceHeight, textureWidth, textureHeight);
            }
        }
    }

    private static IntIterator slices(int target, int total) {
        int count = Mth.positiveCeilDiv(target, total);
        return new Divisor(target, count);
    }

    public static void blit(GuiGraphicsExtractor graphics, Identifier atlasLocation, int x, int y, int width, int height, int uOffset, int vOffset) {
        blit(graphics, atlasLocation, x, y, width, height, uOffset, vOffset, 256, 256);
    }

    public static void blit(GuiGraphicsExtractor graphics, Identifier atlasLocation, int x, int y, int width, int height, int uOffset, int vOffset, int imageWidth, int imageHeight) {
        float u0 = (float) uOffset / imageWidth;
        float u1 = (float) (uOffset + width) / imageWidth;
        float v0 = (float) vOffset / imageHeight;
        float v1 = (float) (vOffset + height) / imageHeight;
        graphics.blit(atlasLocation, x, y, x + width, x + height, u0, u1, v0, v1);
    }
}
