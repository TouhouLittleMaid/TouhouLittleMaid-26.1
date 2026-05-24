package com.github.tartaricacid.touhoulittlemaid.client.resource.loader;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.BedrockModelParser;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoContainer;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.lang.reflect.Type;

final class ChairPackLoader {
    private static final Marker MARKER = MarkerManager.getMarker("ChairPackLoader");
    private static final Type PACK_TYPE = new TypeToken<CustomModelPack<ChairModelInfo>>() {
    }.getType();

    static void loadPack(ResourceAccessor accessor, String domain) {
        PackLoaderHelper.loadPack(
                CustomPackLoader.CHAIR_MODELS,
                accessor, domain,
                PACK_TYPE, MARKER,
                ChairPackLoader::loadChairElement
        );
    }

    private static void loadChairElement(ResourceAccessor accessor, ChairModelInfo info) throws IOException {
        if (info.isGeckoModel()) {
            loadGeckoChairModelElement(accessor, info);
        } else {
            loadChairModelElement(accessor, info);
        }
    }

    private static void loadChairModelElement(ResourceAccessor accessor, ChairModelInfo info) {
        EntityChairModel modelJson = BedrockModelParser.loadChairModel(accessor, info.getModel());
        CustomPackLoader.registerTexture(accessor, info.getTexture());
        if (modelJson != null) {
            String id = info.getModelId().toString();
            var animations = PackLoaderHelper.<EntityChairRenderState>resolveAnimations(info);
            CustomPackLoader.CHAIR_MODELS.putModel(id, modelJson);
            CustomPackLoader.CHAIR_MODELS.putAnimation(id, animations);
            CustomPackLoader.CHAIR_MODELS.putInfo(id, info);
        }
    }

    private static void loadGeckoChairModelElement(ResourceAccessor accessor, ChairModelInfo info) throws IOException {
        BedrockModelParser.loadGeckoModelElement(accessor, info, GeckoContainer.Type.CHAIR);
        CustomPackLoader.CHAIR_MODELS.putInfo(info.getModelId().toString(), info);
    }
}
