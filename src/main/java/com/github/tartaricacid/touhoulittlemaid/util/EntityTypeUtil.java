package com.github.tartaricacid.touhoulittlemaid.util;

import net.minecraft.world.entity.EntityType;

/**
 * 方便 26.1 -> 26.2 迁移的工具类
 */
public final class EntityTypeUtil {
    private EntityTypeUtil() {
    }

    public static EntityType<?> wolf() {
        return EntityType.WOLF;
    }

    public static EntityType<?> cat() {
        return EntityType.CAT;
    }

    public static EntityType<?> parrot() {
        return EntityType.PARROT;
    }

    public static EntityType<?> player() {
        return EntityType.PLAYER;
    }

    public static EntityType<?> armorStand() {
        return EntityType.ARMOR_STAND;
    }

    public static EntityType<?> item() {
        return EntityType.ITEM;
    }

    public static EntityType<?> lightningBolt() {
        return EntityType.LIGHTNING_BOLT;
    }

    public static EntityType<?> ironGolem() {
        return EntityType.IRON_GOLEM;
    }

    public static EntityType<?> creeper() {
        return EntityType.CREEPER;
    }

    public static EntityType<?> zombie() {
        return EntityType.ZOMBIE;
    }

    public static EntityType<?> allay() {
        return EntityType.ALLAY;
    }
}
