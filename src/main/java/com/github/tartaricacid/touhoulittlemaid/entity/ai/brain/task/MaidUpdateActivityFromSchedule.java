package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBrains;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

import javax.annotation.Nullable;

public class MaidUpdateActivityFromSchedule extends Behavior<EntityMaid> {
    private @Nullable Activity cacheActivity;

    public MaidUpdateActivityFromSchedule() {
        super(ImmutableMap.of());
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        Brain<EntityMaid> brain = maid.getBrain();
        long dayTime = level.getGameTime();

        // 让女仆在切换日程表时能够改变自己的活动范围
        if (gameTime - brain.lastScheduleUpdate > 20L) {
            Activity activity = maid.components.task.getScheduleDetail();
            if (this.cacheActivity == null) {
                this.cacheActivity = activity;
            }
            if (!this.cacheActivity.equals(activity) && maid.components.config.isHomeModeEnable() && maid.canBrainMoving()) {
                this.cacheActivity = activity;
                maid.components.task.schedulePos.restrictTo(maid);
                BehaviorUtils.setWalkAndLookTargetMemories(maid, maid.getHomePosition(), 0.7f, 3);
            }
        }

        // 切换日程表，分骑乘和非骑乘两种情况
        updateActivityFromSchedule(level, maid, brain, gameTime);
    }

    public static void updateActivityFromSchedule(EntityMaid maid, Brain<EntityMaid> brain) {
        if (maid.level instanceof ServerLevel serverLevel) {
            long gameTime = serverLevel.getGameTime();
            updateActivityFromSchedule(serverLevel, maid, brain, gameTime);
        }
    }

    public static void updateActivityFromSchedule(EntityMaid maid) {
        if (maid.level instanceof ServerLevel serverLevel) {
            long gameTime = serverLevel.getGameTime();
            Brain<EntityMaid> brain = maid.getBrain();
            updateActivityFromSchedule(serverLevel, maid, brain, gameTime);
        }
    }

    private static void updateActivityFromSchedule(ServerLevel level, EntityMaid maid, Brain<EntityMaid> brain, long gameTime) {
        if (maid.isMaidInSittingPose() || maid.isPassenger()) {
            if (gameTime - brain.lastScheduleUpdate > 20L) {
                brain.lastScheduleUpdate = gameTime;
                Activity activity = maid.components.task.getScheduleDetail();
                Activity riderActivity;
                if (activity.equals(Activity.WORK)) {
                    riderActivity = InitBrains.RIDE_WORK.get();
                } else if (activity.equals(Activity.IDLE)) {
                    riderActivity = InitBrains.RIDE_IDLE.get();
                } else {
                    riderActivity = InitBrains.RIDE_REST.get();
                }
                if (!brain.isActive(riderActivity)) {
                    brain.eraseMemory(MemoryModuleType.PATH);
                    brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                    brain.setActiveActivityIfPossible(riderActivity);

                    // 如果是拥有工作点的 task，需要脱离骑乘的实体
                    if (maid.isPassenger() && !riderActivity.equals(InitBrains.RIDE_WORK.get())) {
                        if (!maid.components.task.getTask().workPointTask(maid)) {
                            return;
                        }
                        // 特殊的实体（比如娱乐工具的，就不需要脱离）
                        if (maid.getVehicle() instanceof EntitySit) {
                            return;
                        }
                        maid.stopRiding();
                    }
                }
            }
        } else {
            brain.updateActivityFromSchedule(level.environmentAttributes(), level.getGameTime(), maid.position());
        }
    }
}
