package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.TeleportItemParticlePackage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.CompletableFuture;

public class TeleportItemParticlePackageProxy {
    public static void handle(TeleportItemParticlePackage message) {
        if (message.delayTicks() <= 0) {
            handleSpawnParticle(message);
        } else {
            CompletableFuture.runAsync(() -> handleSpawnParticleDelay(message, message.delayTicks()), Util.backgroundExecutor());
        }
    }

    private static void handleSpawnParticleDelay(TeleportItemParticlePackage message, int delayTicks) {
        try {
            Thread.sleep(delayTicks * 50L);
            Minecraft.getInstance().submitAsync(() -> handleSpawnParticle(message));
        } catch (InterruptedException e) {
            TouhouLittleMaid.LOGGER.error(e.getMessage());
        }
    }

    private static void handleSpawnParticle(TeleportItemParticlePackage message) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) {
            return;
        }
        Entity e = level.getEntity(message.entityId());
        if (!(e instanceof EntityMaid maid) || !e.isAlive()) {
            return;
        }

        level.playLocalSound(
                maid.getX(), maid.getY(), maid.getZ(),
                SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.2F,
                (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 1.4F + 2.0F,
                false
        );

        float eyeHeight = maid.getEyeHeight(maid.getPose()) / 2f;

        Vec3 fromPos;
        Vec3 toPos;
        if (message.chestToMaid()) {
            fromPos = maid.position().add(0, eyeHeight, 0);
            toPos = Vec3.atCenterOf(message.chestPos());
        } else {
            fromPos = Vec3.atCenterOf(message.chestPos());
            toPos = maid.position().add(0, eyeHeight, 0);
        }

        ItemEntity fromEntity = new ItemEntity(level, fromPos.x, fromPos.y, fromPos.z, ItemStack.EMPTY);
        ItemEntity toEntity = new ItemEntity(level, toPos.x, toPos.y, toPos.z, message.itemStack());

        EntityRenderState itemState = mc.getEntityRenderDispatcher().extractEntity(toEntity, 1.0F);
        mc.particleEngine.add(new ItemPickupParticle(level, itemState, fromEntity, toPos.subtract(fromPos)));
    }
}
