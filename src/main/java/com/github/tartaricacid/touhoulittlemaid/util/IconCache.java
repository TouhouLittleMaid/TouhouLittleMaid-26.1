package com.github.tartaricacid.touhoulittlemaid.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import org.lwjgl.system.MemoryUtil;

import java.util.concurrent.CompletableFuture;

/**
 * Refer: https://github.com/CyclopsMC/IconExporter/
 * MIT license: https://github.com/CyclopsMC/IconExporter/blob/master-1.21/LICENSE.txt
 */
public final class IconCache {
    /**
     * ARGB
     */
    public static final int BACKGROUND_COLOR = 0xFF_00_FF_00;
    /**
     * 不知何故，Minecraft 的顺序是 ABGR
     */
    public static final int BACKGROUND_COLOR_SHIFTED = 0xFF_00_FF_00;

    /**
     * 返回 CompletableFuture 而不在方法内 join，否则会死锁
     */
    public static CompletableFuture<NativeImage> exportImageFromScreenshot(int scaleImage, int backgroundColor) {
        Minecraft mc = Minecraft.getInstance();
        CompletableFuture<NativeImage> done = new CompletableFuture<>();
        // 尝试全屏截图
        Screenshot.takeScreenshot(mc.getMainRenderTarget(), imageFull -> {
            // 从全屏截图中获取我们需要的那部分
            NativeImage image = getSubImage(imageFull, scaleImage, scaleImage);
            // 关闭全屏截图的缓存
            imageFull.close();

            // 将背景颜色转换为透明像素
            for (int cx = 0; cx < image.getWidth(); cx++) {
                for (int cy = 0; cy < image.getHeight(); cy++) {
                    int color = image.getPixel(cx, cy);
                    // 如果颜色等于背景色，那么直接设置为透明像素
                    if (color == backgroundColor) {
                        image.setPixel(cx, cy, 0);
                    }
                }
            }

            done.complete(image);
        });
        return done;
    }

    private static NativeImage getSubImage(NativeImage image, int width, int height) {
        NativeImage imageSub = new NativeImage(width, height, false);
        // 只截取其中一部分贴图
        for (int y = 0; y < imageSub.getHeight(); y++) {
            int pointerOffset = y * image.getWidth() * image.format().components();
            int pointerOffsetSub = y * imageSub.getWidth() * imageSub.format().components();
            MemoryUtil.memCopy(image.pixels + (long) pointerOffset, imageSub.pixels + (long) pointerOffsetSub, (long) imageSub.getWidth() * image.format().components());
        }
        return imageSub;
    }
}
