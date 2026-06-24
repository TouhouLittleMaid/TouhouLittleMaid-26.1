package com.github.tartaricacid.touhoulittlemaid.util.migrate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import javax.annotation.Nullable;

/**
 * 方便 26.1 -> 26.2 的迁移，用此类归一化
 */
public final class ScreenUtil {
    private ScreenUtil() {
    }

    public static void setScreen(@Nullable Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    @Nullable
    public static Screen getScreen() {
        return Minecraft.getInstance().screen;
    }

    public static boolean hasOverlay() {
        return Minecraft.getInstance().getOverlay() != null;
    }

    public static void setTitle(Component title) {
        Minecraft.getInstance().gui.setTitle(title);
    }

    public static void setSubtitle(Component subtitle) {
        Minecraft.getInstance().gui.setSubtitle(subtitle);
    }

    public static void setOverlayMessage(Component message, boolean animate) {
        Minecraft.getInstance().gui.setOverlayMessage(message, animate);
    }

    public static Identifier getMobEffectSprite(Holder<MobEffect> effectHolder) {
        return Gui.getMobEffectSprite(effectHolder);
    }
}
