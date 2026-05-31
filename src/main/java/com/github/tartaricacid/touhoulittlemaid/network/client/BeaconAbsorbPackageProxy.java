package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.network.message.BeaconAbsorbPackage;
import net.minecraft.client.Minecraft;

public final class BeaconAbsorbPackageProxy {
    public static void handle(BeaconAbsorbPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            EntityPowerPoint.spawnExplosionParticle(mc.level, message.x(), message.y(), message.z(), mc.level.getRandom());
        }
    }
}
