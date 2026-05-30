package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagBlock;
import com.github.tartaricacid.touhoulittlemaid.util.TeleportHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class MaidTeleportManager {
    /**
     * 女仆传送到主人处的最大尝试次数
     */
    private static final int MAX_TELEPORT_ATTEMPTS_TIMES = 10;

    private final EntityMaid maid;

    public MaidTeleportManager(EntityMaid entityMaid) {
        maid = entityMaid;
    }

    public boolean teleportToOwner(LivingEntity owner) {
        BlockPos blockPos = owner.blockPosition();
        for (int i = 0; i < MAX_TELEPORT_ATTEMPTS_TIMES; ++i) {
            int x = randomIntInclusive(maid.getRandom(), -3, 3);
            int y = randomIntInclusive(maid.getRandom(), -1, 1);
            int z = randomIntInclusive(maid.getRandom(), -3, 3);
            if (maybeTeleportTo(owner, blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 女仆不能穿过传送门，故接触传送门时，会瞬移走
     */
    void handlePortal() {
        if (maid.level instanceof ServerLevel && !maid.isRemoved() && maid.portalProcess != null) {
            if (TeleportHelper.teleport(maid)) {
                maid.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 1, true, false));
                maid.setPortalCooldown();
            }
        }
    }

    private int randomIntInclusive(RandomSource random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private boolean canTeleportTo(BlockPos pos) {
        // 先检查下方方块是否在黑名单中
        BlockState blockState = maid.level().getBlockState(pos.below());
        if (blockState.is(TagBlock.MAID_AVOID_BLOCK)) {
            return false;
        }

        // 再检查路径节点类型和碰撞箱
        PathType pathNodeType = WalkNodeEvaluator.getPathTypeStatic(maid, pos);
        if (pathNodeType == PathType.WALKABLE || pathNodeType == PathType.WATER) {
            BlockPos blockPos = pos.subtract(maid.blockPosition());
            return maid.level().noCollision(maid, maid.getBoundingBox().move(blockPos));
        }
        return false;
    }

    private boolean teleportTooClosed(LivingEntity owner, int x, int z) {
        return Math.abs(x - owner.getX()) < 2 && Math.abs(z - owner.getZ()) < 2;
    }

    private boolean maybeTeleportTo(LivingEntity owner, int x, int y, int z) {
        if (teleportTooClosed(owner, x, z)) {
            return false;
        } else if (!canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            maid.moveOrInterpolateTo(new Vec3(x + 0.5, y, z + 0.5), maid.getYRot(), maid.getXRot());
            maid.getNavigation().stop();
            maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            maid.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
            maid.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            maid.getBrain().eraseMemory(MemoryModuleType.PATH);
            return true;
        }
    }

    public interface View {
        MaidTeleportManager getTeleportManager();

        default boolean teleportToOwner(LivingEntity owner) {
            return getTeleportManager().teleportToOwner(owner);
        }
    }
}

