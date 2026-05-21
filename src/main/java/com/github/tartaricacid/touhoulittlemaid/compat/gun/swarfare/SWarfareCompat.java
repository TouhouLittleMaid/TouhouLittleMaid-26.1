package com.github.tartaricacid.touhoulittlemaid.compat.gun.swarfare;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

// TODO 枪械类模组还没有升级到 26.1
public class SWarfareCompat {
    private static final String MOD_ID = "superbwarfare";

    public static boolean shouldHideLivingRender(LivingEntity entity) {
        return false;
    }

    public static boolean isVehicle(Entity entity) {
        return false;
    }
}
