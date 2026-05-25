package com.github.tartaricacid.touhoulittlemaid.client.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelManager;
import com.github.tartaricacid.touhoulittlemaid.client.resource.listener.CustomPackReloadListener;
import com.github.tartaricacid.touhoulittlemaid.client.resource.listener.EmojiReloadListener;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = TouhouLittleMaid.MOD_ID)
public final class ClientReloadListenerRegistry {
    private static final Identifier BEDROCK_MODEL = id("bedrock_model");
    private static final Identifier BEDROCK_ENTITY_MODEL = id("bedrock_entity_model");
    private static final Identifier CUSTOM_PACK = id("custom_pack");
    private static final Identifier MAID_EMOJI_RELOAD = id("maid_emoji_reload");

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(AddClientReloadListenersEvent event) {
        InternalBedrockModelManager.INSTANCE = InternalBedrockModelManager.create();

        event.addListener(BEDROCK_MODEL, InternalBedrockModelManager.INSTANCE.getModelSet());
        event.addListener(BEDROCK_ENTITY_MODEL, InternalBedrockModelManager.INSTANCE.getEntityModelSet());
        event.addListener(CUSTOM_PACK, new CustomPackReloadListener());
        event.addListener(MAID_EMOJI_RELOAD, new EmojiReloadListener());
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, path);
    }
}
