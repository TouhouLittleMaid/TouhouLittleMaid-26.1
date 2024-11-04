package com.github.tartaricacid.touhoulittlemaid.client.renderer.sections.events;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = TouhouLittleMaid.MOD_ID, value = Dist.CLIENT)
public class SectionGeometryRenderTypeEvents {
    private static ShaderInstance itemEntityTranslucentCullChunkShader;
    private static ShaderInstance entityCutoutNoCullChunkShader;
    private static ShaderInstance entityTranslucentChunkShader;

    private static final Supplier<RenderType> ITEM_ENTITY_TRANSLUCENT_CULL_CHUNK = Suppliers.memoize(() -> RenderType.create("touhou_little_maid:item_entity_translucent_cull_chunk", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> itemEntityTranslucentCullChunkShader))
            .setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .createCompositeState(true)
    ));

    public static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT_NO_CULL_CHUNK = Util.memoize((textureResourceLocation) -> RenderType.create("touhou_little_maid:entity_cutout_no_cull_chunk", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> entityCutoutNoCullChunkShader))
            .setTextureState(new RenderStateShard.TextureStateShard(textureResourceLocation, false, false))
            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY)
            .createCompositeState(true)
    ));

    public static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_CHUNK = Util.memoize((textureResourceLocation) -> RenderType.create("touhou_little_maid:entity_translucent_chunk", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> entityTranslucentChunkShader))
            .setTextureState(new RenderStateShard.TextureStateShard(textureResourceLocation, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY)
            .createCompositeState(true))
    );

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "rendertype_item_entity_translucent_cull_chunk"), DefaultVertexFormat.NEW_ENTITY), shader -> itemEntityTranslucentCullChunkShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "rendertype_entity_cutout_no_cull_chunk"), DefaultVertexFormat.NEW_ENTITY), shader -> entityCutoutNoCullChunkShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "rendertype_entity_translucent_chunk"), DefaultVertexFormat.NEW_ENTITY), shader -> entityTranslucentChunkShader = shader);
    }

    public static RenderType getItemEntityTranslucentCull() {
        return ITEM_ENTITY_TRANSLUCENT_CULL_CHUNK.get();
    }

    public static RenderType getEntityCutoutNoCull(ResourceLocation textureResourceLocation) {
        return ENTITY_CUTOUT_NO_CULL_CHUNK.apply(textureResourceLocation);
    }

    public static RenderType getEntityTranslucent(ResourceLocation textureResourceLocation) {
        return ENTITY_TRANSLUCENT_CHUNK.apply(textureResourceLocation);
    }
}
