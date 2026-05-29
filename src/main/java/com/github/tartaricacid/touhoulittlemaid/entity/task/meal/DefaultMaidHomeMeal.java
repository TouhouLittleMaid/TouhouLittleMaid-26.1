package com.github.tartaricacid.touhoulittlemaid.entity.task.meal;

import com.github.tartaricacid.touhoulittlemaid.api.task.meal.IMaidMeal;
import com.github.tartaricacid.touhoulittlemaid.api.task.meal.MaidMealType;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl.FavorabilityComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.event.MaidMealRegConfigEvent;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.SpawnParticlePackage;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class DefaultMaidHomeMeal implements IMaidMeal {
    private static final int MAX_PROBABILITY = 15;

    public static boolean isHomeMeal(ItemStack stack) {
        return stack.has(DataComponents.FOOD)
               && !IMaidMeal.isBlockList(stack, MaidConfig.MAID_HOME_MEALS_BLOCK_LIST.get())
               && !IMaidMeal.isBlockList(stack, MaidMealRegConfigEvent.HOME_MEAL_REGEX);
    }

    @Override
    public boolean canMaidEat(EntityMaid maid, ItemStack stack, InteractionHand hand) {
        return isHomeMeal(stack);
    }

    @Override
    public void onMaidEat(EntityMaid maid, ItemStack stack, InteractionHand hand) {
        FoodProperties foodProperties = stack.get(DataComponents.FOOD);
        if (foodProperties != null) {
            // 调用饰品
            maid.components().item.getMaidBauble().fireEvent((b, s) -> {
                b.onMaidEat(maid, s, stack, MaidMealType.HOME_MEAL);
                return false;
            });

            maid.startUsingItem(hand);
            int nutrition = foodProperties.nutrition();
            float saturationModifier = foodProperties.saturation();
            float total = nutrition + nutrition * saturationModifier * 2;
            // 原版的熟牛肉之类的一般在 20 左右（除了迷之炖菜为 34.2）
            int point = Math.round(total) / MAX_PROBABILITY;
            float tailPoint = total - point * MAX_PROBABILITY;
            if (0 < tailPoint && maid.getRandom().nextInt(MAX_PROBABILITY) < tailPoint) {
                point = point + 1;
            }
            maid.components().favorability.apply(Type.HOME_MEAL, point);
            if (point > 0) {
                NetworkHandler.sendToNearby(maid, new SpawnParticlePackage(maid.getId(), SpawnParticlePackage.Type.HEART, stack.getUseDuration(maid)));
            }
        }
    }
}
