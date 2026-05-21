package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * 椅子渲染状态。携带 modelId 以便在 submit 中动态加载模型。
 * TODO: 移除 entity 引用（等 Gecko 渲染器迁移后再清理）
 */
public class EntityChairRenderState extends LivingEntityRenderState {
    public String modelId;
    /** 临时保留实体引用，用于尚未迁移的 Gecko 渲染器 */
    public EntityChair chair;
}
