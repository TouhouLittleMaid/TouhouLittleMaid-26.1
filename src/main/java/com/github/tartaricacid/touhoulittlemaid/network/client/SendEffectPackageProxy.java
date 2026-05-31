package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.SendEffectPackage;
import com.github.tartaricacid.touhoulittlemaid.network.message.SendEffectPackage.EffectData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public final class SendEffectPackageProxy {
    public static void handle(SendEffectPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(message.id());
        if (entity instanceof EntityMaid maid && maid.isAlive()) {
            maid.setEffects(message.effects().stream().map(EffectData::new).toList());
        }
    }
}
