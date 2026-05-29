package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.sensor.MaidHostilesSensor;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.sensor.MaidNearestLivingEntitySensor;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.sensor.MaidPickupEntitiesSensor;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.edible.MaidEdibleBlockAction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.MOD_ID;

public interface InitBrains {
    DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, MOD_ID);
    DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE, MOD_ID);
    DeferredRegister<EnvironmentAttribute<?>> ENVIRONMENT_ATTRIBUTES = DeferredRegister.create(Registries.ENVIRONMENT_ATTRIBUTE, MOD_ID);
    DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(Registries.ACTIVITY, TouhouLittleMaid.MOD_ID);

    Supplier<Activity> RIDE_IDLE = ACTIVITIES.register("ride_idle", () -> new Activity("tlm_ride_idle"));
    Supplier<Activity> RIDE_WORK = ACTIVITIES.register("ride_work", () -> new Activity("tlm_ride_work"));
    Supplier<Activity> RIDE_REST = ACTIVITIES.register("ride_rest", () -> new Activity("tlm_ride_rest"));

    Supplier<MemoryModuleType<List<Entity>>> VISIBLE_PICKUP_ENTITIES = MEMORY_MODULE_TYPES.register(
            "visible_pickup_entities",
            () -> new MemoryModuleType<>(Optional.empty())
    );

    Supplier<MemoryModuleType<PositionTracker>> TARGET_POS = MEMORY_MODULE_TYPES.register(
            "target_pos",
            () -> new MemoryModuleType<>(Optional.empty())
    );

    Supplier<MemoryModuleType<MaidEdibleBlockAction>> MAID_EDIBLE_BLOCK_ACTION = MEMORY_MODULE_TYPES.register(
            "maid_edible_block_action",
            () -> new MemoryModuleType<>(Optional.empty())
    );

    Supplier<SensorType<MaidNearestLivingEntitySensor>> MAID_NEAREST_LIVING_ENTITY_SENSOR = SENSOR_TYPES.register(
            "maid_nearest_living_entity",
            () -> new SensorType<>(MaidNearestLivingEntitySensor::new)
    );

    Supplier<SensorType<MaidHostilesSensor>> MAID_HOSTILES_SENSOR = SENSOR_TYPES.register(
            "maid_hostiles",
            () -> new SensorType<>(MaidHostilesSensor::new)
    );

    Supplier<SensorType<MaidPickupEntitiesSensor>> MAID_PICKUP_ENTITIES_SENSOR = SENSOR_TYPES.register(
            "maid_pickup_entities",
            () -> new SensorType<>(MaidPickupEntitiesSensor::new)
    );

    Supplier<EnvironmentAttribute<Activity>> MAID_DAY_SHIFT_ACTIVITY = ENVIRONMENT_ATTRIBUTES.register(
            "gameplay/maid_day_shift_activity",
            () -> EnvironmentAttribute
                    .builder(AttributeTypes.ACTIVITY)
                    .defaultValue(Activity.IDLE)
                    .build());

    Supplier<EnvironmentAttribute<Activity>> MAID_NIGHT_SHIFT_ACTIVITY = ENVIRONMENT_ATTRIBUTES.register(
            "gameplay/maid_night_shift_activity",
            () -> EnvironmentAttribute
                    .builder(AttributeTypes.ACTIVITY)
                    .defaultValue(Activity.IDLE)
                    .build());

    Supplier<EnvironmentAttribute<Activity>> MAID_ALL_DAY_ACTIVITY = ENVIRONMENT_ATTRIBUTES.register(
            "gameplay/maid_all_day_activity",
            () -> EnvironmentAttribute
                    .builder(AttributeTypes.ACTIVITY)
                    .defaultValue(Activity.WORK)
                    .build()
    );
}
