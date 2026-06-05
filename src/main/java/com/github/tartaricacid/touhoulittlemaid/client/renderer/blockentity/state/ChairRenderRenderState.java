package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * 椅子物品渲染状态，仿照 {@code EntityChairRenderState} 设计
 */
public class ChairRenderRenderState {
    public String modelId;
    public float renderItemScale;

    @Nullable
    public EntityChairModel bedrockModel;
    @Nullable
    public ChairModelInfo chairInfo;
    @Nullable
    public Identifier texture;
}
