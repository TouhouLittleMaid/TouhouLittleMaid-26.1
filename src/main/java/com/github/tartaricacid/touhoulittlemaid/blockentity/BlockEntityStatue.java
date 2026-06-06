package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

import static net.minecraft.world.item.component.CustomData.COMPOUND_TAG_CODEC;

public class BlockEntityStatue extends BlockEntityBase {
    private static final String STATUE_SIZE_TAG = "StatueSize";
    private static final String CORE_BLOCK_TAG = "CoreBlock";
    private static final String CORE_BLOCK_POS_TAG = "CoreBlockPos";
    private static final String ALL_BLOCKS_TAG = "AllBlocks";
    private static final String EXTRA_MAID_DATA = "ExtraMaidData";

    private Size size = Size.SMALL;
    private boolean isCoreBlock = false;
    private BlockPos coreBlockPos = BlockPos.ZERO;
    private List<BlockPos> allBlocks = Lists.newArrayList();
    private @Nullable CompoundTag extraMaidData = null;

    public BlockEntityStatue(BlockPos blockPos, BlockState blockState) {
        super(InitBlocks.STATUE_BE.get(), blockPos, blockState);
    }

    public void setAllData(Size size, boolean isCoreBlock, BlockPos coreBlockPos,
                           List<BlockPos> allBlocks, @Nullable CompoundTag extraData) {
        this.size = size;
        this.isCoreBlock = isCoreBlock;
        this.coreBlockPos = coreBlockPos;
        this.allBlocks = allBlocks;
        this.extraMaidData = extraData;
        this.refresh();
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(STATUE_SIZE_TAG, Size.CODEC, size);
        output.putBoolean(CORE_BLOCK_TAG, isCoreBlock);
        output.store(CORE_BLOCK_POS_TAG, BlockPos.CODEC, coreBlockPos);
        output.store(ALL_BLOCKS_TAG, BlockPos.CODEC.listOf(), allBlocks);
        if (extraMaidData != null) {
            output.store(EXTRA_MAID_DATA, COMPOUND_TAG_CODEC, extraMaidData);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.size = input.read(STATUE_SIZE_TAG, Size.CODEC).orElse(Size.SMALL);
        this.isCoreBlock = input.getBooleanOr(CORE_BLOCK_TAG, false);
        this.coreBlockPos = input.read(CORE_BLOCK_POS_TAG, BlockPos.CODEC).orElse(BlockPos.ZERO);
        input.read(ALL_BLOCKS_TAG, BlockPos.CODEC.listOf()).ifPresent(list -> {
            this.allBlocks.clear();
            this.allBlocks.addAll(list);
        });
        this.extraMaidData = input.read(EXTRA_MAID_DATA, COMPOUND_TAG_CODEC).orElse(null);
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

    public List<BlockPos> getAllBlocks() {
        return allBlocks;
    }

    @Nullable
    public CompoundTag getExtraMaidData() {
        return extraMaidData;
    }

    public enum Size implements StringRepresentable {
        // 雕像的尺寸
        TINY(0.5f, new Vec3i(1, 1, 1)),
        SMALL(1.0f, new Vec3i(1, 2, 1)),
        MIDDLE(2.0f, new Vec3i(2, 4, 2)),
        BIG(3.0f, new Vec3i(3, 6, 3));

        public static final Codec<Size> CODEC = StringRepresentable.fromEnum(Size::values);

        private final float scale;
        private final Vec3i dimension;

        Size(float scale, Vec3i dimension) {
            this.scale = scale;
            this.dimension = dimension;
        }

        public float getScale() {
            return scale;
        }

        public Vec3i getDimension() {
            return dimension;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ENGLISH);
        }
    }
}
