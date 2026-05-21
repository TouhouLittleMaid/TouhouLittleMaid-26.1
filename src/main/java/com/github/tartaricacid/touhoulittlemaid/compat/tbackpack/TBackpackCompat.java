package com.github.tartaricacid.touhoulittlemaid.compat.tbackpack;

import net.minecraft.world.item.ItemStack;

public class TBackpackCompat {
    private static boolean IS_LOADED = false;

    public static void init() {
    }

    public static boolean isLoaded() {
        return IS_LOADED;
    }

    public static boolean isBackpack(ItemStack stack) {
        return false;
    }
}
