package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoReplacedEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GeckoEntityChairRenderer extends GeoReplacedEntityRenderer<EntityChair, EntityChairRenderState, GeckoRenderData> {
    public GeckoEntityChairRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public EntityChairRenderState createRenderState() {
        return new EntityChairRenderState();
    }
}
