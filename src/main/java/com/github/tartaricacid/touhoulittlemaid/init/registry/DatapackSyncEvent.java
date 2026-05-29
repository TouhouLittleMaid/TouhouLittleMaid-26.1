package com.github.tartaricacid.touhoulittlemaid.init.registry;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID)
public class DatapackSyncEvent {
    @SubscribeEvent
    public static void onDatapackSyncEvent(OnDatapackSyncEvent event) {
        event.sendRecipes(InitRecipes.ALTAR_RECIPE.get());
    }
}
