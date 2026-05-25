package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.sensor.MaidHostilesSensor;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.sensor.MaidNearestLivingEntitySensor;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.sensor.MaidPickupEntitiesSensor;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.edible.MaidEdibleBlockAction;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.item.*;
import com.github.tartaricacid.touhoulittlemaid.entity.monster.EntityFairy;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidGameRecordManager;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityDanmaku;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityThrowPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.MaidFishingHook;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@EventBusSubscriber()
public final class InitEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE, TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<EnvironmentAttribute<?>> SCHEDULES = DeferredRegister.create(Registries.ENVIRONMENT_ATTRIBUTE, TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(Registries.ACTIVITY, TouhouLittleMaid.MOD_ID);

    public static DeferredHolder<EntityType<?>, EntityType<EntityMaid>> MAID = ENTITY_TYPES.register("maid", () -> EntityMaid.TYPE);
    public static Supplier<EntityType<EntityChair>> CHAIR = ENTITY_TYPES.register("chair", () -> EntityChair.TYPE);
    public static Supplier<EntityType<EntityFairy>> FAIRY = ENTITY_TYPES.register("fairy", () -> EntityFairy.TYPE);
    public static Supplier<EntityType<EntityDanmaku>> DANMAKU = ENTITY_TYPES.register("danmaku", () -> EntityDanmaku.TYPE);
    public static Supplier<EntityType<EntityPowerPoint>> POWER_POINT = ENTITY_TYPES.register("power_point", () -> EntityPowerPoint.TYPE);
    public static Supplier<EntityType<EntityExtinguishingAgent>> EXTINGUISHING_AGENT = ENTITY_TYPES.register("extinguishing_agent", () -> EntityExtinguishingAgent.TYPE);
    public static Supplier<EntityType<EntityBox>> BOX = ENTITY_TYPES.register("box", () -> EntityBox.TYPE);
    public static Supplier<EntityType<EntityThrowPowerPoint>> THROW_POWER_POINT = ENTITY_TYPES.register("throw_power_point", () -> EntityThrowPowerPoint.TYPE);
    public static Supplier<EntityType<EntityTombstone>> TOMBSTONE = ENTITY_TYPES.register("tombstone", () -> EntityTombstone.TYPE);
    public static Supplier<EntityType<EntitySit>> SIT = ENTITY_TYPES.register("sit", () -> EntitySit.TYPE);
    public static Supplier<EntityType<EntityBroom>> BROOM = ENTITY_TYPES.register("broom", () -> EntityBroom.TYPE);
    public static Supplier<EntityType<MaidFishingHook>> FISHING_HOOK = ENTITY_TYPES.register("fishing_hook", () -> MaidFishingHook.TYPE);

    public static Supplier<Activity> RIDE_IDLE = ACTIVITIES.register("ride_idle", () -> new Activity("tlm_ride_idle"));
    public static Supplier<Activity> RIDE_WORK = ACTIVITIES.register("ride_work", () -> new Activity("tlm_ride_work"));
    public static Supplier<Activity> RIDE_REST = ACTIVITIES.register("ride_rest", () -> new Activity("tlm_ride_rest"));

    public static Supplier<MemoryModuleType<List<Entity>>> VISIBLE_PICKUP_ENTITIES = MEMORY_MODULE_TYPES.register("visible_pickup_entities", () -> new MemoryModuleType<>(Optional.empty()));
    public static Supplier<MemoryModuleType<PositionTracker>> TARGET_POS = MEMORY_MODULE_TYPES.register("target_pos", () -> new MemoryModuleType<>(Optional.empty()));
    public static Supplier<MemoryModuleType<MaidEdibleBlockAction>> MAID_EDIBLE_BLOCK_ACTION = MEMORY_MODULE_TYPES.register("maid_edible_block_action", () -> new MemoryModuleType<>(Optional.empty()));

    public static Supplier<SensorType<MaidNearestLivingEntitySensor>> MAID_NEAREST_LIVING_ENTITY_SENSOR = SENSOR_TYPES.register("maid_nearest_living_entity", () -> new SensorType<>(MaidNearestLivingEntitySensor::new));
    public static Supplier<SensorType<MaidHostilesSensor>> MAID_HOSTILES_SENSOR = SENSOR_TYPES.register("maid_hostiles", () -> new SensorType<>(MaidHostilesSensor::new));
    public static Supplier<SensorType<MaidPickupEntitiesSensor>> MAID_PICKUP_ENTITIES_SENSOR = SENSOR_TYPES.register("maid_pickup_entities", () -> new SensorType<>(MaidPickupEntitiesSensor::new));

    public static Supplier<EnvironmentAttribute<Activity>> MAID_DAY_SHIFT_SCHEDULES = SCHEDULES.register("maid_day_shift_schedules",
            () -> {
                // 06:00 ~ 18:00 工作
                // 18:00 ~ 22:00 娱乐
                // 22:00 ~ 06:00 睡觉
                return EnvironmentAttribute.builder(AttributeTypes.ACTIVITY)
                        .defaultValue(Activity.IDLE)
                        .build();
            });
    public static Supplier<EnvironmentAttribute<Activity>> MAID_NIGHT_SHIFT_SCHEDULES = SCHEDULES.register("maid_night_shift_schedules",
            () -> {
                // 18:00 ~ 06:00 工作
                // 06:00 ~ 14:00 睡觉
                // 14:00 ~ 18:00 娱乐
                return EnvironmentAttribute.builder(AttributeTypes.ACTIVITY)
                        .defaultValue(Activity.IDLE)
                        .build();
            });
    public static Supplier<EnvironmentAttribute<Activity>> MAID_ALL_DAY_SCHEDULES = SCHEDULES.register("maid_all_day_schedules",
            () -> EnvironmentAttribute.builder(AttributeTypes.ACTIVITY).defaultValue(Activity.WORK).build()
    );

    public static EntityDataSerializer<Optional<UUID>> SERIALIZER_OPTIONAL_UUID = EntityDataSerializer.forValueType(ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC));

    public static Supplier<EntityDataSerializer<?>> MAID_SCHEDULE_DATA_SERIALIZERS = DATA_SERIALIZERS.register("maid_schedule", () -> MaidSchedule.DATA);
    public static Supplier<EntityDataSerializer<?>> MAID_CHAT_BUBBLE_DATA_SERIALIZERS = DATA_SERIALIZERS.register("maid_chat_bubble", () -> ChatBubbleRegister.INSTANCE);
    public static Supplier<EntityDataSerializer<?>> OPTIONAL_UUID_SERIALIZERS = DATA_SERIALIZERS.register("optional_uuid", () -> SERIALIZER_OPTIONAL_UUID);
    public static Supplier<EntityDataSerializer<?>> MAID_GAME_WIN_COUNTS = DATA_SERIALIZERS.register("maid_game_win_counts", () -> MaidGameRecordManager.WIN_COUNT_SERIALIZER);


    @SubscribeEvent
    public static void addEntityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(EntityMaid.TYPE, EntityMaid.createAttributes().build());
        event.put(EntityChair.TYPE, LivingEntity.createLivingAttributes().build());
        event.put(EntityBroom.TYPE, LivingEntity.createLivingAttributes().build());
        event.put(EntityFairy.TYPE, EntityFairy.createFairyAttributes().build());
    }

    @SubscribeEvent
    public static void addEntitySpawnPlacement(RegisterSpawnPlacementsEvent event) {
        event.register(InitEntities.FAIRY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFairy::checkFairySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
