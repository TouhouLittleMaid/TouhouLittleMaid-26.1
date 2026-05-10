package com.github.tartaricacid.touhoulittlemaid.compat.sbackpack;


import com.github.tartaricacid.touhoulittlemaid.compat.extracontainer.BackpackProvider;
import com.github.tartaricacid.touhoulittlemaid.compat.extracontainer.ContainerRef;
import com.github.tartaricacid.touhoulittlemaid.compat.extracontainer.ExtraContainerManager;
import com.github.tartaricacid.touhoulittlemaid.compat.sbackpack.curios.SBackpackSlotRef;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

public class SBackpackCompat {
    private static boolean IS_LOADED = false;

    public static void init() {
        IS_LOADED = true;
        NeoForge.EVENT_BUS.register(new BackpackRightClickMaidEvent());
        ExtraContainerManager.register(new SBackpackProvider());
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

    private static class SBackpackProvider implements BackpackProvider {
        @Override
        public boolean isBackpack(ItemStack stack) {
            return SBackpackCompatInner.isBackpack(stack);
        }

        @Override
        public ContainerRef createSlotRef(String slotType, int slotIndex) {
            return new SBackpackSlotRef(slotType, slotIndex);
        }
    }
}
