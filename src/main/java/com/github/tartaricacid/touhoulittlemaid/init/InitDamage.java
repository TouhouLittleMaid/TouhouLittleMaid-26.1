package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityDanmaku;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public final class InitDamage {
    public static final ResourceKey<DamageType> DANMAKU = ResourceKey.create(Registries.DAMAGE_TYPE, IdentifierUtil.modLoc("danmaku"));
    public static final ResourceKey<DamageType> DANMAKU_ENDER_KILLER = ResourceKey.create(Registries.DAMAGE_TYPE, IdentifierUtil.modLoc("danmaku_ender_killer"));

    public static DamageSource danmakuDamage(Entity thrower, EntityDanmaku danmaku) {
        var damageTypes = thrower.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE);
        if (danmaku.isHurtEnderman()) {
            return new DamageSource(damageTypes.getOrThrow(DANMAKU_ENDER_KILLER), danmaku, thrower);
        } else {
            return new DamageSource(damageTypes.getOrThrow(DANMAKU), danmaku, thrower);
        }
    }
}
