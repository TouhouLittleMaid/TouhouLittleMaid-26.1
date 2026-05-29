package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * MaidShootTargetAnyItemTask 的升级版本，不限制手持物品必须是 ProjectileWeaponItem
 */
public class MaidShootTargetAnyItemTask extends Behavior<EntityMaid> {
    private final int attackCooldown;
    private final int chargeDurationTick;
    private final Predicate<ItemStack> weaponTest;
    private int attackTime = -1;
    private int seeTime;
    private int swingTime;

    public MaidShootTargetAnyItemTask(int attackCooldown, int chargeDurationTick, Predicate<ItemStack> weaponTest) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
        this.attackCooldown = attackCooldown;
        this.chargeDurationTick = chargeDurationTick;
        this.weaponTest = weaponTest;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel worldIn, EntityMaid owner) {
        Optional<LivingEntity> memory = owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (memory.isPresent()) {
            LivingEntity target = memory.get();
            return owner.isHolding(weaponTest) && owner.canSee(target);
        }
        return false;
    }

    @Override
    protected boolean canStillUse(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        return entityIn.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(worldIn, entityIn);
    }

    @Override
    protected void start(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        entityIn.components.animation.setSwingingArms(true);
    }

    @Override
    protected void tick(ServerLevel worldIn, EntityMaid owner, long gameTime) {
        owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((target) -> {
            // 强行看见并朝向
            owner.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ());
            boolean canSee = owner.canSee(target);
            boolean seeTimeMoreThanZero = this.seeTime > 0;

            // 如果两者不一致，重置看见时间
            if (canSee != seeTimeMoreThanZero) {
                this.seeTime = 0;
            }
            // 如果看见了对方，增加看见时间，否则减少
            if (canSee) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            // 依据是否可以 use 物品，分别判断
            boolean itemCanNotUse = owner.getMainHandItem().getUseDuration(owner) <= 0;
            if (itemCanNotUse) {
                tickItemCanNotUse(owner, target, canSee);
            } else {
                // 如果实体手部处于激活状态
                tickItemCanUse(owner, target, canSee);
            }
        });
    }

    private void tickItemCanUse(EntityMaid owner, LivingEntity target, boolean canSee) {
        if (owner.isUsingItem()) {
            // 如果看不见对方超时 60，重置激活状态
            if (!canSee && this.seeTime < -60) {
                owner.stopUsingItem();
            } else if (canSee) {
                // 否则开始进行远程攻击
                int ticksUsingItem = owner.getTicksUsingItem();
                if (ticksUsingItem >= this.chargeDurationTick) {
                    owner.stopUsingItem();
                    int powerTime = Math.max(ticksUsingItem, 20);
                    owner.performRangedAttack(target, BowItem.getPowerForTime(powerTime));
                    this.attackTime = this.attackCooldown;
                }
            }
        } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
            // 非激活状态，但是时长合适，开始激活手部
            owner.startUsingItem(InteractionHand.MAIN_HAND);
        }
    }

    private void tickItemCanNotUse(EntityMaid owner, LivingEntity target, boolean canSee) {
        if (owner.components.animation.isSwingingArms()) {
            if (!canSee && this.seeTime < -60) {
                owner.components.animation.setSwingingArms(false);
                swingTime = 0;
            } else if (canSee) {
                if (swingTime >= this.chargeDurationTick) {
                    int powerTime = Math.max(swingTime, 20);
                    owner.performRangedAttack(target, BowItem.getPowerForTime(powerTime));
                    this.attackTime = this.attackCooldown;
                    owner.components.animation.setSwingingArms(false);
                    swingTime = 0;
                }
            }
            swingTime++;
        } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
            owner.components.animation.setSwingingArms(true);
        }
    }

    @Override
    protected void stop(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        this.seeTime = 0;
        this.attackTime = -1;
        this.swingTime = 0;
        entityIn.stopUsingItem();
    }
}
