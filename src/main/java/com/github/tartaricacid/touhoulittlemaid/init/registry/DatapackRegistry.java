package com.github.tartaricacid.touhoulittlemaid.init.registry;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.datapack.resources.KaomojiDataReloadListener;
import com.github.tartaricacid.touhoulittlemaid.datapack.resources.SkillsDataReloadListener;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID)
public class DatapackRegistry {
    @SubscribeEvent
    public static void onAddReloadListenerEvent(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "kaomoji"), new KaomojiDataReloadListener());
        event.addListener(Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "skill"), new SkillsDataReloadListener());
    }
}
