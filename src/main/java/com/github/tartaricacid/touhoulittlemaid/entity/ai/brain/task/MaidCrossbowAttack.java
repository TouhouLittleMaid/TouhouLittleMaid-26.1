package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.CrossbowAttack;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

/**
 * 原版的 CrossbowAttack 调用了 BehaviorUtils.canSee，导致射击限制在 16 格内
 * <p>
 * 故略作修改，让其适配自定义距离
 */
public class MaidCrossbowAttack extends CrossbowAttack<EntityMaid, EntityMaid> {
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        return maid.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                .map(target -> maid.isHolding(this::isCrossbow) && maid.canSee(target))
                .orElse(false);
    }

    private boolean isCrossbow(ItemStack stack) {
        return stack.getItem() instanceof CrossbowItem;
    }
}
