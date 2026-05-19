package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.world.item.component.CustomData.COMPOUND_TAG_CODEC;

public class TileEntityStatue extends BlockEntity {
    private static final String STATUE_SIZE_TAG = "StatueSize";
    private static final String CORE_BLOCK_TAG = "CoreBlock";
    private static final String CORE_BLOCK_POS_TAG = "CoreBlockPos";
    private static final String STATUE_FACING_TAG = "StatueFacing";
    private static final String ALL_BLOCKS_TAG = "AllBlocks";
    private static final String EXTRA_MAID_DATA = "ExtraMaidData";

    private Size size = Size.SMALL;
    private boolean isCoreBlock = false;
    private BlockPos coreBlockPos = BlockPos.ZERO;
    private Direction facing = Direction.NORTH;
    private List<BlockPos> allBlocks = Lists.newArrayList();
    private @Nullable CompoundTag extraMaidData = null;

    public TileEntityStatue(BlockPos blockPos, BlockState blockState) {
        super(InitBlocks.STATUE_TE.get(), blockPos, blockState);
    }

    public void setForgeData(Size size, boolean isCoreBlock, BlockPos coreBlockPos, Direction facing,
                             List<BlockPos> allBlocks, @Nullable CompoundTag extraData) {
        this.size = size;
        this.isCoreBlock = isCoreBlock;
        this.coreBlockPos = coreBlockPos;
        this.facing = facing;
        this.allBlocks = allBlocks;
        this.extraMaidData = extraData;
        refresh();
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        output.putInt(STATUE_SIZE_TAG, size.ordinal());
        output.putBoolean(CORE_BLOCK_TAG, isCoreBlock);
        output.store(CORE_BLOCK_POS_TAG, BlockPos.CODEC, coreBlockPos);
        output.store(STATUE_FACING_TAG, Direction.CODEC, facing);
        output.store(ALL_BLOCKS_TAG, BlockPos.CODEC.listOf(), allBlocks);
        if (extraMaidData != null) {
            output.store(EXTRA_MAID_DATA, COMPOUND_TAG_CODEC, extraMaidData);
        }
        super.saveAdditional(output);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        size = Size.getSizeByIndex(input.getIntOr(STATUE_SIZE_TAG, Size.SMALL.ordinal()));
        isCoreBlock = input.getBooleanOr(CORE_BLOCK_TAG, false);
        coreBlockPos = input.read(CORE_BLOCK_POS_TAG, BlockPos.CODEC).orElse(BlockPos.ZERO);
        facing = input.read(STATUE_FACING_TAG, Direction.CODEC).orElse(Direction.NORTH);
        input.read(ALL_BLOCKS_TAG, BlockPos.CODEC.listOf()).ifPresent(list -> {
            allBlocks.clear();
            allBlocks.addAll(list);
        });
        extraMaidData = input.read(EXTRA_MAID_DATA, COMPOUND_TAG_CODEC).orElse(null);
    }

    public BlockPos getWorldPosition() {
        return this.worldPosition;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return this.saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void refresh() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    public Size getSize() {
        return size;
    }

    public boolean isCoreBlock() {
        return isCoreBlock;
    }

    public BlockPos getCoreBlockPos() {
        return coreBlockPos;
    }

    public Direction getFacing() {
        return facing;
    }

    public List<BlockPos> getAllBlocks() {
        return allBlocks;
    }

    @Nullable
    public CompoundTag getExtraMaidData() {
        return extraMaidData;
    }

    public enum Size {
        // 雕像的尺寸
        TINY(0.5f, new Vec3i(1, 1, 1)),
        SMALL(1.0f, new Vec3i(1, 2, 1)),
        MIDDLE(2.0f, new Vec3i(2, 4, 2)),
        BIG(3.0f, new Vec3i(3, 6, 3));

        private final float scale;
        private final Vec3i dimension;

        Size(float scale, Vec3i dimension) {
            this.scale = scale;
            this.dimension = dimension;
        }

        public static Size getSizeByIndex(int index) {
            return Size.values()[Mth.clamp(index, 0, Size.values().length - 1)];
        }

        public float getScale() {
            return scale;
        }

        public Vec3i getDimension() {
            return dimension;
        }
    }
}
