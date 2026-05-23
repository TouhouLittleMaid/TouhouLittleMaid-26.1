package com.github.tartaricacid.touhoulittlemaid.client.resource.loader;

import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.BedrockModelParser;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoContainer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;

final class ChairPackLoader {
    private static final Marker MARKER = MarkerManager.getMarker("ChairPackLoader");

    static void loadPack(ResourceAccessor accessor, String domain) {
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loading...");

        String path = CustomPackLoader.assetPath(domain, CustomPackLoader.CHAIR_MODELS.getJsonFileName());
        if (!accessor.exists(path)) {
            return;
        }
        try (InputStream stream = accessor.open(path)) {
            CustomModelPack<ChairModelInfo> pack = CustomPackLoader.GSON.fromJson(
                    new InputStreamReader(stream, StandardCharsets.UTF_8),
                    new TypeToken<CustomModelPack<ChairModelInfo>>() {
                    }.getType());
            pack.decorate(domain);
            if (pack.getIcon() != null) {
                CustomPackLoader.registerTexture(accessor, pack.getIcon());
            }
            for (ChairModelInfo info : pack.getModelList()) {
                if (info.isGeckoModel()) {
                    loadGeckoChairModelElement(accessor, info);
                } else {
                    loadChairModelElement(accessor, info);
                }
                LOGGER.debug(MARKER, "Loaded model: {}", info.getModel());
            }
            CustomPackLoader.CHAIR_MODELS.addPack(pack);
        } catch (IOException e) {
            LOGGER.warn(MARKER, "Fail to load model pack in domain {}", domain, e);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(MARKER, "Fail to parse model pack in domain {}", domain, e);
        }

        LOGGER.debug(MARKER, "Touhou little maid mod's model is loaded");
    }

    private static void loadChairModelElement(ResourceAccessor accessor, ChairModelInfo info) {
        EntityChairModel modelJson = BedrockModelParser.loadChairModel(accessor, info.getModel());
        CustomPackLoader.registerTexture(accessor, info.getTexture());
        if (modelJson != null) {
            String id = info.getModelId().toString();
            CustomPackLoader.CHAIR_MODELS.putModel(id, modelJson);
            CustomPackLoader.CHAIR_MODELS.putAnimation(id, resolveAnimations(info));
            CustomPackLoader.CHAIR_MODELS.putInfo(id, info);
        }
    }

    private static void loadGeckoChairModelElement(ResourceAccessor accessor, ChairModelInfo info) throws IOException {
        BedrockModelParser.loadGeckoModelElement(accessor, info, GeckoContainer.Type.CHAIR);
        CustomPackLoader.CHAIR_MODELS.putInfo(info.getModelId().toString(), info);
    }

    private static List<IAnimation<EntityChairRenderState>> resolveAnimations(ChairModelInfo info) {
        List<IAnimation<EntityChairRenderState>> animations = new ArrayList<>();
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
