package com.github.tartaricacid.touhoulittlemaid.client.renderer.sections.dynamic;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.sections.RenderTypeExtension;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.sections.events.ReloadDynamicChunkBufferEvent;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.sections.events.SectionGeometryRenderTypeEvents;
import com.github.tartaricacid.touhoulittlemaid.compat.iris.IrisCompat;
import com.github.tartaricacid.touhoulittlemaid.compat.sodium.SodiumCompat;
import com.github.tartaricacid.touhoulittlemaid.util.EntryStreams;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.ModLoader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Argon4W
 */
public class DynamicChunkBuffers implements ResourceManagerReloadListener {
    public static final AtomicInteger CHUNK_LAYER_IDS = new AtomicInteger(RenderType.chunkBufferLayers().size());
    public static final Function<ResourceLocation, RenderType> CUTOUT = Util.memoize(textureResourceLocation -> Util.make(SodiumCompat.isInstalled() ? RenderType.create("eyelib_sodium_dummy_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, RenderType.CompositeState.builder().setTransparencyState(RenderStateShard.NO_TRANSPARENCY).setCullState(RenderStateShard.NO_CULL).setLightmapState(RenderStateShard.LIGHTMAP).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(true)) : SectionGeometryRenderTypeEvents.getEntityCutoutNoCull(textureResourceLocation), renderType -> ((RenderTypeExtension) renderType).eyelib$setChunkLayerId(CHUNK_LAYER_IDS.getAndIncrement())));
    public static final Function<ResourceLocation, RenderType> TRANSLUCENT = Util.memoize(textureResourceLocation -> Util.make(SodiumCompat.isInstalled() ? RenderType.create("eyelib_sodium_dummy_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, RenderType.CompositeState.builder().setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setCullState(RenderStateShard.NO_CULL).setLightmapState(RenderStateShard.LIGHTMAP).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(true)) : SectionGeometryRenderTypeEvents.getEntityTranslucent(textureResourceLocation), renderType -> ((RenderTypeExtension) renderType).eyelib$setChunkLayerId(CHUNK_LAYER_IDS.getAndIncrement())));
    public static final Map<ResourceLocation, RenderType> DYNAMIC_CUTOUT_LAYERS = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, RenderType> DYNAMIC_TRANSLUCENT_LAYERS = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, Function<RenderType, RenderType>> DYNAMIC_MULTI_LAYERS = new ConcurrentHashMap<>();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        ModLoader.postEvent(new ReloadDynamicChunkBufferEvent());
    }

    public static <T extends Entity> RenderType markCutoutChunkBuffer(T entity) {
        return markCutoutChunkBuffer(getEntityTextureResourceLocation(entity));
    }

    public static <T extends Entity> RenderType markTranslucentChunkBuffer(T entity) {
        return markTranslucentChunkBuffer(getEntityTextureResourceLocation(entity));
    }

    public static <T extends Entity> RenderType markCutoutChunkBuffer(EntityType<? extends T> entityType) {
        return markCutoutChunkBuffer(getEntityTextureResourceLocation(entityType));
    }

    public static <T extends Entity> RenderType markTranslucentChunkBuffer(EntityType<? extends T> entityType) {
        return markTranslucentChunkBuffer(getEntityTextureResourceLocation(entityType));
    }

    public static RenderType markCutoutChunkBuffer(ResourceLocation textureResourceLocation) {
        return addSodiumCutoutPass(textureResourceLocation, addCutoutLayer(textureResourceLocation));
    }

    public static RenderType markTranslucentChunkBuffer(ResourceLocation textureResourceLocation) {
        return addSodiumTranslucentPass(textureResourceLocation, addTranslucentLayer(textureResourceLocation));
    }


    public static <E extends Entity> void markMultiCutoutChunkBuffer(E entity, ResourceLocation... cutoutTextureResourceLocations) {
        markMultiCutoutChunkBuffer(getEntityTextureResourceLocation(entity), cutoutTextureResourceLocations);
    }

    public static <E extends Entity> void markMultiCutoutChunkBuffer(EntityType<? extends E> entityType, ResourceLocation... cutoutTextureResourceLocations) {
        markMultiCutoutChunkBuffer(getEntityTextureResourceLocation(entityType), cutoutTextureResourceLocations);
    }

    public static <E extends Entity> void markMultiTranslucentChunkBuffer(E entity, ResourceLocation... translucentTextureResourceLocations) {
        markMultiTranslucentChunkBuffer(getEntityTextureResourceLocation(entity), translucentTextureResourceLocations);
    }

    public static <E extends Entity> void markMultiTranslucentChunkBuffer(EntityType<? extends E> entityType, ResourceLocation... translucentTextureResourceLocations) {
        markMultiTranslucentChunkBuffer(getEntityTextureResourceLocation(entityType), translucentTextureResourceLocations);
    }

    public static void markMultiCutoutChunkBuffer(ResourceLocation resourceLocation, ResourceLocation... cutoutTextureResourceLocations) {
        markMultiChunkBuffer(resourceLocation, Stream.of(cutoutTextureResourceLocations).map(EntryStreams.create(DynamicChunkBuffers::markCutoutChunkBuffer)).map(EntryStreams.mapEntryKey(RenderType::entityCutoutNoCull)).collect(EntryStreams.collectSequenced()));
    }

    public static void markMultiTranslucentChunkBuffer(ResourceLocation resourceLocation, ResourceLocation... translucentTextureResourceLocations) {
        markMultiChunkBuffer(resourceLocation, Stream.of(translucentTextureResourceLocations).map(EntryStreams.create(DynamicChunkBuffers::markTranslucentChunkBuffer)).map(EntryStreams.mapEntryKey(RenderType::entityCutoutNoCull)).collect(EntryStreams.collectSequenced()));
    }

    public static <E extends Entity> void markEntityChunkBuffer(E entity) {
        markMultiChunkBuffer(getEntityTextureResourceLocation(entity), Util.make(new LinkedHashMap<>(), map -> collectMultiMixedRenderTypes(entity).stream().map(EntryStreams.create(DynamicChunkBuffers::getRenderTypeTexture)).map(EntryStreams.swap()).forEach(EntryStreams.peekEntryValue((resourceLocation, renderType) -> map.computeIfAbsent(renderType, renderType1 -> renderType1.name.equals("entity_cutout_no_cull") ? markCutoutChunkBuffer(resourceLocation) : markTranslucentChunkBuffer(resourceLocation))))));
    }

    public static void markMultiChunkBuffer(ResourceLocation resourceLocation, SequencedMap<RenderType, RenderType> map) {
        DYNAMIC_MULTI_LAYERS.putIfAbsent(resourceLocation, renderType -> map.getOrDefault(unwrapIrisRenderType(renderType), map.firstEntry().getValue()));
    }

    public static <E extends Entity> List<RenderType> collectMultiMixedRenderTypes(E entity) {
        return Util.make(new CopyOnWriteArrayList<>(), list -> Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0, 0, 0, 0, 0, new PoseStack(), renderType -> Util.make(new DoNothingVertexConsumer(), ignored -> list.addIfAbsent(unwrapIrisRenderType(renderType))), LightTexture.FULL_BRIGHT));
    }

    public static RenderType unwrapIrisRenderType(RenderType renderType) {
        return IrisCompat.unwrapIrisRenderType(renderType);
    }

    private static RenderType addSodiumCutoutPass(ResourceLocation resourceLocation, RenderType cutoutRenderType) {
        return SodiumCompat.addSodiumCutoutPass(resourceLocation, cutoutRenderType);
    }

    private static RenderType addSodiumTranslucentPass(ResourceLocation resourceLocation, RenderType translucentRenderType) {
        return SodiumCompat.addSodiumTranslucentPass(resourceLocation, translucentRenderType);
    }

    private static RenderType addCutoutLayer(ResourceLocation resourceLocation) {
        return DYNAMIC_CUTOUT_LAYERS.computeIfAbsent(resourceLocation, DynamicChunkBuffers::createCutoutChunkRenderType);
    }

    private static RenderType addTranslucentLayer(ResourceLocation resourceLocation) {
        return DYNAMIC_TRANSLUCENT_LAYERS.computeIfAbsent(resourceLocation, DynamicChunkBuffers::createTranslucentChunkRenderType);
    }

    public static RenderType createCutoutChunkRenderType(ResourceLocation textureResourceLocation) {
        return CUTOUT.apply(textureResourceLocation);
    }

    public static RenderType createTranslucentChunkRenderType(ResourceLocation textureResourceLocation) {
        return TRANSLUCENT.apply(textureResourceLocation);
    }

    public static ResourceLocation getRenderTypeTexture(RenderType renderType) {
        return ((RenderStateShard.TextureStateShard) ((RenderType.CompositeRenderType) renderType).state.textureState).texture.orElseThrow();
    }

    public static <T extends Entity> ResourceLocation getEntityTextureResourceLocation(T entity) {
        return Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity).getTextureLocation(entity);
    }

    @SuppressWarnings("DataFlowIssue")
    public static <T extends Entity> ResourceLocation getEntityTextureResourceLocation(EntityType<T> entityType) {
        return Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType).getTextureLocation(null);
    }
}
