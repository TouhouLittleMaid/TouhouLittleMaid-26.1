package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidPlaySoundEvent;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

public class MaidEffectsManager {

    private final EntityMaid maid;
    private final RandomSource random;

    private int pickupSoundCount = 5;

    public MaidEffectsManager(EntityMaid entityMaid) {
        maid = entityMaid;
        random = maid.getRandom();
    }

    void spawnPortalParticle() {
        if (maid.level.isClientSide() && maid.getIsInvulnerable() && MiscConfig.INVULNERABLE_PARTICLE_EFFECT.get() && maid.getOwner() != null) {
            maid.level.addParticle(ParticleTypes.PORTAL,
                    maid.getX() + (random.nextDouble() - 0.5D) * (double) maid.getBbWidth(),
                    maid.getY() + random.nextDouble() * (double) maid.getBbHeight() - 0.25D,
                    maid.getZ() + (random.nextDouble() - 0.5D) * (double) maid.getBbWidth(),
                    (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                    (random.nextDouble() - 0.5D) * 2.0D);
        }
    }

    public void spawnRestoreHealthParticle(int particleCount) {
        if (maid.level.isClientSide()) {
            for (int i = 0; i < particleCount; ++i) {
                double xRandom = random.nextGaussian() * 0.02D;
                double yRandom = random.nextGaussian() * 0.02D;
                double zRandom = random.nextGaussian() * 0.02D;

                maid.level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.9f, 0.1f, 0.1f),
                        maid.getX() + (double) (random.nextFloat() * maid.getBbWidth() * 2.0F) - (double) maid.getBbWidth() - xRandom * 10.0D,
                        maid.getY() + (double) (random.nextFloat() * maid.getBbHeight()) - yRandom * 10.0D,
                        maid.getZ() + (double) (random.nextFloat() * maid.getBbWidth() * 2.0F) - (double) maid.getBbWidth() - zRandom * 10.0D,
                        0, 0, 0);
            }
        }
    }

    public void spawnExplosionParticle() {
        if (maid.level.isClientSide()) {
            for (int i = 0; i < 20; ++i) {
                float mx = (random.nextFloat() - 0.5F) * 0.02F;
                float my = (random.nextFloat() - 0.5F) * 0.02F;
                float mz = (random.nextFloat() - 0.5F) * 0.02F;
                maid.level.addParticle(ParticleTypes.CLOUD,
                        maid.getX() + random.nextFloat() - 0.5F,
                        maid.getY() + random.nextFloat() - 0.5F,
                        maid.getZ() + random.nextFloat() - 0.5F,
                        mx, my, mz);
            }
        }
    }

    public void spawnBubbleParticle() {
        if (maid.level.isClientSide()) {
            for (int i = 0; i < 8; ++i) {
                double offsetX = 2 * random.nextDouble() - 1;
                double offsetY = random.nextDouble() / 2;
                double offsetZ = 2 * random.nextDouble() - 1;
                maid.level.addParticle(ParticleTypes.BUBBLE, maid.getX() + offsetX, maid.getY() + offsetY, maid.getZ() + offsetZ,
                        0, 0.1, 0);
            }
        }
    }

    public void spawnHeartParticle() {
        if (maid.level.isClientSide()) {
            for (int i = 0; i < 8; ++i) {
                double offsetX = random.nextGaussian() * 0.02;
                double offsetY = random.nextGaussian() * 0.02;
                double offsetZ = random.nextGaussian() * 0.02;
                maid.level.addParticle(ParticleTypes.HEART, maid.getRandomX(1.0), maid.getRandomY() + 0.5, maid.getRandomZ(1.0), offsetX, offsetY, offsetZ);
            }
        }
    }

    public void spawnRankUpParticle() {
        if (maid.level.isClientSide()) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.particleEngine.createTrackingEmitter(maid, ParticleTypes.TOTEM_OF_UNDYING, 30);
            maid.level.playLocalSound(maid.getX(), maid.getY(), maid.getZ(), SoundEvents.BELL_BLOCK, maid.getSoundSource(), 1.0F, 1.0F, false);
            minecraft.gui.setTitle(Component.translatable("message.touhou_little_maid.gomoku.rank_up.title"));
            minecraft.gui.setSubtitle(Component.translatable("message.touhou_little_maid.gomoku.rank_up.subtitle"));
        }
    }

    void spawnSweepAttackParticle() {
        double xOffset = -Mth.sin(maid.getYRot() * ((float) Math.PI / 180F));
        double zOffset = Mth.cos(maid.getYRot() * ((float) Math.PI / 180F));
        if (maid.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    maid.getX() + xOffset, maid.getY(0.5),
                    maid.getZ() + zOffset, 0, xOffset, 0, zOffset, 0);
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
            if (maid.level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(option, pos.x, pos.y, pos.z, 1, speed.x, speed.y + 0.05, speed.z, 0.0);
            } else {
                maid.level.addParticle(option, pos.x, pos.y, pos.z, speed.x, speed.y + 0.05, speed.z);
            }
        }
    }

    public boolean mayPlaySound() {
        return !NeoForge.EVENT_BUS.post(new MaidPlaySoundEvent(maid)).isCanceled();
    }

    public void tryPlayMaidPickupSound() {
        if (mayPlaySound()) {
            pickupSoundCount--;
            if (pickupSoundCount == 0) {
                maid.playSound(InitSounds.MAID_ITEM_GET.get(), 1, 1);
                pickupSoundCount = 5;
            }
        }
    }

    public interface View {

        MaidEffectsManager getEffectsManager();

        default void spawnRestoreHealthParticle(int particleCount) {
            getEffectsManager().spawnRestoreHealthParticle(particleCount);
        }

        default void spawnExplosionParticle() {
            getEffectsManager().spawnExplosionParticle();
        }

        default void spawnBubbleParticle() {
            getEffectsManager().spawnBubbleParticle();
        }

        default void spawnHeartParticle() {
            getEffectsManager().spawnHeartParticle();
        }

        default void spawnRankUpParticle() {
            getEffectsManager().spawnRankUpParticle();
        }

        default boolean mayPlaySound() {
            return getEffectsManager().mayPlaySound();
        }

        default void tryPlayMaidPickupSound() {
            getEffectsManager().tryPlayMaidPickupSound();
        }

    }

}
