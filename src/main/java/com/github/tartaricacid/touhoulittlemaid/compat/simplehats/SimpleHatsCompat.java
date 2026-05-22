package com.github.tartaricacid.touhoulittlemaid.compat.simplehats;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class SimpleHatsCompat {
    private static final String SIMPLE_HATS = "simplehats";
    private static boolean isLoaded = false;

    public static void init() {
        isLoaded = ModList.get().isLoaded(SIMPLE_HATS);
    }

    public static boolean isHatItem(ItemStack stack) {
        return false;
    }

    public static void extract(ItemStackRenderState state, ItemStack stack) {

    }

    public static void submit(ItemStackRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
    }
}
