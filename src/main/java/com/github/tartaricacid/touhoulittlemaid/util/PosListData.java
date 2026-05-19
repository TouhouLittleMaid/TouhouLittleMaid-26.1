package com.github.tartaricacid.touhoulittlemaid.util;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.List;

public final class PosListData implements ValueIOSerializable {
    private static final String KEY = "PosListData";

    private final List<BlockPos> data = Lists.newArrayList();

    @Override
    public void serialize(ValueOutput output) {
        output.store(KEY, BlockPos.CODEC.listOf(), this.data);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.data.clear();
        input.read(KEY, BlockPos.CODEC.listOf()).ifPresent(this.data::addAll);
    }

    public List<BlockPos> getData() {
        return this.data;
    }

    public void add(BlockPos pos) {
        this.data.add(pos);
    }
}
