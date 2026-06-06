package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static net.minecraft.world.item.component.CustomData.COMPOUND_TAG_CODEC;

public class BlockEntityGarageKit extends BlockEntityBase {
    private static final String EXTRA_DATA = "ExtraData";

    private CompoundTag extraData = new CompoundTag();

    public BlockEntityGarageKit(BlockPos blockPos, BlockState blockState) {
        super(InitBlocks.GARAGE_KIT_BE.get(), blockPos, blockState);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        output.store(EXTRA_DATA, COMPOUND_TAG_CODEC, extraData);
        super.saveAdditional(output);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        extraData = input.read(EXTRA_DATA, COMPOUND_TAG_CODEC).orElseGet(CompoundTag::new);
    }

    public void setExtraData(CompoundTag extraData) {
        this.extraData = extraData;
        this.refresh();
    }

    public CompoundTag getExtraData() {
        return extraData;
    }
}
