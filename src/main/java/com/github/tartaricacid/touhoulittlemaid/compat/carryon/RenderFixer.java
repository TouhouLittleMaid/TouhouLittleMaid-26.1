package com.github.tartaricacid.touhoulittlemaid.compat.carryon;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

public class RenderFixer {
    private static final String CARRY_ON_ID = "carryon";

    @Deprecated(forRemoval = true)
    public static boolean isCarryOnRender(ItemStack stack, MultiBufferSource bufferSource) {
        // TODO 已经不需要了，需要删除
        return false;
    }
}
