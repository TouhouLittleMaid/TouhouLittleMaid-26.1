package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityKeyboard extends TileEntityJoy {
    public TileEntityKeyboard(BlockPos pos, BlockState blockState) {
        super(InitBlocks.KEYBOARD_TE.get(), pos, blockState);
    }
}