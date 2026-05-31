package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.SpawnParticlePackage;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;

import java.util.concurrent.CompletableFuture;

public final class SpawnParticlePackageProxy {
    public static void handle(SpawnParticlePackage message) {
        if (message.delayTicks() <= 0) {
            handleSpawnParticle(message);
        } else {
            CompletableFuture.runAsync(() -> handleSpawnParticleDelay(message, message.delayTicks()), Util.backgroundExecutor());
        }
    }

    private static void handleSpawnParticleDelay(SpawnParticlePackage message, int delayTicks) {
        try {
            Thread.sleep(delayTicks * 50L);
            Minecraft.getInstance().submitAsync(() -> handleSpawnParticle(message));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleSpawnParticle(SpawnParticlePackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity e = mc.level.getEntity(message.entityId());
        if (e instanceof EntityMaid maid && e.isAlive()) {
            switch (message.particleType()) {
                case EXPLOSION:
                    maid.spawnExplosionParticle();
                    return;
                case BUBBLE:
                    maid.spawnBubbleParticle();
                    return;
                case HEART:
                    maid.spawnHeartParticle();
                    return;
                case RANK_UP:
                    maid.spawnRankUpParticle();
                    return;
                case HEAL:
                    maid.spawnRestoreHealthParticle(maid.getRandom().nextInt(3) + 7);
                    return;
                default:
            }
        }
    }
}
