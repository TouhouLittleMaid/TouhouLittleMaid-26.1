package com.github.tartaricacid.touhoulittlemaid.event.maid;

import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.util.MaidItemStorageHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.apache.commons.lang3.function.Consumers;

@EventBusSubscriber
public final class SlabClickEvent {
    @SubscribeEvent
    public static void onInteract(InteractMaidEvent event) {
        Player player = event.getPlayer();
        EntityMaid maid = event.getMaid();
        ItemStack stack = event.getStack();

        if (!stack.is(InitItems.SMART_SLAB_EMPTY)) {
            return;
        }

        ItemStack empty = InitItems.SMART_SLAB_EMPTY.get().getDefaultInstance();
        if (!player.getCooldowns().isOnCooldown(empty)) {
            ItemStack output = InitItems.SMART_SLAB_HAS_MAID.get().getDefaultInstance();
            MaidItemStorageHelper.saveMaid(output, maid, Consumers.nop());

            maid.playSound(SoundEvents.PLAYER_SPLASH, 1.0F, maid.level.getRandom().nextFloat() * 0.1F + 0.9F);
            maid.spawnExplosionParticle();
            maid.discard();

            player.setItemInHand(InteractionHand.MAIN_HAND, output);
            player.getCooldowns().addCooldown(output, 20);
        }

        event.setCanceled(true);
    }
}
