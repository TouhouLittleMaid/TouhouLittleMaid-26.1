package com.github.tartaricacid.touhoulittlemaid.network.client.ai;

import com.github.tartaricacid.touhoulittlemaid.client.sound.data.MaidAISoundInstance;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.TTSAudioToClientPackage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public final class TTSAudioToClientPackageProxy {
    public static void handle(TTSAudioToClientPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(message.maidId());
        if (!(entity instanceof EntityMaid maid)) {
            return;
        }
        if (maid.isAlive()) {
            MaidAISoundInstance instance = new MaidAISoundInstance(maid, message.data());
            Minecraft.getInstance().getSoundManager().play(instance);
        }
    }
}
