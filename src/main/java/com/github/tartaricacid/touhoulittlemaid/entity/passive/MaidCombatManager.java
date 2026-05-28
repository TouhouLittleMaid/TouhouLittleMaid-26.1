package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAttackEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDamageEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidHurtEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidHurtTarget;
import com.github.tartaricacid.touhoulittlemaid.api.task.IAttackTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IRangedAttackTask;
import com.github.tartaricacid.touhoulittlemaid.init.InitAttribute;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.github.tartaricacid.touhoulittlemaid.util.functional.TriFunction;
import com.github.tartaricacid.touhoulittlemaid.util.functional.TriPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiPredicate;

import static net.neoforged.neoforge.common.CommonHooks.onLivingDamagePost;
import static net.neoforged.neoforge.common.CommonHooks.onLivingDamagePre;

public class MaidCombatManager {
    private final EntityMaid maid;

    private int passiveUseShieldTick = 0;

    public MaidCombatManager(EntityMaid maid) {
        this.maid = maid;
    }

    void aiStep() {
        if (this.passiveUseShieldTick > 0) {
            // 如果没有拿着盾牌，直接取消计时，避免疯狂挥手
            ItemStack offHandItem = maid.getItemInHand(InteractionHand.OFF_HAND);
            if (offHandItem.has(DataComponents.BLOCKS_ATTACKS)) {
                this.passiveUseShieldTick--;
            } else {
                this.passiveUseShieldTick = 1;
            }
            // 最后 1 tick 取消盾牌
            if (this.passiveUseShieldTick == 1 && maid.isUsingItem() && maid.getUsedItemHand() == InteractionHand.OFF_HAND) {
                maid.stopUsingItem();
            }
        }
    }

    boolean isWithinMeleeAttackRange(LivingEntity target) {
        int favorability = maid.getFavorability();
        int attackPlusDistance = maid.getFavorabilityManager().getAttackDistancePlusByPoint(favorability);
        double attackDistance = maid.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + attackPlusDistance;
        return maid.distanceTo(target) < attackDistance;
    }

    boolean doHurtTarget(ServerLevel level, Entity target, BiPredicate<ServerLevel, Entity> superHurtTarget) {
        MaidHurtTarget.Pre event = new MaidHurtTarget.Pre(maid, target);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return true;
        }

        // 调用饰品的攻击
        maid.getMaidBauble().fireEvent((b, s) -> {
            b.onMeleeAttack(maid, s, target);
            return false;
        });

        boolean result = superHurtTarget.test(level, target);
        if (result) {
            // 尝试使用横扫之刃
            this.doSweepHurt(target);
            // 调用 hurtEnemy 来实现耐久消耗和部分其他功能
            ItemStack mainHandItem = maid.getMainHandItem();
            Item item = mainHandItem.getItem();
            if (target instanceof LivingEntity livingEntity) {
                item.hurtEnemy(mainHandItem, livingEntity, maid);
                item.postHurtEnemy(mainHandItem, livingEntity, maid);
            }
        }

        MaidHurtTarget.Post postEvent = new MaidHurtTarget.Post(maid, target, result);
        NeoForge.EVENT_BUS.post(postEvent);

