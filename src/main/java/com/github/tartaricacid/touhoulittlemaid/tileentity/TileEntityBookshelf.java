package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityBookshelf extends TileEntityJoy {
    public TileEntityBookshelf(BlockPos pos, BlockState blockState) {
        super(InitBlocks.BOOKSHELF_TE.get(), pos, blockState);
    }
}