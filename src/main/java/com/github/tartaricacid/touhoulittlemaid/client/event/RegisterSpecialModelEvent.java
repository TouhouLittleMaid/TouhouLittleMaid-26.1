package com.github.tartaricacid.touhoulittlemaid.client.event;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.BlockEntityItemStackChairRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.BlockEntityItemStackGarageKitRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.BlockEntityItemStackPicnicBasketRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterSpecialModelEvent {
    @SubscribeEvent
    public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(BlockEntityItemStackChairRenderer.CHAIR_ITEM_RENDERER, BlockEntityItemStackChairRenderer.Unbaked.MAP_CODEC);
        event.register(BlockEntityItemStackGarageKitRenderer.GARAGE_KIT_ITEM_RENDERER, BlockEntityItemStackGarageKitRenderer.Unbaked.MAP_CODEC);
        event.register(BlockEntityItemStackPicnicBasketRenderer.PICNIC_BASKET_ITEM_RENDERER, BlockEntityItemStackPicnicBasketRenderer.Unbaked.MAP_CODEC);
    }
}
