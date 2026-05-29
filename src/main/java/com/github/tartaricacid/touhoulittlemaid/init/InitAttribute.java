package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBroom;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.entity.monster.EntityFairy;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID)
public interface InitAttribute {
    DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, TouhouLittleMaid.MOD_ID);

    /**
     * 女仆使用物品的速度倍率，数值越大，物品冷却时间越短
     */
    DeferredHolder<Attribute, Attribute> MAID_USE_ITEM_SPEED = newAttribute("maid_use_item_speed", 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
    /**
     * 女仆使用弩射击的速度倍率，数值越大，弩箭射击间隔越短
     */
    DeferredHolder<Attribute, Attribute> MAID_CROSSBOW_ATTACK_SPEED = newAttribute("maid_crossbow_attack_speed", 1, 0, Integer.MAX_VALUE);
    /**
     * 女仆使用枪械（TaCZ/卓越前线）射击的速度倍率，数值越大，射击间隔越短
     * <p>
     * 可能无法超越枪本身的最大射速
     */
    DeferredHolder<Attribute, Attribute> MAID_GUN_ATTACK_SPEED = newAttribute("maid_gun_attack_speed", 1, 0, Integer.MAX_VALUE);
    /**
     * 女仆单次射击（弓、弩）后的冷却时间，数值越大，射击后的冷却时间越长
     */
    DeferredHolder<Attribute, Attribute> MAID_SHOOT_COOLDOWN = newAttribute("maid_shoot_cooldown", 2, 0, Integer.MAX_VALUE);
    /**
     * 女仆射出一支三叉戟后的冷却时间，数值越大，射击后的冷却时间越长
     */
    DeferredHolder<Attribute, Attribute> MAID_TRIDENT_COOLDOWN = newAttribute("maid_trident_cooldown", 20, 0, Integer.MAX_VALUE);
    /**
     * 女仆拾取掉落物/经验球/P点的范围，数值越大，拾取范围越大
     */
    DeferredHolder<Attribute, Attribute> MAID_PICKUP_RANGE = newAttribute("maid_pickup_range", 0.5, 0, Double.MAX_VALUE);
    /**
     * 女仆单次举盾的持续时间（单位：tick）
     */
    DeferredHolder<Attribute, Attribute> MAID_PASSIVE_USE_SHIELD_TICK = newAttribute("maid_passive_use_shield_tick", 100, 0, Integer.MAX_VALUE);
    /**
     * 女仆的饱食度，目前本体暂时没有使用，主要给其他附属模组调用
     */
    DeferredHolder<Attribute, Attribute> MAID_HUNGER = newAttribute("maid_hunger", 20, 0, Integer.MAX_VALUE);

    static DeferredHolder<Attribute, Attribute> newAttribute(String id, double defaultValue, double min, double max) {
        final String key = "attribute." + TouhouLittleMaid.MOD_ID + "." + id;
        return ATTRIBUTES.register(id, () -> new RangedAttribute(key, defaultValue, min, max).setSyncable(true));
    }

    static AttributeSupplier.Builder createMaidAttributes() {
        return LivingEntity.createLivingAttributes()
                // 目前仅用于寻路，女仆最大可寻路 64 格
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.SWEEPING_DAMAGE_RATIO)
                // 目前仅用于寻路，女仆最大可寻路 64 格
                .add(Attributes.LUCK)
                // 女仆攻击速度，这个数字表示每秒可施展的攻击次数，会受武器本身的攻击速度影响
                .add(Attributes.ATTACK_SPEED)
                // 用于女仆近战的范围判断
                .add(Attributes.ENTITY_INTERACTION_RANGE, 2)
                // 部分本模组新增属性
                .add(InitAttribute.MAID_USE_ITEM_SPEED)
                .add(InitAttribute.MAID_CROSSBOW_ATTACK_SPEED)
                .add(InitAttribute.MAID_GUN_ATTACK_SPEED)
                .add(InitAttribute.MAID_SHOOT_COOLDOWN)
                .add(InitAttribute.MAID_TRIDENT_COOLDOWN)
                .add(InitAttribute.MAID_PICKUP_RANGE)
                .add(InitAttribute.MAID_PASSIVE_USE_SHIELD_TICK)
                .add(InitAttribute.MAID_HUNGER);
    }

    static AttributeSupplier.Builder createFairyAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 1.5)
                .add(Attributes.ARMOR, 1.)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    @SubscribeEvent
    static void addEntityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(EntityMaid.TYPE, InitAttribute.createMaidAttributes().build());
        event.put(EntityFairy.TYPE, InitAttribute.createFairyAttributes().build());
        event.put(EntityChair.TYPE, LivingEntity.createLivingAttributes().build());
        event.put(EntityBroom.TYPE, LivingEntity.createLivingAttributes().build());
    }
}
