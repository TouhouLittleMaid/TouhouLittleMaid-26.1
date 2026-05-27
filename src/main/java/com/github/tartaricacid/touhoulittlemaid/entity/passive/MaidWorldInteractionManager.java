package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;

/**
 * 世界交互管理器，主要提供方块放置和破坏的相关方法
 */
public class MaidWorldInteractionManager {
    private final EntityMaid maid;

    public MaidWorldInteractionManager(EntityMaid entityMaid) {
        maid = entityMaid;
    }

    public boolean canDestroyBlock(BlockPos pos) {
        BlockState state = maid.level.getBlockState(pos);
        return state.getBlock().canEntityDestroy(state, maid.level, pos, maid)
               && EventHooks.onEntityDestroyBlock(maid, pos, state);
    }

    public boolean canPlaceBlock(BlockPos pos) {
        BlockState oldState = maid.level.getBlockState(pos);
        return oldState.canBeReplaced();
    }

    public boolean placeItemBlock(InteractionHand hand, BlockPos placePos, Direction direction, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            BlockHitResult traceResult = getBlockRayTraceResult(placePos, direction);
            BlockPlaceContext context = new BlockPlaceContext(maid.level, null, hand, stack, traceResult);
            return blockItem.place(context).consumesAction();
        }
        return false;
    }

    public boolean destroyBlock(Level level, BlockPos blockPos, boolean dropBlock, @Nullable Entity entity) {
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.isAir()) {
            return false;
        }
        if (!(blockState.getBlock() instanceof BaseFireBlock)) {
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
        }
        if (dropBlock) {
            BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            maid.dropResourcesToMaidInv(blockState, level, blockPos, blockEntity, ItemStack.EMPTY);
        }
        FluidState fluidState = level.getFluidState(blockPos);
        boolean setResult = level.setBlock(blockPos, fluidState.createLegacyBlock(), Block.UPDATE_ALL);
        if (setResult) {
            level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(entity, blockState));
        }
        return setResult;
    }

    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        return canDestroyBlock(pos) && destroyBlock(maid.level, pos, dropBlock, maid);
    }

    private BlockHitResult getBlockRayTraceResult(BlockPos pos, Direction direction) {
        return new BlockHitResult(new Vec3(
                pos.getX() + 0.5 + direction.getStepX() * 0.5,
                pos.getY() + 0.5 + direction.getStepY() * 0.5,
                pos.getZ() + 0.5 + direction.getStepZ() * 0.5
        ), direction, pos, false);
    }

    public interface View {
        MaidWorldInteractionManager getWorldInteractionManager();

        default boolean canDestroyBlock(BlockPos pos) {
            return getWorldInteractionManager().canDestroyBlock(pos);
        }

        default boolean canPlaceBlock(BlockPos pos) {
            return getWorldInteractionManager().canPlaceBlock(pos);
        }

        default boolean placeItemBlock(InteractionHand hand, BlockPos placePos, Direction direction, ItemStack stack) {
            return getWorldInteractionManager().placeItemBlock(hand, placePos, direction, stack);
        }

        default boolean destroyBlock(Level level, BlockPos blockPos, boolean dropBlock, @Nullable Entity entity) {
            return getWorldInteractionManager().destroyBlock(level, blockPos, dropBlock, entity);
        }

        default boolean destroyBlock(BlockPos pos, boolean dropBlock) {
            return getWorldInteractionManager().destroyBlock(pos, dropBlock);
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

