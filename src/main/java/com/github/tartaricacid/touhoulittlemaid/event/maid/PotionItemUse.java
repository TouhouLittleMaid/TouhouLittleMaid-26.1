package com.github.tartaricacid.touhoulittlemaid.event.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@EventBusSubscriber
public class PotionItemUse {
    @SubscribeEvent
    public static void onMaidPotionItemUse(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof EntityMaid maid && event.getItem().getItem() instanceof PotionItem && maid.level() instanceof ServerLevel serverLevel) {
            ItemStack potionStack = event.getItem();
            // 原版药水非玩家使用后会原样返回，我们需要正确扣掉内容
            potionStack.shrink(1);
            // 说明喝的是堆叠的药水，需要主动给女仆加瓶子
            if (!potionStack.isEmpty()) {
                var inv = maid.components.item.getAvailableInv(false);
                try (Transaction tx = Transaction.openRoot()) {
                    ItemResource resource = ItemResource.of(Items.GLASS_BOTTLE);
                    int insert = inv.insert(resource, 1, tx);
                    // 如果背包满了，那就生成掉落物，预防一些改动物品堆叠的模组
                    if (insert == 0) {
                        maid.spawnAtLocation(serverLevel, new ItemStack(Items.GLASS_BOTTLE));
                    }
                    tx.commit();
                }
                event.setResultStack(potionStack);
            } else {
                event.setResultStack(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
    }
}
