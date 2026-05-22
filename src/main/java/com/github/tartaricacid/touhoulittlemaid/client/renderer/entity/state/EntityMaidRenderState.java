package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Mob;

/**
 * 女仆渲染状态。
 * TODO: 逐步将实体属性拷贝到 state 字段中，最终移除 entity 引用
 */
public class EntityMaidRenderState extends LivingEntityRenderState {
    /**
     * 临时保留实体引用，用于尚未迁移的渲染逻辑和 Layer
     */
    public Mob entity;
}
