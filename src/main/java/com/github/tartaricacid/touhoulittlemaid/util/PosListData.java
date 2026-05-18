package com.github.tartaricacid.touhoulittlemaid.util;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public final class PosListData {
    private final List<BlockPos> data = Lists.newArrayList();

    public ListTag serialize() {
        ListTag nbt = new ListTag();
        for (BlockPos pos : this.data) {
            nbt.add(new IntArrayTag(new int[]{pos.getX(), pos.getY(), pos.getZ()}));
        }
        return nbt;
    }

    public void deserialize(ListTag nbt) {
        // TODO: 这一部分还没单独测试过
        this.data.clear();
        for (int i = 0; i < nbt.size(); i++) {
            Tag tag = nbt.get(i);
            if (tag instanceof IntArrayTag intArrayTag) {
                int[] arr = intArrayTag.getAsIntArray();
                if (arr.length >= 3) {
                    this.data.add(new BlockPos(arr[0], arr[1], arr[2]));
                }
            } else if (tag instanceof CompoundTag compoundTag) {
                BlockPos pos = legacyBlockPos(compoundTag);
                if (pos != null) {
                    this.data.add(pos);
                }
            }
        }
    }

    @Nullable
    private static BlockPos legacyBlockPos(CompoundTag compound) {
        Optional<Integer> x = compound.getInt("X");
        Optional<Integer> y = compound.getInt("Y");
        Optional<Integer> z = compound.getInt("Z");
        if (x.isPresent() && y.isPresent() && z.isPresent()) {
            return new BlockPos(x.get(), y.get(), z.get());
        }
        x = compound.getInt("x");
        y = compound.getInt("y");
        z = compound.getInt("z");
        if (x.isPresent() && y.isPresent() && z.isPresent()) {
            return new BlockPos(x.get(), y.get(), z.get());
        }
        return null;
    }

    public List<BlockPos> getData() {
        return this.data;
    }

    public void add(BlockPos pos) {
        this.data.add(pos);
    }
}
