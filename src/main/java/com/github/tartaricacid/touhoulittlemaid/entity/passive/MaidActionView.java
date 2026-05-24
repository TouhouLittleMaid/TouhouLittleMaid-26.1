package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class MaidActionView {

    // 女仆传送到主人处的最大尝试次数
    private static final int MAX_TELEPORT_ATTEMPTS_TIMES = 10;

    private final EntityMaid maid;

    public MaidActionView(EntityMaid entityMaid) {
        maid = entityMaid;
    }

    BlockHitResult getBlockRayTraceResult(BlockPos pos, Direction direction) {
        return new BlockHitResult(
                new Vec3((double) pos.getX() + 0.5D + (double) direction.getStepX() * 0.5D,
                        (double) pos.getY() + 0.5D + (double) direction.getStepY() * 0.5D,
                        (double) pos.getZ() + 0.5D + (double) direction.getStepZ() * 0.5D),
                direction, pos, false);
    }

    public boolean canDestroyBlock(BlockPos pos) {
        BlockState state = maid.level.getBlockState(pos);
        return state.getBlock().canEntityDestroy(state, maid.level, pos, maid) && net.neoforged.neoforge.event.EventHooks.onEntityDestroyBlock(maid, pos, state);
    }

    public boolean canPlaceBlock(BlockPos pos) {
        BlockState oldState = maid.level.getBlockState(pos);
        return oldState.canBeReplaced();
    }

    public boolean placeItemBlock(InteractionHand hand, BlockPos placePos, Direction direction, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            return blockItem.place(new BlockPlaceContext(maid.level, null, hand, stack,
                    getBlockRayTraceResult(placePos, direction))).consumesAction();
        }
        return false;
    }

    public boolean destroyBlock(Level level, BlockPos blockPos, boolean dropBlock, @Nullable Entity entity) {
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.isAir()) {
            return false;
        } else {
            FluidState fluidState = level.getFluidState(blockPos);
            if (!(blockState.getBlock() instanceof BaseFireBlock)) {
                level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
            }
            if (dropBlock) {
                BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
                maid.itemManager.dropResourcesToMaidInv(blockState, level, blockPos, blockEntity, ItemStack.EMPTY);
            }
            boolean setResult = level.setBlock(blockPos, fluidState.createLegacyBlock(), Block.UPDATE_ALL);
            if (setResult) {
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(entity, blockState));
            }
            return setResult;
        }
    }

    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        return canDestroyBlock(pos) && destroyBlock(maid.level, pos, dropBlock, maid);
    }

    int randomIntInclusive(RandomSource random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    boolean canTeleportTo(BlockPos pos) {
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

    boolean teleportTooClosed(LivingEntity owner, int x, int z) {
        return Math.abs(x - owner.getX()) < 2 && Math.abs(z - owner.getZ()) < 2;
    }

    boolean maybeTeleportTo(LivingEntity owner, int x, int y, int z) {
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

    public interface View {

        MaidActionView getActionView();

        default boolean canDestroyBlock(BlockPos pos) {
            return getActionView().canDestroyBlock(pos);
        }

        default boolean canPlaceBlock(BlockPos pos) {
            return getActionView().canPlaceBlock(pos);
        }

        default boolean placeItemBlock(InteractionHand hand, BlockPos placePos, Direction direction, ItemStack stack) {
            return getActionView().placeItemBlock(hand, placePos, direction, stack);
        }

        default boolean destroyBlock(Level level, BlockPos blockPos, boolean dropBlock, @Nullable Entity entity) {
            return getActionView().destroyBlock(level, blockPos, dropBlock, entity);
        }

        default boolean destroyBlock(BlockPos pos, boolean dropBlock) {
            return getActionView().destroyBlock(pos, dropBlock);
        }

        default boolean teleportToOwner(LivingEntity owner) {
            return getActionView().teleportToOwner(owner);
        }

        default boolean placeItemBlock(BlockPos placePos, Direction direction, ItemStack stack) {
            return placeItemBlock(InteractionHand.MAIN_HAND, placePos, direction, stack);
        }

        default boolean placeItemBlock(BlockPos placePos, ItemStack stack) {
            return placeItemBlock(placePos, Direction.UP, stack);
        }

        default boolean destroyBlock(BlockPos pos) {
            return destroyBlock(pos, true);
        }

    }

}
