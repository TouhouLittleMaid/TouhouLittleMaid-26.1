package com.github.tartaricacid.touhoulittlemaid.api.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public interface TaskDataKey<T> {
    Identifier id();
}