package com.github.tartaricacid.touhoulittlemaid.datagen;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

public class DamageTypeProvider {
    public static final ResourceKey<DamageType> DANMAKU = key("danmaku");
    public static final ResourceKey<DamageType> DANMAKU_ENDER_KILLER = key("danmaku_ender_killer");

    public static void bootstrap(BootstrapContext<DamageType> ctx) {
        ctx.register(DANMAKU, new DamageType("touhou_little_maid.danmaku",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.1f, DamageEffects.HURT, DeathMessageType.DEFAULT));
        ctx.register(DANMAKU_ENDER_KILLER, new DamageType("touhou_little_maid.danmaku",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.1f, DamageEffects.HURT, DeathMessageType.DEFAULT));
    }

    public static ResourceKey<DamageType> key(String id) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, id));
    }
}
