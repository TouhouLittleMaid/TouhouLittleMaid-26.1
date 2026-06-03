package com.github.tartaricacid.touhoulittlemaid.client.animation.special;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.api.animation.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.common.Tags;

import java.util.HashMap;

public class TridentAnimation implements IAnimation<EntityMaidRenderState> {
    @Override
    public void setupAnimation(EntityMaidRenderState state, HashMap<String, BedrockPart> models) {
        if (!state.sleeping && state.isUsingItem
            && state.useItemHand == InteractionHand.MAIN_HAND
            && state.getMainHandItemStack().is(Tags.Items.TOOLS_TRIDENT)
        ) {
            BedrockPart armRight = models.get("armRight");
            if (armRight != null) {
                float rot = state.ticksUsingItem / 10f;
                armRight.xRot = (armRight.getInitRotX() - 80) - Math.min(rot, Mth.PI / 2) - 10;
                armRight.zRot = -Math.min(rot, Mth.PI / 6);
            }
        }
    }
}
