package com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.instance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class MinecraftSoundInstance extends AbstractTickableSoundInstance implements IStoppableSound {
    protected final Entity entity;
    protected float configuredVolume = 1.0f;

    public MinecraftSoundInstance(SoundEvent soundEvent, Entity entity) {
        super(soundEvent, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.x = this.entity.getX();
        this.y = this.entity.getY();
        this.z = this.entity.getZ();
    }

    @Override
    public void tick() {
        // TODO: 配置项
        this.volume = this.configuredVolume * 100.0f / 100.0f;
        if (this.entity.isRemoved()) {
            this.stop();
        } else {
            this.x = this.entity.getX();
            this.y = this.entity.getY();
            this.z = this.entity.getZ();
        }
    }

    public void setConfiguredVolume(float volume) {
        this.configuredVolume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setAsUI() {
        this.attenuation = Attenuation.NONE;
        this.relative = true;
    }

    @Override
    public void setStopped() {
        this.stop();
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().getSoundManager().stop(this);
        });
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }
}
