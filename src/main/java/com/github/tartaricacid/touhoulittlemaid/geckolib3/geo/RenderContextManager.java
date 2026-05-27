package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo;

import com.github.tartaricacid.touhoulittlemaid.compat.iris.IrisCompat;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.mojang.blaze3d.systems.RenderSystem;


public final class RenderContextManager {
    private static boolean renderingInInventory = false;
    private static boolean renderingLevel = false;
    private static boolean offScreen = false;
    private static int forceImmutable = 0;

    public static void setRenderingInInventory(boolean value) {
        renderingInInventory = value;
    }

    public static void setRenderingLevel(boolean value) {
        renderingLevel = value;
    }

    public static void setOffScreen(boolean value) {
        offScreen = value;
    }

    public static RenderContext extract(AnimatableEntity<?> entity) {
        RenderSystem.assertOnRenderThread();
        var ctx = new RenderContext(renderingLevel, IrisCompat.isRenderingShadow(), renderingInInventory, offScreen, true);
        if (forceImmutable == 0 && !entity.determinImmutableContext(ctx)) {
            ctx = new RenderContext(renderingLevel, ctx.irisShadow(), renderingInInventory, offScreen, false);;
        }
        return ctx;
    }

    public static Scope forceImmutable() {
        RenderSystem.assertOnRenderThread();
        ++forceImmutable;
        return () -> --forceImmutable;
    }

    @FunctionalInterface
    public interface Scope extends AutoCloseable {
        @Override
        void close();
    }
}
