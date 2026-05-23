package com.github.tartaricacid.simplebedrockmodel.client.bedrock;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;

/**
 * Bedrock 模型提供者接口。
 * 提供获取模型、渲染包围盒以及模型部件映射表的方法。
 */
public interface BedrockModelProvider {
    /**
     * 获取模型的渲染包围盒。
     *
     * @return 表示渲染包围盒的 AABB。
     */
    AABB getRenderBoundingBox();

    /**
     * 获取模型部件的映射表。
     *
     * @return 以字符串为键、BedrockPart 为值的 HashMap。
     */
    HashMap<String, BedrockPart> getModelMap();
}