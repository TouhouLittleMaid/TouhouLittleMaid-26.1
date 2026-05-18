package com.github.tartaricacid.touhoulittlemaid.api.backpack;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface IBackpackData {
    ContainerData getDataAccess();

    void load(ValueInput tag, EntityMaid maid);

    void save(ValueOutput tag, EntityMaid maid);

    void serverTick(EntityMaid maid);
}
