package com.github.tartaricacid.touhoulittlemaid.client.animation.special;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.api.animation.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.util.Mth;

import java.util.HashMap;

public class SwimAnimation implements IAnimation<EntityMaidRenderState> {
    @Override
    public void setupAnimation(EntityMaidRenderState state, HashMap<String, BedrockPart> models) {
        boolean isSwimming = state.isSwimming;
        float xRot = 90 + (isSwimming ? state.xRot : 0);
        float xRotLerp = Mth.lerp(state.swimAmount, 0, -xRot);

        BedrockPart root = IAnimation.root(models);
        root.xRot = -Mth.DEG_TO_RAD * xRotLerp;

        if (state.isVisuallySwimming) {
            root.offsetX = 0;
            root.offsetY = 1.25f;
            root.offsetZ = -0.5F;
        }
    }
}
