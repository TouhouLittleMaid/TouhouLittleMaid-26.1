/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

@SuppressWarnings({"unchecked"})
public class AnimationUtils {
    public static float convertTicksToSeconds(float ticks) {
        return ticks / 20;
    }

    public static float convertSecondsToTicks(float seconds) {
        return seconds * 20;
    }

    /**
     * 获取实体的 Renderer
     */
    public static <T extends Entity> EntityRenderer<T, ?> getRenderer(T entity) {
        EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        return (EntityRenderer<T, ?>) renderManager.getRenderer(entity);
    }
}