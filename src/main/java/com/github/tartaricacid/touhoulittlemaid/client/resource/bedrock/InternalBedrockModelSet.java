package com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock;

import com.github.tartaricacid.simplebedrockmodel.SimpleBedrockModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

public class InternalBedrockModelSet<T> extends SimplePreparableReloadListener<Void> {
    private Map<Identifier, T> models = ImmutableMap.of();
    private Map<Identifier, Function<InputStream, ? extends T>> knowLocations = Maps.newHashMap();

    void addModel(Identifier location, Function<InputStream, ? extends T> function) {
        this.knowLocations.put(location, function);
    }

    void immutableKnowLocations() {
        this.knowLocations = ImmutableMap.copyOf(knowLocations);
    }

    @Override
    protected Void prepare(ResourceManager manager, ProfilerFiller filler) {
        this.models = Maps.newHashMap();
        this.knowLocations.keySet().forEach(location -> {
            // 将 ID 转换成实际模型文件路径，默认是 <namespace>:models/<path>.json
            Identifier path = Identifier.fromNamespaceAndPath(location.getNamespace(), "models/" + location.getPath() + ".json");
            Function<InputStream, ? extends T> modelFunction = knowLocations.get(location);
            manager.getResource(path).ifPresentOrElse(model -> {
                SimpleBedrockModel.LOGGER.info("Loading bedrock model file: {}", path);
                try (InputStream stream = model.open()) {
                    this.models.put(location, modelFunction.apply(stream));
                } catch (IOException e) {
                    SimpleBedrockModel.LOGGER.error("Failed to load model file: {}", path, e);
                }
            }, () -> SimpleBedrockModel.LOGGER.error("Not found model file: {}", path));
        });
        return null;
    }

    @Override
    protected void apply(Void unused, ResourceManager manager, ProfilerFiller filler) {
        this.models = ImmutableMap.copyOf(models);
    }

    Map<Identifier, T> getModels() {
        return models;
    }
}
