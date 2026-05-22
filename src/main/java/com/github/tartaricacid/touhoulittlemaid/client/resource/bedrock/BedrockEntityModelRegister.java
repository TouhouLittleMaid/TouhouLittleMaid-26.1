package com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockEntityModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID, value = Dist.CLIENT)
public class BedrockEntityModelRegister {
    public static BedrockEntityModelRegister INSTANCE = null;

    private final BedrockModelSet<SimpleBedrockModel<?>> modelSet = new BedrockModelSet<>();
    private final BedrockModelSet<SimpleBedrockEntityModel<? extends EntityRenderState>> entityModelSet = new BedrockModelSet<>();

    @SubscribeEvent
    public static void onRegisterClientReloadListenersEvent(AddClientReloadListenersEvent event) {
        INSTANCE = new BedrockEntityModelRegister();

        BedrockModelLoader.MODELS.forEach((location, function)
                -> INSTANCE.modelSet.addModel(location, function));
        BedrockModelLoader.ENTITY_MODELS.forEach((location, function)
                -> INSTANCE.entityModelSet.addModel(location, function));

        // 将注册冻结
        INSTANCE.modelSet.immutableKnowLocations();
        INSTANCE.entityModelSet.immutableKnowLocations();

        // 添加到最前面，避免实体读取模型时模型还没加载完成
        event.addListener(Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock_model"), INSTANCE.modelSet);
        event.addListener(Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock_entity_model"), INSTANCE.entityModelSet);
    }

    public SimpleBedrockModel<?> getModel(Identifier location) {
        return modelSet.getModels().get(location);
    }

    public SimpleBedrockEntityModel<? extends EntityRenderState> getEntityModel(Identifier location) {
        return entityModelSet.getModels().get(location);
    }
}
