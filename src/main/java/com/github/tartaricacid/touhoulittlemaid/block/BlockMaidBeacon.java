package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.block.properties.BeaconPosition;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemMaidBeacon;
import com.github.tartaricacid.touhoulittlemaid.network.message.OpenBeaconGuiPackage;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityMaidBeacon;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

public class BlockMaidBeacon extends BaseEntityBlock {
    public static final EnumProperty<BeaconPosition> POSITION = EnumProperty.create("position", BeaconPosition.class);

    private static final VoxelShape UP_AABB = Block.box(3, 1, 3, 13, 16, 13);
    private static final VoxelShape DOWN_AABB = Block.box(6.5, 0, 6.5, 9.5, 26, 9.5);

    private static final MapCodec<BlockMaidBeacon> CODEC = simpleCodec(BlockMaidBeacon::new);

    public BlockMaidBeacon(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .sound(SoundType.WOOD)
                .strength(2, 2)
                .noOcclusion()
                .lightLevel(s -> s.getValue(POSITION) == BeaconPosition.DOWN ? 0 : 15));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POSITION, BeaconPosition.DOWN));
    }

    public BlockMaidBeacon(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(BlockMaidBeacon.POSITION) != BeaconPosition.DOWN) {
            return new BlockEntityMaidBeacon(pos, state);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(
                type, InitBlocks.MAID_BEACON_BE.get(),
                BlockEntityMaidBeacon::serverTick
        );
    }

    @Override
    public InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level worldIn, BlockPos pos,
                                       Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof BlockEntityMaidBeacon) {
            if (!worldIn.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new OpenBeaconGuiPackage(pos));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(itemStack, state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        BeaconPosition position = state.getValue(POSITION);
        return position == BeaconPosition.DOWN ? DOWN_AABB : UP_AABB;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader worldIn, ScheduledTickAccess ticks,
                                  BlockPos pos, Direction facing, BlockPos neighbourPos,
                                  BlockState neighbourState, RandomSource random) {
        if (facing.getAxis() == Direction.Axis.Y) {
            BeaconPosition position = stateIn.getValue(POSITION);
            if (position == BeaconPosition.DOWN && facing == Direction.UP) {
                if (!neighbourState.is(this) || neighbourState.getValue(POSITION) == BeaconPosition.DOWN) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
            if (position != BeaconPosition.DOWN && facing == Direction.DOWN) {
                if (!neighbourState.is(this)
                    || neighbourState.getValue(POSITION) == BeaconPosition.UP_W_E
                    || neighbourState.getValue(POSITION) == BeaconPosition.UP_N_S
                ) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }
        return super.updateShape(stateIn, worldIn, ticks, pos, facing, neighbourPos, neighbourState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        if (worldIn.isClientSide() || !player.isCreative()) {
            return super.playerWillDestroy(worldIn, pos, state, player);
        }

        BeaconPosition position = state.getValue(POSITION);
        if (position != BeaconPosition.DOWN) {
            BlockPos belowPos = pos.below();
            BlockState belowState = worldIn.getBlockState(belowPos);
            if (belowState.is(this) && belowState.getValue(POSITION) == BeaconPosition.DOWN) {
                worldIn.setBlock(belowPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                worldIn.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, belowPos, Block.getId(belowState));
            }
        }

        return super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return new ItemStack(InitItems.MAID_BEACON.get());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level world = context.getLevel();
        int maxHeight = world.getMaxY() - 1;
        if (blockpos.getY() < maxHeight && world.getBlockState(blockpos.above()).canBeReplaced(context)) {
            return super.getStateForPlacement(context);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Direction facing = placer == null ? Direction.NORTH : placer.getDirection();

        BlockState stateUp;
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            stateUp = this.defaultBlockState().setValue(POSITION, BeaconPosition.UP_N_S);
        } else {
            stateUp = this.defaultBlockState().setValue(POSITION, BeaconPosition.UP_W_E);
        }

        worldIn.setBlock(pos.above(), stateUp, Block.UPDATE_ALL);
        BlockEntity te = worldIn.getBlockEntity(pos.above());
        if (te instanceof BlockEntityMaidBeacon beacon) {
            ItemMaidBeacon.itemStackToBlockEntity(stack, beacon);
            beacon.refresh();
        }
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
        switch (direction) {
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                if (state.getValue(POSITION) == BeaconPosition.UP_N_S) {
                    return state.setValue(POSITION, BeaconPosition.UP_W_E);
                }
                if (state.getValue(POSITION) == BeaconPosition.UP_W_E) {
                    return state.setValue(POSITION, BeaconPosition.UP_N_S);
                }
                return state;
            default:
                return state;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POSITION);
    }
}
