package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.ContextFunction;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.alchemy.PotionContents;

public class EffectLevel extends ContextFunction<Entity> {
    @Override
    public boolean validateArgumentSize(int size) {
        return size >= 1;
    }

    @Override
    protected Object eval(ExecutionContext<IContext<Entity>> context, ArgumentCollection arguments) {
        int sum = 0;
        for (var i = 0; i < arguments.size(); ++i) {
            Identifier effectId = arguments.getAsResourceLocation(context, i);
            if (effectId == null) {
                continue;
            }

            var effectOpt = BuiltInRegistries.MOB_EFFECT.get(effectId);
            if (effectOpt.isEmpty()) {
                continue;
            }

            var effect = effectOpt.get();
            if (context.entity().entity() instanceof LivingEntity) {
                MobEffectInstance instance = ((LivingEntity) context.entity().entity()).getEffect(effect);
                if (instance != null) {
                    sum += instance.getAmplifier() + 1;
                }
            } else if (context.entity().entity() instanceof Arrow arrow) {
                for (MobEffectInstance instance : arrow.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects()) {
                    if (instance.getEffect() == effect) {
                        sum += instance.getAmplifier() + 1;
                        break;
                    }
                }
            } else {
                return null;
            }
        }

        return sum;
    }
}
