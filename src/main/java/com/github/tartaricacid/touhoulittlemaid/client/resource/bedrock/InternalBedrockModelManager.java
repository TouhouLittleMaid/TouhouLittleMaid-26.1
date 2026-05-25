package com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockEntityModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public class InternalBedrockModelManager {
    public static @Nullable InternalBedrockModelManager INSTANCE = null;

    private final InternalBedrockModelSet<SimpleBedrockModel<?>> modelSet = new InternalBedrockModelSet<>();
    private final InternalBedrockModelSet<SimpleBedrockEntityModel<? extends EntityRenderState>> entityModelSet = new InternalBedrockModelSet<>();

    public static InternalBedrockModelManager create() {
        InternalBedrockModelManager manager = new InternalBedrockModelManager();

        InternalBedrockModelRegistry.MODELS.forEach(manager.modelSet::addModel);
        InternalBedrockModelRegistry.ENTITY_MODELS.forEach(manager.entityModelSet::addModel);

        // 将注册冻结
        manager.modelSet.immutableKnowLocations();
        manager.entityModelSet.immutableKnowLocations();

        return manager;
    }

    public InternalBedrockModelSet<SimpleBedrockModel<?>> getModelSet() {
        return modelSet;
    }

    public InternalBedrockModelSet<SimpleBedrockEntityModel<? extends EntityRenderState>> getEntityModelSet() {
        return entityModelSet;
    }

    public SimpleBedrockModel<?> getModel(Identifier location) {
        return modelSet.getModels().get(location);
    }

    public SimpleBedrockEntityModel<? extends EntityRenderState> getEntityModel(Identifier location) {
        return entityModelSet.getModels().get(location);
    }
}
