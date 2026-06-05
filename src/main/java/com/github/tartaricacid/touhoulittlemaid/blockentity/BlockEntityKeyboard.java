package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityKeyboard extends BlockEntityJoy {
    public BlockEntityKeyboard(BlockPos pos, BlockState blockState) {
        super(InitBlocks.KEYBOARD_BE.get(), pos, blockState);
    }
}