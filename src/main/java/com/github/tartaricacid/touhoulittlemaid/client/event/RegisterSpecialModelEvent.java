package com.github.tartaricacid.touhoulittlemaid.client.event;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.item.ChairItemRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.item.GarageKitItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterSpecialModelEvent {
    @SubscribeEvent
    public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(ChairItemRenderer.CHAIR_ITEM_RENDERER, ChairItemRenderer.Unbaked.MAP_CODEC);
        event.register(GarageKitItemRenderer.GARAGE_KIT_ITEM_RENDERER, GarageKitItemRenderer.Unbaked.MAP_CODEC);
    }
}
