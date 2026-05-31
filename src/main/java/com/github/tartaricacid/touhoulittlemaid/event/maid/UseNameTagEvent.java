package com.github.tartaricacid.touhoulittlemaid.event.maid;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID, value = Dist.DEDICATED_SERVER)
public final class UseNameTagEvent {
    @SubscribeEvent
    public static void onInteractServer(InteractMaidEvent event) {
        ItemStack stack = event.getStack();
        Player player = event.getPlayer();
        EntityMaid maid = event.getMaid();

        if (stack.get(DataComponents.CUSTOM_NAME) == null
            && player.getMainHandItem().getItem() == Items.NAME_TAG
            && player.equals(maid.getOwner())
        ) {
            event.setCanceled(true);
        }
    }
}
