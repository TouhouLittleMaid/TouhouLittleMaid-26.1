package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl.MaidSwimComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBrains;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

/**
 * 女仆从离开呼吸的任务返回
 */
public class MaidBreathAirStopTask extends Behavior<EntityMaid> {
    public MaidBreathAirStopTask() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        if (!maid.components.swim.isGoingToBreath()) {
            return false;
        }
        // 下面的条件表示女仆不再有窒息风险
        if (maid.getAirSupply() >= 290) {
            return true;
        }
        if (MobEffectUtil.hasWaterBreathing(maid)) {
            return true;
        }
        if (hasDrownBauble(maid)) {
            return true;
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        maid.components.swim.setGoingToBreath(false);
        // 如果呼吸计划打断了某些任务寻路，则需要重置目标记忆来重新寻路
        maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        maid.getBrain().eraseMemory(InitBrains.TARGET_POS.get());
    }

    private boolean hasDrownBauble(EntityMaid maid) {
        BaubleItemHandler maidBauble = maid.components.item.getMaidBauble();
        for (int i = 0; i < maidBauble.size(); i++) {
            if (maidBauble.getResource(i).is(InitItems.DROWN_PROTECT_BAUBLE.get())) {
                return true;
            }
        }
        return false;
    }
}
