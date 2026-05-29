package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import com.github.tartaricacid.touhoulittlemaid.util.functional.TriFunction;
import com.github.tartaricacid.touhoulittlemaid.util.functional.TriPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiPredicate;

public interface CombatHook extends MaidComponent {
    boolean isWithinMeleeAttackRange(LivingEntity target);

    boolean doHurtTarget(ServerLevel level, Entity target, BiPredicate<ServerLevel, Entity> superHurtTarget);

    boolean hurtServer(ServerLevel level, DamageSource source, float amount,
                       TriPredicate<ServerLevel, DamageSource, Float> superHurtServer);

    void actuallyHurt(ServerLevel level, DamageSource damageSrc, float damageAmount);

    ItemStack getProjectile(ItemStack weaponStack);

    void performRangedAttack(LivingEntity target, float distanceFactor);

    ItemStack getItemBlockingWith();

    float applyItemBlocking(ServerLevel level, DamageSource source, float damage,
                            TriFunction<ServerLevel, DamageSource, Float, Float> superMethod);

    boolean canUseShield();
}
