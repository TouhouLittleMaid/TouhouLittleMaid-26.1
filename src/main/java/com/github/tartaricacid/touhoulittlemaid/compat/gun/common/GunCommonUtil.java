package com.github.tartaricacid.touhoulittlemaid.compat.gun.common;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// TODO 枪械类模组还没有升级到 26.1
public class GunCommonUtil {
    public static void initAndAddTask(TaskManager manager) {
    }

    public static boolean isInstalled() {
        return false;
    }

    public static boolean isGun(ItemStack stack) {
        return false;
    }

    public static boolean canStartAttacking(EntityMaid maid) {
        return false;
    }

    @Nullable
    public static Identifier getGunId(ItemStack stack) {
        return null;
    }

    public static Optional<Boolean> canSee(EntityMaid maid, LivingEntity target) {
        return Optional.empty();
    }

    public static void tick(EntityMaid shooter, LivingEntity target, ItemStack gunItem) {
    }

    public static int performGunAttack(EntityMaid shooter, LivingEntity target, ItemStack gunItem) throws Exception {
        return 100;
    }
}
