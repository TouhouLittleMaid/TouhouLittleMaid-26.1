package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagBlock;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntitySnackCabinet;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class BlockSnackCabinet extends BaseEntityBlock {
    public static final MapCodec<BlockSnackCabinet> CODEC = simpleCodec(BlockSnackCabinet::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 2);

    public static final int TYPE_NONE = 0;
    public static final int TYPE_FULL = 1;
    public static final int TYPE_HALF = 2;

    public BlockSnackCabinet(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0F, 3.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, TYPE_NONE));
    }

    public BlockSnackCabinet(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                  BlockPos pos, Direction direction, BlockPos neighbourPos,
                                  BlockState neighborState, RandomSource random) {
        // 判断上方方块的 AABB 的 Y 最大值
        if (direction == Direction.UP) {
            if (neighborState.is(TagBlock.SNACK_CABINET_HALF)) {
                return state.setValue(TYPE, TYPE_HALF);
            }
            if (neighborState.is(TagBlock.SNACK_CABINET_FULL)) {
                return state.setValue(TYPE, TYPE_FULL);
            }
        }
        return super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighborState, random);
    }

    @Override
    public InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
        // 如果是手持方块物品并点击上方，那么不打开界面，方便放置方块
        Direction direction = hit.getDirection();
        if (direction == Direction.UP && heldItem.getItem() instanceof BlockItem) {
            return InteractionResult.PASS;
        }

        // 否则打开界面
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider provider = this.getMenuProvider(state, level, pos);
            if (provider != null) {
                player.openMenu(provider);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntitySnackCabinet cabinet) {
            cabinet.recheckOpen();
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntitySnackCabinet(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
}
