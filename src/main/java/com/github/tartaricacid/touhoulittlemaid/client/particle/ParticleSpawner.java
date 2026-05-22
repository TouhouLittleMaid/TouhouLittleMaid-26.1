package com.github.tartaricacid.touhoulittlemaid.client.particle;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector3d;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ParticleSpawner {
    private static final Cache<String, ParticleOptions> PARTICLE_OPTIONS_CACHE = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build();

    public static boolean evalSpawnParticle(ExecutionContext<IContext<Entity>> context, Function.ArgumentCollection arguments, boolean absPos)
            throws ExecutionException, CommandSyntaxException {
        String id = arguments.getAsString(context, 0);
        if (StringUtils.isBlank(id)) {
            return false;
        }

        Vector3d pos = new Vector3d(0, 0, 0);
        Vector3d delta = new Vector3d(0, 0, 0);
        double particleSpeed = 0f;
        int count = 0;
        int particleLifeTime = 20;

        int size = arguments.size();
        if (size > 1) {
            pos.x = arguments.getAsDouble(context, 1);
        }
        if (size > 2) {
            pos.y = arguments.getAsDouble(context, 2);
        }
        if (size > 3) {
            pos.z = arguments.getAsDouble(context, 3);
        }
        if (size > 4) {
            delta.x = arguments.getAsDouble(context, 4);
        }
        if (size > 5) {
            delta.y = arguments.getAsDouble(context, 5);
        }
        if (size > 6) {
            delta.z = arguments.getAsDouble(context, 6);
        }
        if (size > 7) {
            particleSpeed = arguments.getAsDouble(context, 7);
        }
        if (size > 8) {
            count = Math.max(arguments.getAsInt(context, 8), 0);
        }
        if (size > 9) {
            particleLifeTime = Math.max(arguments.getAsInt(context, 9), 1);
        }
        spawnParticle(context.entity().entity(), id, pos, delta, particleSpeed, count, particleLifeTime, absPos, context.entity().random());
        return true;
    }

    @SuppressWarnings("all")
    private static void spawnParticle(Entity entity, String id, Vector3d pos, Vector3d delta,
                                      double particleSpeed, int count, int particleLifeTime, boolean absPos, RandomSource random)
            throws CommandSyntaxException, ExecutionException {
        ParticleOptions particleOptions = PARTICLE_OPTIONS_CACHE.get(id, () ->
                ParticleArgument.readParticle(new StringReader(id), entity.level().registryAccess()));
        if (particleOptions == null) {
            return;
        }
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        if (count == 0) {
            // 单个粒子准确在指定位置生成
            Vec3 offset = new Vec3(pos.x(), pos.y(), pos.z());
            if (!absPos) {
                if (entity instanceof Player player) {
                    offset = offset.yRot(-player.yBodyRot * Mth.DEG_TO_RAD);
                } else {
                    offset = offset.yRot(-entity.getYRot() * Mth.DEG_TO_RAD);
                }
            }

            double xPos = entity.getX() + offset.x();
            double yPos = entity.getY() + offset.y();
            double zPos = entity.getZ() + offset.z();

            double xSpeed = particleSpeed * delta.x();
            double ySpeed = particleSpeed * delta.y();
            double zSpeed = particleSpeed * delta.z();

            Minecraft.getInstance().execute(() -> {
                Particle result = particleEngine.createParticle(particleOptions, xPos, yPos, zPos, xSpeed, ySpeed, zSpeed);
                if (result != null) {
                    result.setLifetime(particleLifeTime);
                }
            });
        } else {
            // 多个粒子就需要加点随机了
            for (int i = 0; i < count; ++i) {
                createParticle(entity, pos, delta, particleSpeed, particleLifeTime, particleEngine, particleOptions, absPos, random);
            }
        }
    }

    private static void createParticle(Entity entity, Vector3d pos, Vector3d delta, double particleSpeed, int particleLifeTime,
                                       ParticleEngine particleEngine, ParticleOptions particleOptions, boolean absPos, RandomSource random) {
        double offsetX = random.nextGaussian() * delta.x();
        double offsetY = random.nextGaussian() * delta.y();
        double offsetZ = random.nextGaussian() * delta.z();

        double xSpeed = random.nextGaussian() * particleSpeed;
        double ySpeed = random.nextGaussian() * particleSpeed;
        double zSpeed = random.nextGaussian() * particleSpeed;

        Vec3 offset = new Vec3(pos.x() + offsetX, pos.y() + offsetY, pos.z() + offsetZ);
        if (!absPos) {
            offset = offset.yRot(-entity.getYRot() * Mth.DEG_TO_RAD);
        }

        double posX = entity.getX() + offset.x();
        double posY = entity.getY() + offset.y();
        double posZ = entity.getZ() + offset.z();

        Minecraft.getInstance().execute(() -> {
            Particle result = particleEngine.createParticle(particleOptions, posX, posY, posZ, xSpeed, ySpeed, zSpeed);
            if (result != null) {
                result.setLifetime(particleLifeTime);
            }
        });
    }
}
