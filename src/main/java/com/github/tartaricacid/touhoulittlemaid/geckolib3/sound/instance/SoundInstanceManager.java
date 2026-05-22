package com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.instance;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SoundInstanceManager {
    private final Int2ReferenceOpenHashMap<IStoppableSound> instanceMap;
    private final ReferenceArrayList<IStoppableSound> unnamedInstanceList;

    public SoundInstanceManager() {
        this.instanceMap = new Int2ReferenceOpenHashMap<>(0);
        this.unnamedInstanceList = new ReferenceArrayList<>(0);
    }

    public boolean playSound(AnimatableEntity<?> animatable, int id, String soundName, boolean force, @Nullable Consumer<@Nullable MinecraftSoundInstance> configure) {
        MinecraftSoundInstance instance;
        if (soundName.contains(":")) {
            // 如果声音名带冒号，那么大概率就是调用原版音频，因为 Windows 中冒号不是合法的文件名
            var soundId = Identifier.tryParse(soundName);
            if (soundId != null) {
                SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundId);
                instance = new MinecraftSoundInstance(soundEvent, animatable.getEntity());
            } else {
                instance = null;
            }
        } else {
            // 否则认为是自定义的音频文件
            instance = animatable.getSoundStream(soundName)
                    .map(provider -> new CustomSoundInstance(InitSounds.GECKO_CUSTOM.get(), provider, animatable.getEntity()))
                    .orElse(null);
        }
        if (configure != null) {
            configure.accept(instance);
        }
        if (instance == null) {
            return false;
        }

        if (id != 0) {
            if (force) {
                var oldInstance = instanceMap.put(id, instance);
                if (oldInstance != null && !oldInstance.isStopped()) {
                    oldInstance.setStopped();
                }
            } else {
                var newInstance = instanceMap.compute(id, (ignored, oldInstance) -> {
                    if (oldInstance == null || oldInstance.isStopped()) {
                        return instance;
                    }
                    return oldInstance;
                });
                if (newInstance != instance) {
                    return false;
                }
            }
        } else {
            unnamedInstanceList.add(instance);
        }
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getSoundManager().play(instance));
        return true;
    }

    public boolean stopPlayingSound(int id) {
        if (id == 0) {
            return false;
        }
        var instance = instanceMap.remove(id);
        if (instance != null) {
            instance.setStopped();
            return true;
        }
        return false;
    }

    public void stopAllPlayingSounds() {
        instanceMap.values().forEach(IStoppableSound::setStopped);
        for (var instance: unnamedInstanceList) {
            instance.setStopped();
        }
        instanceMap.clear();
        unnamedInstanceList.clear();
    }

    public void trim() {
        var iter = instanceMap.int2ReferenceEntrySet().fastIterator();
        while (iter.hasNext()) {
            var instance = iter.next();
            if (instance.getValue().isStopped()) {
                iter.remove();
            }
        }

        unnamedInstanceList.removeIf(IStoppableSound::isStopped);
    }
}
