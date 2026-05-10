package com.github.tartaricacid.touhoulittlemaid.client.resource.pojo;


import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.List;

public interface IModelInfo {
    static Identifier createCacheIconId(Identifier modelId) {
        String namespace = modelId.getNamespace();
        String path = modelId.getPath();
        return Identifier.fromNamespaceAndPath(namespace, path + "/cache");
    }

    Identifier getModelId();

    Identifier getCacheIconId();

    String getName();

    Identifier getModel();

    boolean isGeckoModel();

    @Nullable
    List<Identifier> getAnimation();

    Identifier getTexture();

    @Nullable
    List<Identifier> getExtraTextures();

    List<String> getDescription();

    float getRenderItemScale();

    <T extends IModelInfo> T decorate();

    <T extends IModelInfo> T extra(Identifier newModelId, Identifier texture);
}
