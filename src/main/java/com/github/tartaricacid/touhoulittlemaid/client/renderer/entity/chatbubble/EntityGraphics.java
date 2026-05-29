package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Divisor;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class EntityGraphics {
    private final SubmitNodeCollector submitNode;
    private final PoseStack poseStack;
    private final EntityMaidRenderState state;
    private final int packedLight;
    private final float partialTicks;

    public EntityGraphics(SubmitNodeCollector submitNode, PoseStack poseStack, EntityMaidRenderState state, int packedLight, float partialTicks) {
        this.submitNode = submitNode;
        this.poseStack = poseStack;
        this.state = state;
        this.packedLight = packedLight;
        this.partialTicks = partialTicks;
    }

    public void fill(int minX, int minY, int maxX, int maxY, int color) {
        this.fill(minX, minY, maxX, maxY, 0, color);
    }

    public void fill(int minX, int minY, int maxX, int maxY, int z, int color) {
        this.fill(RenderTypes.textBackground(), minX, minY, maxX, maxY, z, color);
    }

    public void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int color) {
        this.fill(renderType, minX, minY, maxX, maxY, 0, color);
    }

    public void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int z, int color) {
        if (minX < maxX) {
            int i = minX;
            minX = maxX;
            maxX = i;
        }
        if (minY < maxY) {
            int j = minY;
            minY = maxY;
            maxY = j;
        }

        int finalMinX = minX;
        int finalMinY = minY;
        int finalMaxY = maxY;
        int finalMaxX = maxX;

        this.submitNode.submitCustomGeometry(this.poseStack, renderType, (pose, consumer) -> {
            Matrix4f matrix4f = pose.pose();
            consumer.addVertex(matrix4f, finalMinX, finalMinY, z).setColor(color).setLight(this.packedLight);
            consumer.addVertex(matrix4f, finalMinX, finalMaxY, z).setColor(color).setLight(this.packedLight);
            consumer.addVertex(matrix4f, finalMaxX, finalMaxY, z).setColor(color).setLight(this.packedLight);
            consumer.addVertex(matrix4f, finalMaxX, finalMinY, z).setColor(color).setLight(this.packedLight);
        });
    }

    public void drawString(FormattedCharSequence text, int x, int y, int color) {
        this.drawString(text, x, y, color, true);
    }

    public void drawString(FormattedCharSequence text, float x, float y, int color, boolean dropShadow) {
        this.submitNode.submitText(this.poseStack, x, y, text, dropShadow, Font.DisplayMode.NORMAL, this.packedLight, color, 0, 0);
    }

    public void drawString(Component text, int x, int y, int color, boolean dropShadow) {
        this.drawString(text.getVisualOrderText(), x, y, color, dropShadow);
    }

    public void drawWordWrap(Font font, FormattedText text, int startX, int startY, int lineWidth, int color) {
        int currentY = startY;
        for (FormattedCharSequence lineSequence : font.split(text, lineWidth)) {
            this.drawString(lineSequence, startX, currentY, color, false);
            currentY += 9;
        }
    }

    public void blitNineSliced(Identifier atlasLocation, int x, int y, int width, int height, int sliceSize, int uOffset, int vOffset, int textureWidth, int textureHeight) {
        this.blitNineSliced(atlasLocation, x, y, width, height, sliceSize, sliceSize, sliceSize, sliceSize, uOffset, vOffset, textureWidth, textureHeight);
    }

    public void blitNineSliced(Identifier atlasLocation, int x, int y, int width, int height, int sliceWidth, int sliceHeight, int uWidth, int vHeight, int textureX, int textureY) {
        this.blitNineSliced(atlasLocation, x, y, width, height, sliceWidth, sliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, textureX, textureY);
    }

    public void blitNineSliced(Identifier atlasLocation, int x, int y, int width, int height, int leftSliceWidth, int topSliceHeight, int rightSliceWidth, int bottomSliceHeight, int uWidth, int vHeight, int textureX, int textureY) {
        leftSliceWidth = Math.min(leftSliceWidth, width / 2);
        rightSliceWidth = Math.min(rightSliceWidth, width / 2);
        topSliceHeight = Math.min(topSliceHeight, height / 2);
        bottomSliceHeight = Math.min(bottomSliceHeight, height / 2);
        if (width == uWidth && height == vHeight) {
            this.blit(atlasLocation, x, y, textureX, textureY, width, height);
        } else if (height == vHeight) {
            this.blit(atlasLocation, x, y, textureX, textureY, leftSliceWidth, height);
            this.blitRepeating(atlasLocation, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, height, textureX + leftSliceWidth, textureY, uWidth - rightSliceWidth - leftSliceWidth, vHeight);
            this.blit(atlasLocation, x + width - rightSliceWidth, y, textureX + uWidth - rightSliceWidth, textureY, rightSliceWidth, height);
        } else if (width == uWidth) {
            this.blit(atlasLocation, x, y, textureX, textureY, width, topSliceHeight);
            this.blitRepeating(atlasLocation, x, y + topSliceHeight, width, height - bottomSliceHeight - topSliceHeight, textureX, textureY + topSliceHeight, uWidth, vHeight - bottomSliceHeight - topSliceHeight);
            this.blit(atlasLocation, x, y + height - bottomSliceHeight, textureX, textureY + vHeight - bottomSliceHeight, width, bottomSliceHeight);
        } else {
            this.blit(atlasLocation, x, y, textureX, textureY, leftSliceWidth, topSliceHeight);
            this.blitRepeating(atlasLocation, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, topSliceHeight, textureX + leftSliceWidth, textureY, uWidth - rightSliceWidth - leftSliceWidth, topSliceHeight);
            this.blit(atlasLocation, x + width - rightSliceWidth, y, textureX + uWidth - rightSliceWidth, textureY, rightSliceWidth, topSliceHeight);
            this.blit(atlasLocation, x, y + height - bottomSliceHeight, textureX, textureY + vHeight - bottomSliceHeight, leftSliceWidth, bottomSliceHeight);
            this.blitRepeating(atlasLocation, x + leftSliceWidth, y + height - bottomSliceHeight, width - rightSliceWidth - leftSliceWidth, bottomSliceHeight, textureX + leftSliceWidth, textureY + vHeight - bottomSliceHeight, uWidth - rightSliceWidth - leftSliceWidth, bottomSliceHeight);
            this.blit(atlasLocation, x + width - rightSliceWidth, y + height - bottomSliceHeight, textureX + uWidth - rightSliceWidth, textureY + vHeight - bottomSliceHeight, rightSliceWidth, bottomSliceHeight);
            this.blitRepeating(atlasLocation, x, y + topSliceHeight, leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX, textureY + topSliceHeight, leftSliceWidth, vHeight - bottomSliceHeight - topSliceHeight);
            this.blitRepeating(atlasLocation, x + leftSliceWidth, y + topSliceHeight, width - rightSliceWidth - leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX + leftSliceWidth, textureY + topSliceHeight, uWidth - rightSliceWidth - leftSliceWidth, vHeight - bottomSliceHeight - topSliceHeight);
            this.blitRepeating(atlasLocation, x + width - rightSliceWidth, y + topSliceHeight, leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX + uWidth - rightSliceWidth, textureY + topSliceHeight, rightSliceWidth, vHeight - bottomSliceHeight - topSliceHeight);
        }
    }

    public void blitRepeating(Identifier atlas, int startX, int startY, int areaWidth, int areaHeight, int uOffset, int vOffset, int sourceWidth, int sourceHeight) {
        blitRepeating(atlas, startX, startY, areaWidth, areaHeight, uOffset, vOffset, sourceWidth, sourceHeight, 256, 256);
    }

    public void blitRepeating(Identifier atlas, int startX, int startY, int areaWidth, int areaHeight, int uOffset, int vOffset, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight) {
        int currentX = startX;
        int sliceWidth;
        for (IntIterator widthIterator = slices(areaWidth, sourceWidth); widthIterator.hasNext(); currentX += sliceWidth) {
            sliceWidth = widthIterator.nextInt();
            int uPadding = (sourceWidth - sliceWidth) / 2;
            int currentY = startY;
            int sliceHeight;
            for (IntIterator heightIterator = slices(areaHeight, sourceHeight); heightIterator.hasNext(); currentY += sliceHeight) {
                sliceHeight = heightIterator.nextInt();
                int vPadding = (sourceHeight - sliceHeight) / 2;
                this.blit(atlas, currentX, currentY, uOffset + uPadding, vOffset + vPadding, sliceWidth, sliceHeight, textureWidth, textureHeight);
            }
        }
    }

    private static IntIterator slices(int totalLength, int sliceLength) {
        int count = Mth.positiveCeilDiv(totalLength, sliceLength);
        return new Divisor(totalLength, count);
    }

    public void blit(Identifier atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        this.blit(atlasLocation, x, y, 0, uOffset, vOffset, uWidth, vHeight, 256, 256);
    }

    public void blit(Identifier atlasLocation, int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        this.blit(atlasLocation, x, x + uWidth, y, y + vHeight, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    public void blit(Identifier atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight) {
        this.blit(atlasLocation, x, y, width, height, uOffset, vOffset, width, height, textureWidth, textureHeight);
    }

    public void blit(Identifier atlasLocation, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        this.blit(atlasLocation, x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    void blit(Identifier atlasLocation, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight) {
        this.innerBlit(atlasLocation, x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / textureWidth, (uOffset + uWidth) / textureWidth, (vOffset + 0.0F) / textureHeight, (vOffset + vHeight) / textureHeight);
    }

    public void innerBlit(Identifier atlas, int x1, int x2, int y1, int y2, int z, float minU, float maxU, float minV, float maxV) {
        this.submitNode.submitCustomGeometry(this.poseStack, RenderTypes.text(atlas), ((pose, buffer) -> {
            Matrix4f matrix4f = pose.pose();
            buffer.addVertex(matrix4f, x1, y1, z).setColor(0xFFFFFFFF).setUv(minU, minV).setLight(this.packedLight);
            buffer.addVertex(matrix4f, x1, y2, z).setColor(0xFFFFFFFF).setUv(minU, maxV).setLight(this.packedLight);
            buffer.addVertex(matrix4f, x2, y2, z).setColor(0xFFFFFFFF).setUv(maxU, maxV).setLight(this.packedLight);
            buffer.addVertex(matrix4f, x2, y1, z).setColor(0xFFFFFFFF).setUv(maxU, minV).setLight(this.packedLight);
        }));
    }

    public EntityMaidRenderState getRenderState() {
        return state;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public SubmitNodeCollector getSubmitNode() {
        return submitNode;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}
