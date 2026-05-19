package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityComputer extends TileEntityJoy {
    public TileEntityComputer(BlockPos pos, BlockState blockState) {
        super(InitBlocks.COMPUTER_TE.get(), pos, blockState);
    }
}