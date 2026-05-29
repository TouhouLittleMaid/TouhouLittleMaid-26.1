package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;

import javax.annotation.Nullable;

public interface SoundHook extends MaidComponent {
    boolean playSound(SoundEvent soundEvent, float volume, float pitch);

    @Nullable
    SoundEvent getAmbientSound();

    @Nullable
    SoundEvent getHurtSound(DamageSource damageSourceIn);

    @Nullable
    SoundEvent getDeathSound();
}
