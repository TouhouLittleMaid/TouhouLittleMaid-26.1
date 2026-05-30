package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation.INNER_ANIMATION;

public final class SpecialAnimation {
    public static void init() {
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/special/hecatia_dimension.js"), getSpecialHecatia());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/special/wakasagihime_sit.js"), getSpecialWakasagihime());
    }

    public static IAnimation<EntityMaidRenderState> getSpecialHecatia() {
        return (state, models) -> {
            var dim = state.dimension;
            if (dim == null) {
                return;
            }

            BedrockPart earthHair = models.get("earthHair");
            BedrockPart logoEarth = models.get("logoEarth");
            BedrockPart earthTop = models.get("earthTop");
            BedrockPart earthSideLeft = models.get("earthSideLeft");
            BedrockPart earthSideRight = models.get("earthSideRight");

            BedrockPart moonHair = models.get("moonHair");
            BedrockPart logoMoon = models.get("logoMoon");
            BedrockPart moonTop = models.get("moonTop");
            BedrockPart moonSideLeft = models.get("moonSideLeft");
            BedrockPart moonSideRight = models.get("moonSideRight");

            BedrockPart otherHair = models.get("otherHair");
            BedrockPart logoOther = models.get("logoOther");
            BedrockPart otherTop = models.get("otherTop");
            BedrockPart otherSideLeft = models.get("otherSideLeft");
            BedrockPart otherSideRight = models.get("otherSideRight");

            if (dim.equals(Level.OVERWORLD)) {
                setVisible(earthHair, true);
                setVisible(logoEarth, true);
                setVisible(earthTop, true);
                setVisible(earthSideLeft, false);
                setVisible(earthSideRight, false);

                setVisible(moonHair, false);
                setVisible(logoMoon, false);
                setVisible(moonTop, false);
                setVisible(moonSideLeft, false);
                setVisible(moonSideRight, true);

                setVisible(otherHair, false);
                setVisible(logoOther, false);
                setVisible(otherTop, false);
                setVisible(otherSideLeft, true);
                setVisible(otherSideRight, false);
            } else if (dim.equals(Level.NETHER)) {
                setVisible(earthHair, false);
                setVisible(logoEarth, false);
                setVisible(earthTop, false);
                setVisible(earthSideLeft, true);
                setVisible(earthSideRight, false);

                setVisible(moonHair, true);
                setVisible(logoMoon, true);
                setVisible(moonTop, true);
                setVisible(moonSideLeft, false);
                setVisible(moonSideRight, false);

                setVisible(otherHair, false);
                setVisible(logoOther, false);
                setVisible(otherTop, false);
                setVisible(otherSideLeft, false);
                setVisible(otherSideRight, true);
            } else {
                setVisible(earthHair, false);
                setVisible(logoEarth, false);
                setVisible(earthTop, false);
                setVisible(earthSideLeft, true);
                setVisible(earthSideRight, false);

                setVisible(moonHair, false);
                setVisible(logoMoon, false);
                setVisible(moonTop, false);
                setVisible(moonSideLeft, false);
                setVisible(moonSideRight, true);

                setVisible(otherHair, true);
                setVisible(logoOther, true);
                setVisible(otherTop, true);
                setVisible(otherSideLeft, false);
                setVisible(otherSideRight, false);
            }

            if (!state.headEquipment.isEmpty()) {
                setVisible(earthTop, false);
                setVisible(moonTop, false);
                setVisible(otherTop, false);
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getSpecialWakasagihime() {
        return (state, models) -> {
            if (!state.sitting) {
                return;
            }

            BedrockPart armLeft = models.get("armLeft");
            if (armLeft != null) {
                armLeft.xRot = -0.798f;
                armLeft.zRot = 0.274f;
            }

            BedrockPart armRight = models.get("armRight");
            if (armRight != null) {
                armRight.xRot = -0.798f;
                armRight.zRot = -0.274f;
            }
        };
    }

    private static void setVisible(BedrockPart part, boolean visible) {
        if (part != null) {
            part.visible = visible;
        }
    }
}
