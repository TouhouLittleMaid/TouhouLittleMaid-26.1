package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class EntityYukkuriSlimeRenderState extends LivingEntityRenderState {
    /**
     * 史莱姆当前的 squish 值，用于 scale 计算
     */
    public float squish;
    public float oSquish;
    public int size;
}
