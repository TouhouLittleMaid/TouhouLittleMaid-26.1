package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Swim;

public class MaidSwimJumpTask extends Swim {
    public MaidSwimJumpTask(float chance) {
        super(chance);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Mob mob, long gameTime) {
        // 在水下的时候禁止被动跳跃
        return super.canStillUse(level, mob, gameTime) && !mob.isUnderWater() && mob.getNavigation().isDone();
    }
}
