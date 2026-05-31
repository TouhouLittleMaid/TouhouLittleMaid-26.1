package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.network.message.ItemBreakPackage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class ItemBreakPackageProxy {
    public static void handle(ItemBreakPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity e = mc.level.getEntity(message.id());
        if (e instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
            livingEntity.breakItem(message.item());
        }
    }
}
