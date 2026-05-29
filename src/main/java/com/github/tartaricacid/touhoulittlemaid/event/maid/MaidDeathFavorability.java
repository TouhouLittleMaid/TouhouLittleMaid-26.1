package com.github.tartaricacid.touhoulittlemaid.event.maid;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl.FavorabilityComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class MaidDeathFavorability {
    @SubscribeEvent
    public static void onDeath(MaidDeathEvent event) {
        FavorabilityComponent manager = event.getMaid().components.favorability;
        manager.apply(Type.DEATH);
    }
}
