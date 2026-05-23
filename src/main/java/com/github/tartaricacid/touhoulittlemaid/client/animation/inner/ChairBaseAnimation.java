package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import net.minecraft.resources.Identifier;

import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation.INNER_ANIMATION;

public final class ChairBaseAnimation {
    private static final float DEG_TO_RAD = 0.017453292F;

    public static void init() {
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/chair/passenger/hidden.js"), getPassengerHidden());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/chair/passenger/rotation.js"), getPassengerRotation());
    }

    public static IAnimation<EntityChairRenderState> getPassengerHidden() {
        return (state, models) -> {
            setVisible(models.get("passengerHidden"), !state.hasPassenger);
            setVisible(models.get("passengerShow"), state.hasPassenger);
        };
    }

    public static IAnimation<EntityChairRenderState> getPassengerRotation() {
        return (state, models) -> {
            BedrockPart passengerRotationYaw = models.get("passengerRotationYaw");
            BedrockPart passengerRotationPitch = models.get("passengerRotationPitch");
            if (passengerRotationYaw != null) {
                passengerRotationYaw.yRot = (state.passengerYRot - state.yRot) * DEG_TO_RAD;
            }
            if (passengerRotationPitch != null) {
                passengerRotationPitch.xRot = state.passengerXRot * DEG_TO_RAD;
            }
        };
    }

    private static void setVisible(BedrockPart part, boolean visible) {
        if (part != null) {
            part.visible = visible;
        }
    }
}
