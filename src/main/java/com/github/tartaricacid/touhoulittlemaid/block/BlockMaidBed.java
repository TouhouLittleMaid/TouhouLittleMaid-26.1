package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityMaidBed;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class BlockMaidBed extends HorizontalDirectionalBlock implements EntityBlock {
    public static final Map<DyeColor, BlockMaidBed> COLOR_TO_BLOCK = new EnumMap<>(DyeColor.class);

    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    private static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
    private static final MapCodec<BlockMaidBed> CODEC = simpleCodec(p -> new BlockMaidBed(p, DyeColor.PINK));

    private final DyeColor color;

    public BlockMaidBed(Identifier id, DyeColor color) {
        super(BlockBehaviour.Properties.of()
                .mapColor(color)
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .sound(SoundType.WOOD)
                .strength(0.2F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, BedPart.FOOT)
                .setValue(OCCUPIED, false));
        this.color = color;
        COLOR_TO_BLOCK.put(color, this);
    }

    public BlockMaidBed(Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
        COLOR_TO_BLOCK.put(color, this);
    }

    /**
     * 根据颜色获取对应的方块实例
     */
    @Nullable
    public static BlockMaidBed getByColor(DyeColor color) {
        return COLOR_TO_BLOCK.get(color);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return BASE;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks,
                                  BlockPos pos, Direction facing, BlockPos neighbourPos,
                                  BlockState neighbourState, RandomSource random) {
        if (facing == getNeighbourDirection(stateIn.getValue(PART), stateIn.getValue(FACING))) {
            return neighbourState.is(this) && neighbourState.getValue(PART) != stateIn.getValue(PART)
                    ? stateIn.setValue(OCCUPIED, neighbourState.getValue(OCCUPIED))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, level, ticks, pos, facing, neighbourPos, neighbourState, random);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        if (worldIn.isClientSide()) {
            return super.playerWillDestroy(worldIn, pos, state, player);
        }
        if (!player.isCreative()) {
            return super.playerWillDestroy(worldIn, pos, state, player);
        }
        BedPart bedpart = state.getValue(PART);
        if (bedpart != BedPart.FOOT) {
            return super.playerWillDestroy(worldIn, pos, state, player);
        }
        BlockPos blockpos = pos.relative(getNeighbourDirection(bedpart, state.getValue(FACING)));
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.getBlock() == this && blockstate.getValue(PART) == BedPart.HEAD) {
            worldIn.setBlock(blockpos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            worldIn.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockpos, Block.getId(blockstate));
        }
        return super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos relativePos = context.getClickedPos().relative(direction);
        BlockState blockState = context.getLevel().getBlockState(relativePos);
        return blockState.canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, direction) : null;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide()) {
            return;
        }

        BlockPos headPos = pos.relative(state.getValue(FACING));
        worldIn.setBlock(headPos, state.setValue(PART, BedPart.HEAD), Block.UPDATE_ALL);
        worldIn.updateNeighborsAt(pos, Blocks.AIR);
        state.updateNeighbourShapes(worldIn, pos, Block.UPDATE_ALL);

        if (worldIn.getBlockEntity(headPos) instanceof BlockEntityMaidBed bed) {
            bed.setColor(this.color);
        }
    }

    @Override
    public void fallOn(Level worldIn, BlockState blockState, BlockPos pos, Entity entityIn, double fallDistance) {
        super.fallOn(worldIn, blockState, pos, entityIn, fallDistance * 0.5f);
    }

    @Override
    public void updateEntityMovementAfterFallOn(BlockGetter worldIn, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityMovementAfterFallOn(worldIn, entity);
        } else {
            Vec3 movement = entity.getDeltaMovement();
            if (movement.y < 0) {
                double modulus = entity instanceof LivingEntity ? 1.0 : 0.8;
                entity.setDeltaMovement(movement.x, -movement.y * 0.66 * modulus, movement.z);
            }
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    protected long getSeed(BlockState state, BlockPos pos) {
        BlockPos sourcePos = pos.relative(state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(sourcePos.getX(), pos.getY(), sourcePos.getZ());
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter world, BlockPos pos, LivingEntity sleeper) {
        if (sleeper instanceof EntityMaid) {
            return true;
        }
        return super.isBed(state, world, pos, sleeper);
    }

    private Direction getNeighbourDirection(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART) == BedPart.HEAD) {
            return new BlockEntityMaidBed(pos, state);
        }
        return null;
    }

    public DyeColor getColor() {
        return color;
    }
}
