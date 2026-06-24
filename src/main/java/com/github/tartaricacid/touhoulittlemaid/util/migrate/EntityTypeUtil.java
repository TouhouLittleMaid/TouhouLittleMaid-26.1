package com.github.tartaricacid.touhoulittlemaid.util.migrate;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * 方便 26.1 -> 26.2 迁移的工具类
 */
public final class EntityTypeUtil {
    private EntityTypeUtil() {
    }

    public static Optional<EntityType<?>> byString(String id) {
        Identifier identifier = Identifier.tryParse(id);
        return BuiltInRegistries.ENTITY_TYPE.getOptional(identifier);
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
