package com.github.tartaricacid.touhoulittlemaid.client.model.bedrock;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockVersion;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.google.common.collect.Lists;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.List;

public class EntityChairModel extends SimpleBedrockEntityModel<EntityChairRenderState> {
    private List<IAnimation<EntityChairRenderState>> animations = Lists.newArrayList();

    public EntityChairModel() {
        super();
    }

    public EntityChairModel(InputStream stream) {
        super(stream);
    }

    public EntityChairModel(BedrockModelPOJO pojo, BedrockVersion version) {
        super(pojo, version);
    }

    @Override
    @NullMarked
    public void setupAnim(EntityChairRenderState state) {
        if (animations == null || animations.isEmpty()) {
            return;
        }
        this.animations.forEach(animation ->
                animation.setupAnimation(state, modelMap)
        );
    }

    public void setAnimations(@Nullable List<IAnimation<EntityChairRenderState>> animations) {
        this.animations = animations;
    }
}
