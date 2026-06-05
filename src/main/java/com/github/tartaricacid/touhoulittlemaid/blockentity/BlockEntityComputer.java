package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityComputer extends BlockEntityJoy {
    public BlockEntityComputer(BlockPos pos, BlockState blockState) {
        super(InitBlocks.COMPUTER_BE.get(), pos, blockState);
    }
}