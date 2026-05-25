package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID, value = Dist.CLIENT)
public final class EntityCacheUtil {
    /**
     * 实体缓存，在客户端会大量运用实体渲染，这个缓存可以减少重复创建实体带来的性能问题
     */
    private static final Cache<EntityType<?>, Entity> ENTITY_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private static boolean CREATING_PREVIEW = false;

    /**
     * 女仆实体缓存，用于雕像，因为雕像如果共用一个实体，会导致 GeckoLib 动画渲染错误
     */
    public static final Cache<Long, EntityMaid> STATUE_CACHE = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS).build();
    /**
     * 女仆实体缓存，用于物品形态的手办，因为如果共用一个实体，会导致 GeckoLib 动画渲染错误
     */
    public static final Cache<ItemStack, EntityMaid> GARAGE_KIT_CACHE = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS).build();
    private static ResourceKey<Level> dimAt;

    public static EntityMaid getMaid(Level level, EntitySpawnReason reason) {
        return getEntity(EntityMaid.TYPE, (l, _) -> new EntityMaid(l), level, reason);
    }

    public static EntityChair getChair(Level level, EntitySpawnReason reason) {
        return getEntity(EntityChair.TYPE, (l, _) -> new EntityChair(l), level, reason);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity> E getEntity(EntityType<E> type, BiFunction<Level, EntitySpawnReason, E> fallback, Level level, EntitySpawnReason reason) {
        CREATING_PREVIEW = true;
        try {
            return (E) ENTITY_CACHE.get(type, () ->
                    Objects.requireNonNullElseGet(type.create(level, reason), () -> fallback.apply(level, reason)));
        } catch (ExecutionException e) {
            TouhouLittleMaid.LOGGER.error("Failed to create preview entity", e);
            return fallback.apply(level, reason);
        } finally {
            CREATING_PREVIEW = false;
        }
    }

    public static boolean creatingPreviewEntity() {
        return CREATING_PREVIEW;
    }

    public static void clearMaidDataResidue(EntityMaid maid, boolean clearEquipmentData) {
        maid.hurtDuration = 0;
        maid.hurtTime = 0;
        maid.deathTime = 0;
        maid.setOnGround(true);
        maid.setInSittingPose(false);
        maid.setMaidBackpackType(BackpackManager.getEmptyBackpack());
        maid.setCustomName(Component.empty());
        if (clearEquipmentData) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                maid.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    public static void onChangeDim(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() && event.getEntity() == Minecraft.getInstance().player) {
            ResourceKey<Level> dim = event.getEntity().level.dimension();
            if (!dim.equals(dimAt)) {
                dimAt = dim;
                EntityCacheUtil.ENTITY_CACHE.invalidateAll();
            }
        }
    }
}
