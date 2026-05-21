package com.github.tartaricacid.touhoulittlemaid.compat.aquaculture.client;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.MaidFishingHookRenderState;
import net.minecraft.resources.Identifier;

public class AquacultureFishingHookRenderState extends MaidFishingHookRenderState {
    public boolean hasBobber;
    public boolean hasHook;
    public float bobberColorR = 1.0F;
    public float bobberColorG = 1.0F;
    public float bobberColorB = 1.0F;
    public Identifier hookTexture;
}

