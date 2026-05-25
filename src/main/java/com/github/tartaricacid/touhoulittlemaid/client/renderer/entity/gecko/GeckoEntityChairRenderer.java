package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoReplacedEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jspecify.annotations.Nullable;

public class GeckoEntityChairRenderer extends GeoReplacedEntityRenderer<EntityChair, EntityChairRenderState, GeckoRenderData> {
    public GeckoEntityChairRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public EntityChairRenderState createRenderState() {
        return new EntityChairRenderState();
    }

    @Override
    public @Nullable GeckoRenderData getGeckoRenderData(EntityChairRenderState state) {
        if (state.geckoUpdateTask != null) {
            return state.geckoUpdateTask.getResult();
        }
        return null;
    }

    @Override
    protected void scale(EntityChairRenderState state, PoseStack poseStack) {
        var scale = state.chairInfo.getRenderEntityScale();
        poseStack.scale(scale, scale, scale);
    }
}
