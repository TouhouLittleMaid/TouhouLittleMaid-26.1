package com.github.tartaricacid.touhoulittlemaid.event.food;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAfterEatEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseRemainder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@EventBusSubscriber
public class ConvertFoodEatenEvent {
    @SubscribeEvent
    public static void onAfterMaidEat(MaidAfterEatEvent event) {
        ItemStack foodAfterEat = event.getFoodAfterEat();
        EntityMaid maid = event.getMaid();

        FoodProperties foodProperties = foodAfterEat.get(DataComponents.FOOD);
        UseRemainder useRemainder = foodAfterEat.get(DataComponents.USE_REMAINDER);

        if (!foodAfterEat.isEmpty() && foodProperties != null && useRemainder != null) {
            ItemStack convertedStack = useRemainder.convertInto().create();
            if (!convertedStack.isEmpty()) {
                var availableInv = maid.getAvailableInv(false);
                try (Transaction tx = Transaction.openRoot()) {
                    ItemResource resource = ItemResource.of(convertedStack);
                    int insert = availableInv.insert(resource, convertedStack.count(), tx);
                    // 如果女仆背包满了，掉落在地上
                    if (insert < convertedStack.count()) {
                        ItemStack droppedStack = convertedStack.copyWithCount(convertedStack.count() - insert);
                        ItemEntity itemEntity = new ItemEntity(maid.level, maid.getX(), maid.getY(), maid.getZ(), droppedStack);
                        maid.level.addFreshEntity(itemEntity);
                    }
                    tx.commit();
                }
            }
        }
    }
}
