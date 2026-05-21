package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;

public class MaidFishingHookRenderState extends EntityRenderState {
    public Vec3 lineOriginOffset = Vec3.ZERO;
    public float lineColorR;
    public float lineColorG;
    public float lineColorB;
}
