package com.github.tartaricacid.touhoulittlemaid.client.resource.loader;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.BedrockModelParser;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.MaidModels;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoContainer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;

final class MaidPackLoader {
    private static final Marker MARKER = MarkerManager.getMarker("MaidPackLoader");

    static void loadPack(ResourceAccessor accessor, String domain) {
        PackLoaderHelper.loadPack(
                CustomPackLoader.MAID_MODELS,
                accessor, domain, MARKER,
                MaidPackLoader::loadMaidElement
        );
    }

    private static void loadMaidElement(ResourceAccessor accessor, MaidModelInfo info) throws IOException {
        if (info.isGeckoModel()) {
            loadGeckoMaidModelElement(accessor, info);
        } else {
            loadMaidModelElement(accessor, info);
        }
    }

    private static void loadMaidModelElement(ResourceAccessor accessor, MaidModelInfo info) {
        EntityMaidModel modelJson = BedrockModelParser.loadMaidModel(accessor, info.getModel());
        CustomPackLoader.registerTexture(accessor, info.getTexture());
        if (modelJson != null) {
            MaidModelInfo.EasterEgg easterEgg = info.getEasterEgg();
            if (easterEgg != null && StringUtils.isNotBlank(easterEgg.getTag())) {
                putEasterEggData(info, modelJson);
            } else {
                putModelData(info, modelJson);
            }
        }
    }

    private static void loadGeckoMaidModelElement(ResourceAccessor accessor, MaidModelInfo info) throws IOException {
        BedrockModelParser.loadGeckoModelElement(accessor, info, GeckoContainer.Type.MAID);
        if (info.getEasterEgg() != null && StringUtils.isNotBlank(info.getEasterEgg().getTag())) {
            putEasterEggData(info, null);
        } else {
            CustomPackLoader.MAID_MODELS.putInfo(info.getModelId().toString(), info);
        }
    }

    @SuppressWarnings("all")
    private static void putEasterEggData(MaidModelInfo info, @Nullable EntityMaidModel modelJson) {
        var animations = PackLoaderHelper.<EntityMaidRenderState>resolveAnimations(info);
        CustomPackLoader.MAID_MODELS.putAnimation(info.getModelId().toString(), animations);

        MaidModels.ModelData data = new MaidModels.ModelData(modelJson, info);
        var easterEgg = info.getEasterEgg();
        if (easterEgg.isEncrypt()) {
            CustomPackLoader.MAID_MODELS.putEasterEggEncryptTagModel(easterEgg.getTag(), data);
        } else {
            CustomPackLoader.MAID_MODELS.putEasterEggNormalTagModel(easterEgg.getTag(), data);
        }
    }

    private static void putModelData(MaidModelInfo info, EntityMaidModel modelJson) {
        String id = info.getModelId().toString();
        var animations = PackLoaderHelper.<EntityMaidRenderState>resolveAnimations(info);
        CustomPackLoader.MAID_MODELS.putModel(id, modelJson);
        CustomPackLoader.MAID_MODELS.putAnimation(id, animations);
        CustomPackLoader.MAID_MODELS.putInfo(id, info);
    }
}
