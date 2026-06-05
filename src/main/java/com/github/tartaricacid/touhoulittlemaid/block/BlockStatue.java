package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityGarageKit;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityStatue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BlockStatue extends Block implements EntityBlock {
    public static final BooleanProperty IS_TINY = BooleanProperty.create("is_tiny");

    public BlockStatue(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .sound(SoundType.MUD)
                .strength(1, 2)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(IS_TINY, false));
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        if (worldIn.isClientSide()) {
            return super.playerWillDestroy(worldIn, pos, state, player);
        }
        this.getStatue(worldIn, pos).ifPresent(statue -> {
            this.restoreClayBlock(worldIn, pos, statue);
            if (!player.isCreative()) {
                Block.popResource(worldIn, pos, new ItemStack(Blocks.CLAY));
            }
        });
        return super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, ServerLevel world, BlockPos pos, Explosion explosion) {
        this.getStatue(world, pos).ifPresent(statue ->
                this.restoreClayBlock(world, pos, statue)
        );
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_TINY);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityStatue(pos, state);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    private Optional<BlockEntityStatue> getStatue(BlockGetter world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BlockEntityStatue statue) {
            return Optional.of(statue);
        }
        return Optional.empty();
    }

    private void restoreClayBlock(Level worldIn, BlockPos pos, BlockEntityStatue statue) {
        List<BlockPos> posList = statue.getAllBlocks();
        for (BlockPos storagePos : posList) {
            if (storagePos.equals(pos)) {
                continue;
            }
            getStatue(worldIn, storagePos).ifPresent(_ -> {
                BlockState clay = Blocks.CLAY.defaultBlockState();
                worldIn.setBlock(storagePos, clay, Block.UPDATE_ALL);
            });
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(IS_TINY);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.getBlockState(pos.below()).is(Blocks.FIRE)) {
            return;
        }
        getStatue(level, pos).ifPresent(statue -> {
            level.setBlockAndUpdate(pos, InitBlocks.GARAGE_KIT.get().defaultBlockState());
            level.levelEvent(LevelEvent.SOUND_EXTINGUISH_FIRE, pos, 0);
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BlockEntityGarageKit kit && statue.getExtraMaidData() != null) {
                kit.setData(statue.getFacing(), statue.getExtraMaidData());
            }
        });
    }
}
