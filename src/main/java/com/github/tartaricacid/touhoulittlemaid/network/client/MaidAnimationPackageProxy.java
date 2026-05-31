package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.MaidAnimationPackage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class MaidAnimationPackageProxy {
    public static void handle(MaidAnimationPackage message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (level.getEntity(message.maidId()) instanceof EntityMaid maid) {
            maid.getAnimationManager().animationId = message.animationId();
            maid.getAnimationManager().animationRecordTime = System.currentTimeMillis();
            maid.getAnimationManager().shouldReset = true;
        }
    }
}
