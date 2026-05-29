package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.init.InitAttribute;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class MaidConstant {
    /**
     * 如果其他模组想要给女仆添加额外属性
     * <p>
     * 可通过 forge 的 EntityAttributeModificationEvent 添加
     */
    public static AttributeSupplier.Builder createAttributes() {
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
}
