package com.github.tartaricacid.touhoulittlemaid.event;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.extensions.BlockAltarExtensions;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID)
public class ClientExtensionsEvent {
    @SubscribeEvent
    public static void RegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerBlock(new BlockAltarExtensions(), InitBlocks.ALTAR.get());
    }
}
