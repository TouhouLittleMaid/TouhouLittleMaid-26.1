package com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.instance;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.stream.AudioStreamProvider;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.stream.CustomAudioStream;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.stream.LoopingAudioStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CustomSoundInstance extends MinecraftSoundInstance {
    private final AudioStreamProvider provider;
    private volatile CustomAudioStream audioStream;

    public CustomSoundInstance(SoundEvent soundEvent, AudioStreamProvider provider, Entity entity) {
        super(soundEvent, entity);
        this.provider = provider;
    }

    @Override
    public @NotNull CompletableFuture<AudioStream> getStream(@NotNull SoundBufferLibrary soundBuffers, @NotNull Sound sound, boolean looping) {
        var future = new CompletableFuture<AudioStream>();
        Minecraft.getInstance().execute(() -> {
            try {
                var stream = looping ? new LoopingAudioStream(provider) : provider.openStream();
                audioStream = stream;
                future.complete(stream);
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public boolean isStopped() {
        if (audioStream == null) {
            return super.isStopped();
        }
        if (audioStream.isClosed()) {
            if (!super.isStopped()) {
                super.setStopped();
            }
            return true;
        }
        return super.isStopped();
    }
}
