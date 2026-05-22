package com.github.tartaricacid.touhoulittlemaid.geckolib3.util;

import com.github.tartaricacid.touhoulittlemaid.compat.iris.IrisCompat;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.RenderContext;
import com.mojang.blaze3d.systems.RenderSystem;


public final class RenderContextManager {
    private static boolean renderingInInventory = false;
    private static boolean renderingLevel = false;
    private static boolean offScreen = false;

    public static void setRenderingInInventory(boolean value) {
        renderingInInventory = value;
    }

    public static void setRenderingLevel(boolean value) {
        renderingLevel = value;
    }

    public static void setOffScreen(boolean value) {
        offScreen = value;
    }

    public static RenderContext extract(boolean immutable) {
        RenderSystem.assertOnRenderThread();
        return new RenderContext(renderingLevel, IrisCompat.isRenderingShadow(), renderingInInventory, offScreen, immutable);
    }
}
