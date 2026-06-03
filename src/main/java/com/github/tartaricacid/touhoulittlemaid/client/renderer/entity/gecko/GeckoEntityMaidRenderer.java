package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko;

import com.github.tartaricacid.touhoulittlemaid.api.event.client.AddMaidLayerEvent;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer.*;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoReplacedEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class GeckoEntityMaidRenderer extends GeoReplacedEntityRenderer<EntityMaid, EntityMaidRenderState, GeckoMaidRenderData> {
    public GeckoEntityMaidRenderer(EntityRendererProvider.Context context) {
        super(context);

        this.addLayer(new GeckoLayerMaidHeld());
        this.addLayer(new GeckoLayerMaidBipedHead(context));
        this.addLayer(new GeckoLayerMaidBackpack());
        this.addLayer(new GeckoLayerMaidBackItem());
        this.addLayer(new GeckoLayerMaidBanner(context));

        NeoForge.EVENT_BUS.post(new AddMaidLayerEvent.Gecko(context, this));
    }

    @Override
    public @NonNull EntityMaidRenderState createRenderState() {
        return new EntityMaidRenderState();
    }

    @Override
    public @Nullable GeckoMaidRenderData getGeckoRenderData(EntityMaidRenderState state) {
        if (state.geckoUpdateTask != null) {
            return state.geckoUpdateTask.getResult();
        }
        return null;
    }

    @Override
    protected void setupRotations(@NonNull EntityMaidRenderState state, @NonNull PoseStack poseStack, float bodyRot, float entityScale) {
        var data = getGeckoRenderData(state);
        if (data != null) {
            var ctx = data.ctx;
            if ((ctx.level() || ctx.irisShadow()) && !Float.isNaN(data.climbRotation)) {
                bodyRot = data.climbRotation;
            }
        }
        super.setupRotations(state, poseStack, bodyRot, entityScale);
    }

    @Override
    protected void scale(EntityMaidRenderState state, PoseStack poseStack) {
        var scale = state.modelInfo.getRenderEntityScale();
        poseStack.scale(scale, scale, scale);
    }
}
