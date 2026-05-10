package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MaidWalkToLivingEntityTask extends MaidCheckRateTask {
    private static final int MAX_DELAY_TIME = 12;

    private final float speedModifier;
    private final Predicate<EntityMaid> startSearchPredicate;
    private final BiPredicate<EntityMaid, LivingEntity> entityPredicate;
    private final BiConsumer<EntityMaid, LivingEntity> arriveAction;
    private final float closeEnoughDistance;

    private @Nullable LivingEntity targetEntity = null;

    public MaidWalkToLivingEntityTask(float speedModifier, float closeEnoughDistance, @Nullable Predicate<EntityMaid> startSearchPredicate,
                                      @Nullable BiPredicate<EntityMaid, LivingEntity> entityPredicate, @Nullable BiConsumer<EntityMaid, LivingEntity> arriveAction) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speedModifier = speedModifier;
        this.setMaxCheckRate(MAX_DELAY_TIME);
        this.closeEnoughDistance = closeEnoughDistance;
        this.startSearchPredicate = Objects.requireNonNullElse(startSearchPredicate, maid -> false);
        this.entityPredicate = Objects.requireNonNullElse(entityPredicate, (maid, entity) -> false);
        this.arriveAction = Objects.requireNonNullElse(arriveAction, ((maid, entity) -> {
        }));
    }

    @Override
    protected void start(ServerLevel worldIn, EntityMaid maid, long gameTimeIn) {
        targetEntity = null;
        if (!startSearchPredicate.test(maid)) {
            return;
        }
        this.getEntities(maid)
                .find(e -> maid.isWithinRestriction(e.blockPosition()))
                .filter(Entity::isAlive)
                .filter(e -> this.entityPredicate.test(maid, e))
                .filter(maid::canPathReach)
                .findFirst()
                .ifPresent(e -> {
                    targetEntity = e;
                    BehaviorUtils.setWalkAndLookTargetMemories(maid, e, this.speedModifier, (int) Math.max(0, this.closeEnoughDistance - 1));
                });

        if (targetEntity != null && targetEntity.closerThan(maid, this.closeEnoughDistance)) {
            this.arriveAction.accept(maid, targetEntity);
            maid.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
            maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            targetEntity = null;
        }
    }

    private NearestVisibleLivingEntities getEntities(EntityMaid maid) {
        return maid.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
    }
}
