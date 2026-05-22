package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.layer.*;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;

public class GeckoEntityMaidRenderer<T extends Mob> extends GeoReplacedEntityRenderer<T, EntityMaidRenderState, GeckoMaidRenderData> {
    public GeckoEntityMaidRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        addLayer(new GeckoLayerMaidHeld());
        addLayer(new GeckoLayerMaidBipedHead(Minecraft.getInstance().getBlockEntityRenderDispatcher()));
        addLayer(new GeckoLayerMaidBackpack());
        addLayer(new GeckoLayerMaidBackItem());
        addLayer(new GeckoLayerMaidBanner());
        addAdditionGeckoEntityMaidRenderer(renderManager);
    }

    @Override
    public EntityMaidRenderState createRenderState() {
        return new EntityMaidRenderState();
    }

    private void addAdditionGeckoEntityMaidRenderer(EntityRendererProvider.Context renderManager) {
        for (ILittleMaid littleMaid : TouhouLittleMaid.EXTENSIONS) {
            littleMaid.addAdditionGeckoMaidLayer(this, renderManager);
        }
    }

    @Override
    protected void setupRotations(EntityMaidRenderState state, GeckoMaidRenderData data, RenderContext ctx, PoseStack poseStack, float bodyRot, float entityScale) {
        if ((ctx.level() || ctx.irisShadow()) && !Float.isNaN(data.climbRotation)) {
            bodyRot = data.climbRotation;
        }
        super.setupRotations(state, data, ctx, poseStack, bodyRot, entityScale);
    }
}
