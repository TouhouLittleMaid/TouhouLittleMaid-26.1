package com.github.tartaricacid.touhoulittlemaid.client.resource.loader;

import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.AbstractClientModels;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.IModelInfo;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;


final class PackLoaderHelper {
    @FunctionalInterface
    interface ModelElementConsumer<I extends IModelInfo> {
        void accept(ResourceAccessor accessor, I info) throws IOException;
    }

    static <M, I extends IModelInfo, A extends EntityRenderState> void loadPack(
            AbstractClientModels<M, I, A> models,
            ResourceAccessor accessor,
            String domain,
            Marker marker,
            ModelElementConsumer<I> elementConsumer
    ) {
        LOGGER.debug(marker, "Touhou little maid mod's model is loading...");

        String path = CustomPackLoader.assetPath(domain, models.getJsonFileName());
        if (!accessor.exists(path)) {
            return;
        }

        try (InputStream stream = accessor.open(path)) {
            CustomModelPack<I> pack = CustomPackLoader.GSON.fromJson(
                    new InputStreamReader(stream, StandardCharsets.UTF_8),
                    new TypeToken<CustomModelPack<I>>() {
                    }.getType());

            pack.decorate(domain);

            if (pack.getIcon() != null) {
                CustomPackLoader.registerTexture(accessor, pack.getIcon());
            }

            for (I info : pack.getModelList()) {
                elementConsumer.accept(accessor, info);
                LOGGER.debug(marker, "Loaded model: {}", info.getModel());
            }

            models.addPack(pack);
        } catch (IOException e) {
            LOGGER.warn(marker, "Fail to load model pack in domain {}", domain, e);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(marker, "Fail to parse model pack in domain {}", domain, e);
        }

        LOGGER.debug(marker, "Touhou little maid mod's model is loaded");
    }


    static <T extends EntityRenderState> List<IAnimation<T>> resolveAnimations(IModelInfo info) {
        List<IAnimation<T>> animations = new ArrayList<>();
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
