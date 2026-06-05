package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityBookshelf extends BlockEntityJoy {
    public BlockEntityBookshelf(BlockPos pos, BlockState blockState) {
        super(InitBlocks.BOOKSHELF_BE.get(), pos, blockState);
    }
}