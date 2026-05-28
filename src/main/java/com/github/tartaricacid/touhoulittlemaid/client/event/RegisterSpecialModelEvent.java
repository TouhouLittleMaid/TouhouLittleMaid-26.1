package com.github.tartaricacid.touhoulittlemaid.client.event;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.TileEntityItemStackChairRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.TileEntityItemStackGarageKitRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.TileEntityItemStackPicnicBasketRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterSpecialModelEvent {
    @SubscribeEvent
    public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(TileEntityItemStackChairRenderer.CHAIR_ITEM_RENDERER, TileEntityItemStackChairRenderer.Unbaked.MAP_CODEC);
        event.register(TileEntityItemStackGarageKitRenderer.GARAGE_KIT_ITEM_RENDERER, TileEntityItemStackGarageKitRenderer.Unbaked.MAP_CODEC);
        event.register(TileEntityItemStackPicnicBasketRenderer.PICNIC_BASKET_ITEM_RENDERER, TileEntityItemStackPicnicBasketRenderer.Unbaked.MAP_CODEC);
    }
}
