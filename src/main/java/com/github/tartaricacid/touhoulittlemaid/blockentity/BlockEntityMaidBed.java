package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BlockEntityMaidBed extends BlockEntityBase {
    private static final String COLOR_TAG = "BedColor";
    private DyeColor color = DyeColor.PINK;

    public BlockEntityMaidBed(BlockPos blockPos, BlockState blockState) {
        super(InitBlocks.MAID_BED_BE.get(), blockPos, blockState);
    }

    public void setColor(DyeColor color) {
        this.color = color;
        this.refresh();
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(COLOR_TAG, DyeColor.CODEC, this.color);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.color = input.read(COLOR_TAG, DyeColor.CODEC).orElse(DyeColor.PINK);
    }

    public DyeColor getColor() {
        return color;
    }
}
