package com.github.tartaricacid.touhoulittlemaid.compat.sbackpack;


import com.github.tartaricacid.touhoulittlemaid.compat.sbackpack.curios.SBackpackCuriosCompat;
import com.github.tartaricacid.touhoulittlemaid.init.registry.CompatRegistry;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.ModList;

public class SBackpackCompat {
    private static boolean IS_LOADED = false;

    public static void init() {
        IS_LOADED = true;
        NeoForge.EVENT_BUS.register(new BackpackRightClickMaidEvent());
        // 女仆与精妙背包的 Curios 兼容
        if (ModList.get().isLoaded(CompatRegistry.CURIOS)) {
            SBackpackCuriosCompat.init();
        }
    }

    public static boolean isLoaded() {
        return IS_LOADED;
    }

    public static boolean isBackpack(ItemStack stack) {
        if (isLoaded()) {
            return SBackpackCompatInner.isBackpack(stack);
        }
        return false;
    }
}
