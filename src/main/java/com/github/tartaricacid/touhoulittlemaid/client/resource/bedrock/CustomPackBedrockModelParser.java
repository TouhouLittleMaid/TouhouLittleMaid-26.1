package com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockVersion;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.IModelInfo;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoContainer;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;

public final class CustomPackBedrockModelParser {
    private static final Marker MARKER = MarkerManager.getMarker("CustomPackBedrockModelParser");

    @Nullable
    public static EntityMaidModel loadMaidModel(ResourceAccessor accessor, Identifier modelLocation) {
        return loadModel(accessor, modelLocation, EntityMaidModel::new);
    }

    @Nullable
    public static EntityChairModel loadChairModel(ResourceAccessor accessor, Identifier modelLocation) {
        return loadModel(accessor, modelLocation, EntityChairModel::new);
    }

    @Nullable
    private static <M> M loadModel(ResourceAccessor accessor, Identifier modelLocation,
                                   ModelFactory<M> factory) {
        String path = CustomPackLoader.assetPath(modelLocation);
        if (!accessor.exists(path)) {
            return null;
        }
        try (InputStream stream = accessor.open(path)) {
            return parseModel(stream, modelLocation, factory);
        } catch (IOException ioe) {
            LOGGER.warn(MARKER, "Failed to load model: {}", modelLocation, ioe);
        }
        return null;
    }

    @Nullable
    private static <M> M parseModel(InputStream stream, Identifier modelLocation,
                                    ModelFactory<M> factory) {
        BedrockModelPOJO pojo = CustomPackLoader.GSON.fromJson(
                new InputStreamReader(stream, StandardCharsets.UTF_8),
                BedrockModelPOJO.class
        );

        if (BedrockVersion.isLegacyVersion(pojo)) {
            if (pojo.getGeometryModelLegacy() != null) {
                return factory.create(pojo, BedrockVersion.LEGACY);
            }
            LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
            return null;
        }

        if (BedrockVersion.isNewVersion(pojo)) {
            if (pojo.getGeometryModelNew() != null) {
                return factory.create(pojo, BedrockVersion.NEW);
            }
            LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
            return null;
        }

        LOGGER.warn(MARKER, "{} model version is not 1.10.0 or 1.12.0", modelLocation);
        return null;
    }

    public static void loadGeckoModelElement(ResourceAccessor accessor, IModelInfo info,
                                             GeckoContainer.Type type) throws IOException {
        Identifier uid = info.getModelId();
        Identifier modelLocation = info.getModel();
        String modelPath = CustomPackLoader.assetPath(modelLocation);
        if (!accessor.exists(modelPath)) {
            return;
        }
        List<Identifier> animation = Objects.requireNonNullElse(info.getAnimation(), List.of());
        CustomPackLoader.registerTexture(accessor, info.getTexture());
        GeckoContainerBuilder.registerModelContainer(uid, () -> accessor.open(modelPath),
                id -> {
                    String animationPath = CustomPackLoader.assetPath(id);
                    if (!accessor.exists(animationPath)) {
                        return null;
                    }
                    return accessor.open(animationPath);
                },
                animation,
                info.getTexture(),
                type
        );
    }

    @FunctionalInterface
    private interface ModelFactory<M> {
        M create(BedrockModelPOJO pojo, BedrockVersion version);
    }
}
