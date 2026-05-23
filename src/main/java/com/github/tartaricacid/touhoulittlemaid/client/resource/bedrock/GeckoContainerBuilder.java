package com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.event.client.DefaultGeckoAnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.ControllerResource;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.collection.ChairControllerCollection;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.collection.MaidControllerCollection;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.Animation;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.MolangParser;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.file.AnimationFile;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.Converter;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.FormatVersion;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.pojo.RawGeoModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.raw.tree.RawGeometryTree;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.GeoBuilder;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.ConditionManager;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoAsset;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoContainer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoLibCache;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.json.JsonAnimationUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.github.tartaricacid.touhoulittlemaid.api.event.client.DefaultGeckoAnimationEvent.AnimationType.*;

public class GeckoContainerBuilder {
    public static final Object2ReferenceOpenHashMap<DefaultGeckoAnimationEvent.AnimationType, Identifier> DEFAULT_ANIMATION_FILES = new Object2ReferenceOpenHashMap<>(
            new DefaultGeckoAnimationEvent.AnimationType[]{
                    MAID,
                    TAC,
                    ISS,
                    IM,
                    CHAIR
            },
            new Identifier[]{
                    Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid.animation.json"),
                    Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/tac.animation.json"),
                    Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/iss.animation.json"),
                    Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/im.animation.json"),
                    Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/chair.animation.json")
            });

    public static final Object2ReferenceOpenHashMap<String, Animation> DEFAULT_MAID_ANIMATIONS = new Object2ReferenceOpenHashMap<>();
    public static final Object2ReferenceOpenHashMap<String, Animation> DEFAULT_CHAIR_ANIMATIONS = new Object2ReferenceOpenHashMap<>();

    public static void reload() {
        clearAllCache();
        loadDefaultAnimation();
    }

    public static <T> void registerModelContainer(Identifier id, InputStreamSupplier geoStreamGetter,
                                                  InputStreamGetter<T> animStreamGetter,
                                                  List<T> animationFileIds,
                                                  Identifier texture,
                                                  GeckoContainer.Type type) throws IOException {
        GeoModel geo;
        try (InputStream geoStream = geoStreamGetter.get()) {
            geo = GeckoContainerBuilder.registerGeo(geoStream);
        }

        var animationData = new AnimationFile();
        animationData.animations().putAll(type == GeckoContainer.Type.MAID ? DEFAULT_MAID_ANIMATIONS : DEFAULT_CHAIR_ANIMATIONS);
        for (var animationFileId : animationFileIds) {
            InputStream animStream = animStreamGetter.get(animationFileId);
            if (animStream != null) {
                try (animStream) {
                    animationData.animations().putAll(getAnimationFile(animStream).animations());
                }
            }
        }
        ConditionManager manager = new ConditionManager();
        for (var name : animationData.animations().keySet()) {
            manager.addTest(name);
        }

        var controllerResource = new ControllerResource(
                animationData.animations(),
                manager.armor,
                Object2ReferenceMaps.emptyMap(),
                Object2ReferenceMaps.emptyMap()
        );
        var controllerFactory = type == GeckoContainer.Type.MAID ?
                MaidControllerCollection.build(controllerResource) :
                ChairControllerCollection.build(controllerResource);

        // TODO: 控制器、音频、molang 函数读取
        var asset = new GeckoAsset(Object2ReferenceMaps.emptyMap(), Object2ReferenceMaps.emptyMap(), Object2ReferenceMaps.emptyMap());
        GeckoLibCache.getInstance().getModels().put(id,
                new GeckoContainer(geo, animationData, controllerFactory, Object2ReferenceMaps.emptyMap(), manager, texture, asset, type));
    }

    private static GeoModel registerGeo(InputStream inputStream) {
        RawGeoModel rawModel = Converter.fromInputStream(inputStream);
        if (rawModel.getFormatVersion() == FormatVersion.NEW) {
            RawGeometryTree rawGeometryTree = RawGeometryTree.build(rawModel);
            return GeoBuilder.constructGeoModel(rawGeometryTree);
        }
        return null;
    }

    public static AnimationFile getAnimationFile(InputStream stream) {
        AnimationFile animationFile = new AnimationFile();
        MolangParser parser = GeckoLibCache.getInstance().parser;
        JsonObject jsonObject = GsonHelper.fromJson(CustomPackLoader.GSON, new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : JsonAnimationUtils.getAnimations(jsonObject)) {
            String animationName = entry.getKey();
            Animation animation;
            try {
                animation = JsonAnimationUtils.deserializeJsonToAnimation(JsonAnimationUtils.getAnimation(jsonObject, animationName), parser);
                animationFile.animations().put(animationName, animation);
            } catch (ChainedJsonException e) {
                TouhouLittleMaid.LOGGER.error("Failed to load animation {}: {}", animationName, e.getMessage());
            }
        }
        return animationFile;
    }

    private static void clearAllCache() {
        GeckoLibCache.getInstance().getModels().clear();
    }

    private static void loadDefaultAnimation() {
        DEFAULT_MAID_ANIMATIONS.clear();
        DEFAULT_CHAIR_ANIMATIONS.clear();

        var animationFiles = new EnumMap<DefaultGeckoAnimationEvent.AnimationType, AnimationFile>(DefaultGeckoAnimationEvent.AnimationType.class);
        for (var entry : DEFAULT_ANIMATION_FILES.entrySet()) {
            try (InputStream stream = Minecraft.getInstance().getResourceManager().open(entry.getValue())) {
                animationFiles.put(entry.getKey(), getAnimationFile(stream));
            } catch (IOException e) {
                animationFiles.put(entry.getKey(), new AnimationFile());
                TouhouLittleMaid.LOGGER.error("Failed to load default maid animation file:", e);
            }
        }

        NeoForge.EVENT_BUS.post(new DefaultGeckoAnimationEvent(animationFiles));

        for (var type : DEFAULT_ANIMATION_FILES.keySet()) {
            if (type == CHAIR) {
                DEFAULT_CHAIR_ANIMATIONS.putAll(animationFiles.get(CHAIR).animations());
            } else {
                DEFAULT_MAID_ANIMATIONS.putAll(animationFiles.get(type).animations());
            }
        }
    }

    @FunctionalInterface
    public interface InputStreamGetter<T> {
        @Nullable
        InputStream get(T id) throws IOException;
    }

    @FunctionalInterface
    public interface InputStreamSupplier {
        InputStream get() throws IOException;
    }
}
