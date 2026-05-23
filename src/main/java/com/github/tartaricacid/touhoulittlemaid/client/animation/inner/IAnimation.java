package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.HashMap;

@FunctionalInterface
public interface IAnimation<T extends EntityRenderState> {
    void setupAnimation(T state, HashMap<String, BedrockPart> models);
}
