package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.RunOne;

import java.util.List;
import java.util.function.Predicate;

public class MaidRunOne extends RunOne<EntityMaid> {
    private final Predicate<EntityMaid> enableCondition;

    public MaidRunOne(List<Pair<? extends BehaviorControl<? super EntityMaid>, Integer>> entryCondition, Predicate<EntityMaid> enableCondition) {
        super(entryCondition);
        this.enableCondition = enableCondition;
    }

    public MaidRunOne(List<Pair<? extends BehaviorControl<? super EntityMaid>, Integer>> entryCondition) {
        this(entryCondition, maid -> true);
    }

    @Override
    public boolean tryStart(ServerLevel pLevel, EntityMaid maid, long pGameTime) {
        if (!enableCondition.test(maid)) {
            return false;
        }
        return !maid.components.animation.isBegging() && !maid.isSleeping() && !(maid.getVehicle() instanceof EntitySit) && super.tryStart(pLevel, maid, pGameTime);
    }
}