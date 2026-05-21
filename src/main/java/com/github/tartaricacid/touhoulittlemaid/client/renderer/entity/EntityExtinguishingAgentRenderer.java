package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityExtinguishingAgent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/**
 * EntityExtinguishingAgent 的渲染器，该实体本身不可见，不渲染任何内容。
 */
public class EntityExtinguishingAgentRenderer extends EntityRenderer<EntityExtinguishingAgent, EntityRenderState> {
    public EntityExtinguishingAgentRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        // 该实体不可见，不渲染任何内容
    }
}
