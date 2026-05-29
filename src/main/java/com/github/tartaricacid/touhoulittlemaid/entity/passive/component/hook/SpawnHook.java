package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public interface SpawnHook extends MaidComponent {
    Component getTypeName();

    @Nullable
    SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
                                 EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn);

    void thunderHit(ServerLevel world, LightningBolt lightning);
}
