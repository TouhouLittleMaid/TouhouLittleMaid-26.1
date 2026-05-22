package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.entity.EntityFunction;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.binding.ValueConversions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.StringUtils;

public class SoundFunction {
    public static class Stop extends EntityFunction {
        @Override
        protected Object eval(ExecutionContext<IContext<Entity>> ctx, Function.ArgumentCollection arguments) {
            if (!ctx.entity().allowEmitting()) {
                return false;
            }
            int id;
            var idObj = arguments.getValue(ctx, 0);
            if (idObj instanceof Number idNum) {
                id = -idNum.intValue();
                if (id > 0) {
                    return false;
                }
            } else {
                id = ValueConversions.asPooledString(idObj);
            }
            var global = arguments.size() == 2 && arguments.getAsBoolean(ctx, 1);
            var manager = ctx.entity().getSoundManager(global);
            if (manager != null) {
                return manager.stopPlayingSound(id);
            }
            return false;
        }

        @Override
        public boolean validateArgumentSize(int size) {
            return size == 1 || size == 2;
        }
    }

    public static class StopAll extends EntityFunction {
        @Override
        protected Object eval(ExecutionContext<IContext<Entity>> ctx, Function.ArgumentCollection arguments) {
            if (!ctx.entity().allowEmitting()) {
                return false;
            }
            var global = arguments.size() > 0 && arguments.getAsBoolean(ctx, 0);
            var manager = ctx.entity().getSoundManager(global);
            if (manager != null) {
                manager.stopAllPlayingSounds();
                return true;
            }
            return false;
        }

        @Override
        public boolean validateArgumentSize(int size) {
            return size <= 1;
        }
    }

    public static class Play extends EntityFunction {
        @Override
        protected Object eval(ExecutionContext<IContext<Entity>> ctx, Function.ArgumentCollection arguments) {
            if (!ctx.entity().allowEmitting()) {
                return false;
            }
            int id;
            var idObj = arguments.getValue(ctx, 0);
            if (idObj instanceof Number idNum) {
                id = -idNum.intValue();
                if (id > 0) {
                    return false;
                }
            } else {
                id = ValueConversions.asPooledString(idObj);
            }
            String soundName = arguments.getAsString(ctx, 1);
            if (StringUtils.isBlank(soundName)) {
                return false;
            }
            Entity targetEntity = ctx.entity().entity();
            if (targetEntity == null) {
                return false;
            }
            int mode;
            if (arguments.size() >= 3) {
                mode = arguments.getAsInt(ctx, 2);
                if (mode < 0 || mode > 7) {
                    return false;
                }
            } else {
                mode = 0;
            }

            var manager = ctx.entity().getSoundManager((mode & 2) == 2);
            if (manager == null) {
                return false;
            }

            return manager.playSound(ctx.entity().animatableEntity(), id, soundName, (mode & 1) == 1, instance -> {
                if (instance == null) {
                    ctx.entity().debugPrint("Sound not found: %s", soundName);
                    return;
                }
                instance.setLooping((mode & 4) == 4);
                // 设置音量和音调
                if (arguments.size() >= 4) {
                    float volume = arguments.getAsFloat(ctx, 3);
                    instance.setConfiguredVolume(Mth.clamp(volume, 0.001f, 1000f));
                }
                if (arguments.size() >= 5) {
                    float pitch = arguments.getAsFloat(ctx, 4);
                    instance.setPitch(Mth.clamp(pitch, 0.001f, 1000f));
                }

                // 如果是 GUI 内，设置为 UI 音效
                if (ctx.entity().animatableEntity().isFakePlayer()) {
                    instance.setAsUI();
                }
            });
        }

        @Override
        public boolean validateArgumentSize(int size) {
            return size >= 2 && size <= 5;
        }
    }
}