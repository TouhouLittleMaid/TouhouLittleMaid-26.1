package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.client.sound.data.MaidSoundInstanceAtPos;
import com.github.tartaricacid.touhoulittlemaid.network.message.PlayMaidSoundAtPosPackage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class PlayMaidSoundAtPosPackageProxy {
    public static void handle(PlayMaidSoundAtPosPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.getValue(message.soundEvent());
        if (event == null) {
            return;
        }
        mc.getSoundManager().play(new MaidSoundInstanceAtPos(event, message.id(),
                message.x(), message.y(), message.z(), message.volume(), message.pitch()));
    }
}
