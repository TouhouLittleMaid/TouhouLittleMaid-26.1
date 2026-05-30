package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation.INNER_ANIMATION;

public final class PlayerMaidAnimation {
    public static void init() {
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/player/arm/default.js"), getPlayerArmDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/player/sit/default.js"), getPlayerSitDefault());
    }

    public static IAnimation<EntityMaidRenderState> getPlayerArmDefault() {
        return (state, models) -> {
            BedrockPart armLeft = models.get("armLeft");
            BedrockPart armRight = models.get("armRight");

            double f1 = 1.0 - Math.pow(1.0 - state.attackTime, 4);
            double f2 = Math.sin(f1 * Math.PI);
            double f3 = Math.sin(state.attackTime * Math.PI) * -0.7 * 0.75;
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = state.walkAnimationSpeed;
            float ageInTicks = state.ageInTicks;

            if (armLeft != null) {
                armLeft.xRot = (float) (-Math.cos(limbSwing * 0.67) * 0.7 * limbSwingAmount);
                armLeft.yRot = 0;
                armLeft.zRot = (float) (Math.cos(ageInTicks * 0.05) * 0.025 - 0.05);
                if (state.attackTime > 0.0 && isSwingLeftHand(state)) {
                    armLeft.xRot = (float) (armLeft.xRot - (f2 * 1.2 + f3));
                    armLeft.zRot = (float) (armLeft.zRot + Math.sin(state.attackTime * Math.PI) * -0.4);
                }
                if (state.isUsingItem && state.useItemHand == InteractionHand.OFF_HAND) {
                    armLeft.xRot = armLeft.getInitRotX() - (float) Math.PI * 80 / 180.0f;
                    armLeft.yRot = armLeft.getInitRotY() + (float) Math.PI * 25 / 180.0f;
                }
            }

            if (armRight != null) {
                armRight.xRot = (float) (Math.cos(limbSwing * 0.67) * 0.7 * limbSwingAmount);
                armRight.yRot = 0;
                armRight.zRot = (float) (-Math.cos(ageInTicks * 0.05) * 0.025 + 0.05);
                if (state.attackTime > 0.0 && !isSwingLeftHand(state)) {
                    armRight.xRot = (float) (armRight.xRot - (f2 * 1.2 + f3));
                    armRight.zRot = (float) (armRight.zRot + Math.sin(state.attackTime * Math.PI) * -0.4);
                }
                if (state.isUsingItem && state.useItemHand == InteractionHand.MAIN_HAND) {
                    armRight.xRot = armRight.getInitRotX() - (float) Math.PI * 80 / 180.0f;
                    armRight.yRot = armRight.getInitRotY() - (float) Math.PI * 20 / 180.0f;
                }
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getPlayerSitDefault() {
        return (state, models) -> {
            BedrockPart head = models.get("head");
            BedrockPart legLeft = models.get("legLeft");
            BedrockPart legRight = models.get("legRight");
            BedrockPart armLeft = models.get("armLeft");
            BedrockPart armRight = models.get("armRight");
            BedrockPart root = IAnimation.root(models);

            if (head != null) {
                head.offsetY = 0;
            }

            if (state.isPassenger) {
                playerRidingPosture(legLeft, legRight);
                root.offsetY = 0.3f;
            } else if (state.sitting) {
                playerSittingPosture(armLeft, armRight, legLeft, legRight);
                root.offsetY = 0.3f;
            } else {
                root.offsetY = 0f;
            }
        };
    }

    private static void playerRidingPosture(BedrockPart legLeft, BedrockPart legRight) {
        if (legLeft != null) {
            legLeft.xRot = -1.4f;
            legLeft.yRot = -0.4f;
        }

        if (legRight != null) {
            legRight.xRot = -1.4f;
            legRight.yRot = 0.4f;
        }
    }

    private static void playerSittingPosture(BedrockPart armLeft, BedrockPart armRight,
                                             BedrockPart legLeft, BedrockPart legRight) {
        if (armLeft != null) {
            armLeft.xRot = -0.798f;
            armLeft.zRot = 0.274f;
        }

        if (armRight != null) {
            armRight.xRot = -0.798f;
            armRight.zRot = -0.274f;
        }

        ridingPosture(legLeft, legRight);
    }

    private static void ridingPosture(BedrockPart legLeft, BedrockPart legRight) {
        if (legLeft != null) {
            legLeft.xRot = -1.134f;
            legLeft.zRot = -0.262f;
        }
        if (legRight != null) {
            legRight.xRot = -1.134f;
            legRight.zRot = 0.262f;
        }
    }

    private static boolean isSwingLeftHand(EntityMaidRenderState state) {
        return state.attackArm == HumanoidArm.LEFT;
    }
}
