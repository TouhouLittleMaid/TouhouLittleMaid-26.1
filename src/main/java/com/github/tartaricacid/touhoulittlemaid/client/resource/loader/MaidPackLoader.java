package com.github.tartaricacid.touhoulittlemaid.client.resource.loader;

import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.BedrockModelParser;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.MaidModels;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoContainer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;

final class MaidPackLoader {
    private static final Marker MARKER = MarkerManager.getMarker("MaidPackLoader");

    static void loadPack(ResourceAccessor accessor, String domain) {
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loading...");

        String path = CustomPackLoader.assetPath(domain, CustomPackLoader.MAID_MODELS.getJsonFileName());
        if (!accessor.exists(path)) {
            return;
        }
        try (InputStream stream = accessor.open(path)) {
            CustomModelPack<MaidModelInfo> pack = CustomPackLoader.GSON.fromJson(
                    new InputStreamReader(stream, StandardCharsets.UTF_8),
                    new TypeToken<CustomModelPack<MaidModelInfo>>() {
                    }.getType());
            pack.decorate(domain);
            if (pack.getIcon() != null) {
                CustomPackLoader.registerTexture(accessor, pack.getIcon());
            }
            for (MaidModelInfo maidModelItem : pack.getModelList()) {
                if (maidModelItem.isGeckoModel()) {
                    loadGeckoMaidModelElement(accessor, maidModelItem);
                } else {
                    loadMaidModelElement(accessor, maidModelItem);
                }
                LOGGER.debug(MARKER, "Loaded model: {}", maidModelItem.getModel());
            }
            CustomPackLoader.MAID_MODELS.addPack(pack);
        } catch (IOException e) {
            LOGGER.warn(MARKER, "Fail to load model pack in domain {}", domain, e);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(MARKER, "Fail to parse model pack in domain {}", domain, e);
        }

        LOGGER.debug(MARKER, "Touhou little maid mod's model is loaded");
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
        CustomPackLoader.MAID_MODELS.putAnimation(info.getModelId().toString(), resolveAnimations(info));
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
        CustomPackLoader.MAID_MODELS.putModel(id, modelJson);
        CustomPackLoader.MAID_MODELS.putAnimation(id, resolveAnimations(info));
        CustomPackLoader.MAID_MODELS.putInfo(id, info);
    }

    private static List<IAnimation<EntityMaidRenderState>> resolveAnimations(MaidModelInfo info) {
        List<IAnimation<EntityMaidRenderState>> animations = new ArrayList<>();
        List<Identifier> animationIds = info.getAnimation();
        if (animationIds == null || animationIds.isEmpty()) {
            return animations;
        }
        for (Identifier animationId : animationIds) {
            if (InnerAnimation.containsKey(animationId)) {
                animations.add(InnerAnimation.get(animationId));
            }
        }
        return animations;
    }
}
