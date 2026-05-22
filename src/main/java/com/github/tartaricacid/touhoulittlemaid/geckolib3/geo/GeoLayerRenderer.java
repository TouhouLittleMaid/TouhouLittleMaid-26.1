package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public interface GeoLayerRenderer<S extends EntityRenderState, D extends GeckoRenderData> {
    void submit(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, AnimationEvent<?> event, S state, D data, CameraRenderState camera);
}