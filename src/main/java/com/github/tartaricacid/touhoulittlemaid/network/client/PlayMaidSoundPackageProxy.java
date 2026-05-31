package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.client.sound.data.MaidSoundInstance;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.PlayMaidSoundPackage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

public final class PlayMaidSoundPackageProxy {
    public static void handle(PlayMaidSoundPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(message.entityId());
        if (!(entity instanceof EntityMaid maid)) {
            return;
        }
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.getValue(message.soundEvent());
        if (event == null) {
            return;
        }
        mc.getSoundManager().play(new MaidSoundInstance(event, message.id(), maid));
    }
}
