package com.github.tartaricacid.touhoulittlemaid.event.food;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAfterEatEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig.MAID_EATEN_RETURN_CONTAINER_LIST;

@EventBusSubscriber
public class RemainFoodEatenEvent {
    @SubscribeEvent
    public static void onAfterMaidEat(MaidAfterEatEvent event) {
        ItemStack foodAfterEat = event.getFoodAfterEat();
        if (!foodAfterEat.isEmpty()) {
            ItemStackTemplate remainder = foodAfterEat.getCraftingRemainder();
            if (remainder == null) {
                return;
            }
            var stack = remainder.create();

            if (stack.isEmpty()) {
                String itemId = ItemsUtil.getItemId(foodAfterEat.getItem());
                for (List<String> strings : MAID_EATEN_RETURN_CONTAINER_LIST.get()) {
                    if (strings.get(0).equals(itemId)) {
                        stack = getItemStack(strings.get(1));
                        break;
                    }
                }
            }

            if (!stack.isEmpty()) {
                EntityMaid maid = event.getMaid();
                var availableInv = maid.getAvailableInv(false);

                try (Transaction tx = Transaction.openRoot()) {
                    ItemResource resource = ItemResource.of(stack);
                    int insert = availableInv.insert(resource, stack.count(), tx);
                    // 如果女仆背包满了，掉落在地上
                    if (insert < stack.count()) {
                        ItemStack droppedStack = stack.copyWithCount(stack.count() - insert);
                        ItemEntity itemEntity = new ItemEntity(maid.level, maid.getX(), maid.getY(), maid.getZ(), droppedStack);
                        maid.level.addFreshEntity(itemEntity);
                    }
                    tx.commit();
                }
            }
        }
    }

    private static ItemStack getItemStack(String itemId) {
        Identifier resourceLocation = Identifier.parse(itemId);
        Item value = BuiltInRegistries.ITEM.getValue(resourceLocation);
        return new ItemStack(value);
    }
}
