package com.github.tartaricacid.touhoulittlemaid.client.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.input.DismountBroomKey;
import com.github.tartaricacid.touhoulittlemaid.client.input.STTChatKey;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = TouhouLittleMaid.MOD_ID)
public class KeyMappingRegister {
    public static final KeyMapping.Category MAID_CATEGORY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "main")
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(MAID_CATEGORY);

        event.register(STTChatKey.STT_CHAT_KEY);
        event.register(DismountBroomKey.DISMOUNT_KEY);
    }
}
