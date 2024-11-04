package com.github.tartaricacid.touhoulittlemaid.client.renderer.sections.events;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.sections.SectionGeometryBlockEntityRenderDispatcher;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.sections.dynamic.DynamicChunkBuffers;
import com.github.tartaricacid.touhoulittlemaid.compat.iris.IrisCompat;
import com.github.tartaricacid.touhoulittlemaid.compat.sodium.SodiumCompat;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;

/**
 * @author Argon4W
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = TouhouLittleMaid.MOD_ID, value = Dist.CLIENT)
public class SectionGeometryRendererGameEvents {
    @SubscribeEvent
    public static void onAddSectionGeometry(AddSectionGeometryEvent event) {
        event.addRenderer(new SectionGeometryBlockEntityRenderDispatcher(event.getSectionOrigin().immutable()));
    }

    @SubscribeEvent
    public static void onRenderVanillaChunkBufferItems(RenderLevelStageEvent event) {
        if (SodiumCompat.isInstalled()) {
            return;
        }

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Vector3f position = event.getCamera().getPosition().toVector3f();
        event.getLevelRenderer().renderSectionLayer(SectionGeometryRenderTypeEvents.getItemEntityTranslucentCull(), position.x, position.y, position.z, event.getModelViewMatrix(), event.getProjectionMatrix());
        event.getLevelRenderer().renderBuffers.bufferSource().endBatch(SectionGeometryRenderTypeEvents.getItemEntityTranslucentCull());
    }

    @SubscribeEvent
    public static void onRenderDynamicCutoutRenderType(RenderLevelStageEvent event) {
        if (IrisCompat.isRenderingShadow()) {
            return;
        }

        if (event.getStage() != SodiumCompat.getCutoutRenderStage()) {
           return;
        }

        Vector3f position = event.getCamera().getPosition().toVector3f();

        for (RenderType renderType : DynamicChunkBuffers.DYNAMIC_CUTOUT_LAYERS.values()) {
            event.getLevelRenderer().renderSectionLayer(renderType, position.x, position.y, position.z, event.getModelViewMatrix(), event.getProjectionMatrix());
            event.getLevelRenderer().renderBuffers.bufferSource().endBatch(renderType);
        }
    }

    @SubscribeEvent
    public static void onRenderDynamicTranslucentRenderType(RenderLevelStageEvent event) {
        if (IrisCompat.isRenderingShadow()) {
            return;
        }

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Vector3f position = event.getCamera().getPosition().toVector3f();

        for (RenderType renderType : DynamicChunkBuffers.DYNAMIC_TRANSLUCENT_LAYERS.values()) {
            event.getLevelRenderer().renderSectionLayer(renderType, position.x, position.y, position.z, event.getModelViewMatrix(), event.getProjectionMatrix());
            event.getLevelRenderer().renderBuffers.bufferSource().endBatch(renderType);
        }
    }
}
