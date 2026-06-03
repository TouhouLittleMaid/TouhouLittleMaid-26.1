package com.github.tartaricacid.touhoulittlemaid.api.animation;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.BedrockModelUtil;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.HashMap;

@FunctionalInterface
public interface IAnimation<T extends EntityRenderState> {
    /**
     * 获取模型的根节点，如果要对模型整体进行移动、旋转和缩放，就可以通过操作根节点来实现。
     */
    static BedrockPart root(HashMap<String, BedrockPart> models) {
        return models.get(BedrockModelUtil.ROOT_NAME);
    }

    /**
     * 动画设置方法，在这里对模型进行操作来实现动画效果。
     *
     * @param state  实体渲染状态，可以通过它获取一些实体的状态信息来实现条件动画。
     * @param models 模型的所有部件，key 是模型中部件的名字，value 是对应的 BedrockPart 对象，可以通过它来对模型进行操作。
     */
    void setupAnimation(T state, HashMap<String, BedrockPart> models);
}
