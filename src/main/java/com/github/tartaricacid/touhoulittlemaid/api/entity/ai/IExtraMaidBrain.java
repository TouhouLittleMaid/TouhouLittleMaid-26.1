package com.github.tartaricacid.touhoulittlemaid.api.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.Collections;
import java.util.List;

public interface IExtraMaidBrain {
    /**
     * 为女仆 AI 添加新的 MemoryModuleType
     */
    default List<MemoryModuleType<?>> getExtraMemoryTypes() {
        return Collections.emptyList();
    }

    /**
     * 为女仆 AI 添加新的 SensorType
     */
    default List<SensorType<? extends Sensor<? super EntityMaid>>> getExtraSensorTypes() {
        return Collections.emptyList();
    }

    /**
     * 为女仆 AI 添加新的核心行为
     * <p>
     * 核心行为无论在什么 Activity 都会被添加
     */
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getCoreBehaviors() {
        return Collections.emptyList();
    }

    /**
     * 为女仆 AI 添加新的 Panic 行为，也就是女仆被怪物打了以后乱跑时，会短暂处于这个活动中
     * <p>
     * Panic 行为只会在 Panic Activity 中被添加
     */
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getPanicBehaviors() {
        return Collections.emptyList();
    }

    /**
     * 为女仆 AI 添加新的 Ride Idle 行为，也就是女仆骑乘实体时并处于休息日程时的行为
     * <p>
     * Ride Idle 行为只会在 Ride Idle Activity 中被添加
     */
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getRideIdleBehaviors() {
        return Collections.emptyList();
    }

    /**
     * 为女仆 AI 添加新的 Ride Work 行为，也就是女仆骑乘实体时并处于工作日程时的行为
     * <p>
     * Ride Work 行为只会在 Ride Work Activity 中被添加
     */
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getRideWorkBehaviors() {
        return Collections.emptyList();
    }

    /**
     * 为女仆 AI 添加新的 Ride Rest 行为，也就是女仆骑乘实体时并处于睡觉日程时的行为
     * <p>
     * Ride Rest 行为只会在 Ride Rest Activity 中被添加
     */
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getRideRestBehaviors() {
        return Collections.emptyList();
    }

    /**
     * 为女仆 AI 添加新的 Idle 行为，也就是女仆处于休息状态时的行为
     * <p>
     * Idle 行为只会在 Idle Activity 中被添加
     */
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getIdleBehaviors() {
        return Collections.emptyList();
    }

    /**
     * 为女仆 AI 添加新的 Work 行为，也就是女仆处于工作状态时的行为
     * <p>
     * Work 行为只会在 Work Activity 中被添加
     */
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getWorkBehaviors() {
        return Collections.emptyList();
    }

    /**
     * 为女仆 AI 添加新的 Rest 行为，也就是女仆处于睡觉状态时的行为
     * <p>
     * Rest 行为只会在 Rest Activity 中被添加
     */
    default List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getRestBehaviors() {
        return Collections.emptyList();
    }
}