        // 部分 task 有额外伤害
        if (maid.getTask() instanceof IAttackTask attackTask && attackTask.hasExtraAttack(maid, target)) {
            boolean extraResult = attackTask.doExtraAttack(maid, target);
            return result && extraResult;
        }
        return result;
    }

    boolean hurtServer(ServerLevel level, DamageSource source, float amount,
                       TriPredicate<ServerLevel, DamageSource, Float> superHurtServer
    ) {
        if (NeoForge.EVENT_BUS.post(new MaidAttackEvent(maid, source, amount)).isCanceled()) {
            return false;
        }
        if (source.getEntity() instanceof Player player && maid.isAlliedTo(player)) {
            // 主人和同 Team 玩家对自己女仆的伤害数值为 1/5，最大为 2
            amount = Mth.clamp(amount / 5, 0, 2);
            return superHurtServer.test(level, source, amount);
        }
        // 使用盾牌
        if (source.is(DamageTypeTags.IS_PROJECTILE) && maid.canUseShield()) {
            boolean isUsingShield = maid.isUsingItem() && maid.getUsedItemHand() == InteractionHand.OFF_HAND;
            if (!isUsingShield) {
                maid.startUsingItem(InteractionHand.OFF_HAND);
                // 使用五秒的盾牌
                AttributeInstance attribute = maid.getAttribute(InitAttribute.MAID_PASSIVE_USE_SHIELD_TICK);
                if (attribute != null) {
                    this.passiveUseShieldTick = (int) attribute.getValue();
                } else {
                    this.passiveUseShieldTick = 100;
                }
            }
        }
        return superHurtServer.test(level, source, amount);
    }

    ItemStack getProjectile(ItemStack weaponStack) {
        // 烟花只检查副手：优先检查副手有没有烟花
        if (maid.getOffhandItem().getItem() instanceof FireworkRocketItem) {
            return maid.getOffhandItem();
        }
        if (!(maid.getMainHandItem().getItem() instanceof ProjectileWeaponItem weaponItem)) {
            return ItemStack.EMPTY;
        }
        var handler = maid.getAvailableInv(true);
        int slot = ItemsUtil.findStackSlot(handler, weaponItem.getAllSupportedProjectiles(weaponStack));
        if (slot < 0) {
            // 不存在时，返回空
            return ItemStack.EMPTY;
        } else {
            // 拿到弹药物品
            return ItemUtil.getStack(handler, slot);
        }
    }

    void performRangedAttack(LivingEntity target, float distanceFactor) {
        IMaidTask maidTask = maid.getTask();
        if (maidTask instanceof IRangedAttackTask rangedAttackTask) {
            // 调用饰品的攻击
            maid.getMaidBauble().fireEvent((b, s) -> {
                b.onRangedAttack(maid, s, rangedAttackTask);
                return false;
            });
            rangedAttackTask.performRangedAttack(maid, target, distanceFactor);
        }
    }

    boolean canUseShield() {
        ItemStack offhandItem = maid.getOffhandItem();
        return offhandItem.has(DataComponents.BLOCKS_ATTACKS)
               && !maid.getCooldowns().isOnCooldown(offhandItem.getItem().getDefaultInstance());
    }

    @Nullable
    ItemStack getItemBlockingWith() {
        if (!maid.getUseItem().isEmpty()) {
            BlocksAttacks blocksAttacks = maid.getUseItem().get(DataComponents.BLOCKS_ATTACKS);
            if (blocksAttacks != null) {
                return maid.getUseItem();
            }
        }
        return null;
    }

    float applyItemBlocking(ServerLevel level, DamageSource source, float damage,
                            TriFunction<ServerLevel, DamageSource, Float, Float> superMethod
    ) {
        boolean shouldPredicateBlockItemBreaking = maid.isBlocking();
        InteractionHand interactionhand = maid.getUsedItemHand();
        float v = superMethod.apply(level, source, damage);
        if (shouldPredicateBlockItemBreaking && maid.getUseItem().isEmpty()) {
            if (interactionhand == InteractionHand.MAIN_HAND) {
                maid.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else {
                maid.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
            maid.setUseItem(ItemStack.EMPTY);
        }
        return v;
    }

    /**
     * 重新复写父类方法，添加上自己的 Event
     */
    @SuppressWarnings("UnstableApiUsage")
    void actuallyHurt(ServerLevel level, DamageSource damageSrc, float damageAmount) {
        if (!maid.isInvulnerableTo(level, damageSrc) && maid.getDamageContainers() != null) {
            DamageContainer peek = maid.getDamageContainers().peek();

            // 获取盔甲减伤后的数值
            float armorAbsorb = maid.getDamageAfterArmorAbsorb(damageSrc, peek.getNewDamage());
            peek.setReduction(DamageContainer.Reduction.ARMOR, peek.getNewDamage() - armorAbsorb);

            // 获取抗性提升减伤后的数值
            maid.getDamageAfterMagicAbsorb(damageSrc, peek.getNewDamage());

            // 获取事件减伤效果
            MaidHurtEvent maidHurtEvent = new MaidHurtEvent(maid, damageSrc, peek.getNewDamage());
            damageAmount = NeoForge.EVENT_BUS.post(maidHurtEvent).isCanceled() ? 0 : maidHurtEvent.getAmount();
            peek.setReduction(DamageContainer.Reduction.ABSORPTION, peek.getNewDamage() - damageAmount);

            // NeoForge 事件也来一套
            float damage = onLivingDamagePre(maid, peek);
            peek.setReduction(DamageContainer.Reduction.ABSORPTION, Math.min(maid.getAbsorptionAmount(), damage));

            // 总减伤效果，用于玩家信息统计
            float damageDealtAbsorbed = Math.min(damage, peek.getReduction(DamageContainer.Reduction.ABSORPTION));
            maid.setAbsorptionAmount(Math.max(0, maid.getAbsorptionAmount() - damageDealtAbsorbed));
            if (0 < damageDealtAbsorbed && damageDealtAbsorbed < 3.5 && damageSrc.getEntity() instanceof ServerPlayer player) {
                player.awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(damageDealtAbsorbed * 10));
            }

            // 饰品
            MutableFloat newDamage = new MutableFloat(peek.getNewDamage());
            boolean baubleCancel = maid.getMaidBauble().fireEvent(
                    (b, s) -> b.onInjured(maid, s, damageSrc, newDamage)
            );
            float damageAfterAbsorption = newDamage.getValue();
            // 如果饰品取消了事件，那么也不触发后续内容了
            if (baubleCancel || damageAfterAbsorption <= 0) {
                return;
            }

            // 再来一次事件
            MaidDamageEvent maidDamageEvent = new MaidDamageEvent(maid, damageSrc, damageAfterAbsorption);
            damageAfterAbsorption = NeoForge.EVENT_BUS.post(maidDamageEvent).isCanceled() ? 0 : maidDamageEvent.getAmount();

            // 最终运用实际伤害
            if (damageAfterAbsorption != 0) {
                maid.getCombatTracker().recordDamage(damageSrc, damageAfterAbsorption);
                maid.setHealth(maid.getHealth() - damageAfterAbsorption);
                maid.gameEvent(GameEvent.ENTITY_DAMAGE);
                maid.onDamageTaken(peek);
            }

            // NeoForge 事件也来一套
            onLivingDamagePost(maid, peek);
        }
    }

    private void doSweepHurt(Entity target) {
        ItemStack mainhandItem = maid.getItemInHand(InteractionHand.MAIN_HAND);
        boolean canSweep = mainhandItem.canPerformAction(ItemAbilities.SWORD_SWEEP);
        float sweepingDamageRatio = (float) maid.getAttributes().getValue(Attributes.SWEEPING_DAMAGE_RATIO);
        if (canSweep && sweepingDamageRatio > 0) {
            float baseDamage = (float) maid.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float sweepDamage = 1.0f + sweepingDamageRatio * baseDamage;
            AABB sweepRange = maid.getFavorabilityManager().getSweepRange(target, maid.getFavorability());
            List<LivingEntity> hurtEntities = maid.level.getEntitiesOfClass(LivingEntity.class, sweepRange);
            for (LivingEntity entity : hurtEntities) {
                if (entity != maid && entity != target && !maid.isAlliedTo(entity) && maid.canAttack(entity) && maid.wantsToAttack(entity, maid.getOwner())) {
                    float posX = Mth.sin(maid.getYRot() * ((float) Math.PI / 180F));
                    float posY = -Mth.cos(maid.getYRot() * ((float) Math.PI / 180F));
                    entity.knockback(0.4, posX, posY);
                    entity.hurt(maid.damageSources().mobAttack(maid), sweepDamage);
                }
            }
            maid.level.playSound(null, maid.getX(), maid.getY(), maid.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, maid.getSoundSource(), 1, 1);
            maid.getParticleManager().spawnSweepAttackParticle();
        }
    }

    interface View {
        MaidCombatManager getCombatManager();

        default boolean canUseShield() {
            return this.getCombatManager().canUseShield();
        }
    }
}
