package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


/**
 * 粒子效果管理器，负责生成各种粒子效果
 */
@MaidManagerDef(alias = "particleManager", exposeView = true)
public class MaidParticleManager {
    private final EntityMaid maid;
    private final Level level;
    private final RandomSource random;

    public MaidParticleManager(EntityMaid entityMaid) {
        this.maid = entityMaid;
        this.level = entityMaid.level;
        this.random = maid.getRandom();
    }

    void tick() {
        // 女仆无敌状态时，随机散发末影人粒子
        if (maid.getSyncInvulnerable()
            && MiscConfig.INVULNERABLE_PARTICLE_EFFECT.get()
            && maid.getOwner() != null
        ) {
            spawnPortalParticle();
        }
    }

    public void spawnPortalParticle() {
        if (isClient()) {
            level.addParticle(ParticleTypes.PORTAL,
                    maid.getX() + (random.nextDouble() - 0.5D) * (double) maid.getBbWidth(),
                    maid.getY() + random.nextDouble() * (double) maid.getBbHeight() - 0.25D,
                    maid.getZ() + (random.nextDouble() - 0.5D) * (double) maid.getBbWidth(),
                    (random.nextDouble() - 0.5D) * 2.0, -random.nextDouble(),
                    (random.nextDouble() - 0.5D) * 2.0
            );
        }
    }

    public void spawnRestoreHealthParticle(int particleCount) {
        if (isClient()) {
            for (int i = 0; i < particleCount; ++i) {
                double xRandom = random.nextGaussian() * 0.02;
                double yRandom = random.nextGaussian() * 0.02;
                double zRandom = random.nextGaussian() * 0.02;

                level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.9f, 0.1f, 0.1f),
                        maid.getX() + (random.nextFloat() * maid.getBbWidth() * 2.0F) - maid.getBbWidth() - xRandom * 10.0,
                        maid.getY() + (random.nextFloat() * maid.getBbHeight()) - yRandom * 10.0,
                        maid.getZ() + (random.nextFloat() * maid.getBbWidth() * 2.0F) - maid.getBbWidth() - zRandom * 10.0,
                        0, 0, 0
                );
            }
        }
    }

    public void spawnExplosionParticle() {
        if (isClient()) {
            for (int i = 0; i < 20; ++i) {
                float mx = (random.nextFloat() - 0.5F) * 0.02F;
                float my = (random.nextFloat() - 0.5F) * 0.02F;
                float mz = (random.nextFloat() - 0.5F) * 0.02F;
                level.addParticle(ParticleTypes.CLOUD,
                        maid.getX() + random.nextFloat() - 0.5F,
                        maid.getY() + random.nextFloat() - 0.5F,
                        maid.getZ() + random.nextFloat() - 0.5F,
                        mx, my, mz
                );
            }
        }
    }

    public void spawnBubbleParticle() {
        if (isClient()) {
            for (int i = 0; i < 8; ++i) {
                double offsetX = 2 * random.nextDouble() - 1;
                double offsetY = random.nextDouble() / 2;
                double offsetZ = 2 * random.nextDouble() - 1;
                level.addParticle(ParticleTypes.BUBBLE,
                        maid.getX() + offsetX,
                        maid.getY() + offsetY,
                        maid.getZ() + offsetZ,
                        0, 0.1, 0
                );
            }
        }
    }

    public void spawnHeartParticle() {
        if (isClient()) {
            for (int i = 0; i < 8; ++i) {
                double offsetX = random.nextGaussian() * 0.02;
                double offsetY = random.nextGaussian() * 0.02;
                double offsetZ = random.nextGaussian() * 0.02;
                level.addParticle(ParticleTypes.HEART,
                        maid.getRandomX(1.0),
                        maid.getRandomY() + 0.5,
                        maid.getRandomZ(1.0),
                        offsetX, offsetY, offsetZ
                );
            }
        }
    }

    public void spawnRankUpParticle() {
        if (isClient()) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.particleEngine.createTrackingEmitter(maid, ParticleTypes.TOTEM_OF_UNDYING, 30);
            level.playLocalSound(maid.getX(), maid.getY(), maid.getZ(),
                    SoundEvents.BELL_BLOCK, maid.getSoundSource(),
                    1.0F, 1.0F, false);
            minecraft.gui.setTitle(Component.translatable("message.touhou_little_maid.gomoku.rank_up.title"));
            minecraft.gui.setSubtitle(Component.translatable("message.touhou_little_maid.gomoku.rank_up.subtitle"));
        }
    }

    public void spawnSweepAttackParticle() {
        double xOffset = -Mth.sin(maid.getYRot() * Mth.DEG_TO_RAD);
        double zOffset = Mth.cos(maid.getYRot() * Mth.DEG_TO_RAD);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    maid.getX() + xOffset,
                    maid.getY(0.5),
                    maid.getZ() + zOffset, 0,
                    xOffset, 0, zOffset, 0
            );
        }
    }

    public void spawnItemParticles(ItemStack stack, int amount) {
        for (int i = 0; i < amount; ++i) {
            Vec3 speed = new Vec3((random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);

            speed = speed.xRot(-maid.getXRot() * Mth.DEG_TO_RAD);
            speed = speed.yRot(-maid.getYRot() * Mth.DEG_TO_RAD);

            double yOffset = -random.nextFloat() * 0.6 - 0.3;
            Vec3 pos = new Vec3((random.nextFloat() - 0.5) * 0.3, yOffset, 0.6);

            pos = pos.xRot(-maid.getXRot() * Mth.DEG_TO_RAD);
            pos = pos.yRot(-maid.getYRot() * Mth.DEG_TO_RAD);
            pos = pos.add(maid.getX(), maid.getEyeY(), maid.getZ());

            ItemParticleOption option = new ItemParticleOption(ParticleTypes.ITEM, stack.getItem());
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        option, pos.x, pos.y, pos.z, 1,
                        speed.x, speed.y + 0.05, speed.z, 0.0
                );
            } else {
                level.addParticle(
                        option, pos.x, pos.y, pos.z,
                        speed.x, speed.y + 0.05, speed.z
                );
            }
        }
    }

    private boolean isClient() {
        return level.isClientSide();
    }

    public interface View {
        MaidParticleManager getParticleManager();

        default void spawnRestoreHealthParticle(int particleCount) {
            getParticleManager().spawnRestoreHealthParticle(particleCount);
        }

        default void spawnExplosionParticle() {
            getParticleManager().spawnExplosionParticle();
        }

        default void spawnBubbleParticle() {
            getParticleManager().spawnBubbleParticle();
        }

        default void spawnHeartParticle() {
            getParticleManager().spawnHeartParticle();
        }

        default void spawnRankUpParticle() {
            getParticleManager().spawnRankUpParticle();
        }
    }
}
