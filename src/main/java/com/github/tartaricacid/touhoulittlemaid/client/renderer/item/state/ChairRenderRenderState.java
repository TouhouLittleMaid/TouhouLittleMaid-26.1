package com.github.tartaricacid.touhoulittlemaid.client.renderer.item.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.Nullable;

/**
 * 椅子物品渲染状态，仿照 {@code EntityChairRenderState} 设计
 */
public class ChairRenderRenderState {
    public String modelId;
    public float renderItemScale;
    public @Nullable EntityRenderState entityRenderState;
}
