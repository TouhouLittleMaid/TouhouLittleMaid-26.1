package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;

import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation.INNER_ANIMATION;

public final class MaidExtraAnimation {
    private static final float DEG_TO_RAD = 0.017453292f;

    public static void init() {
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/arm/extra.js"), getArmExtra());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/arm/vertical.js"), getArmVertical());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/extra.js"), getHeadExtra());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/hurt.js"), getHeadHurt());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/reverse_blink.js"), getHeadReverseBlink());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/leg/extra.js"), getLegExtra());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/leg/vertical.js"), getLegVertical());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/health/less_show.js"), getHealthLessShow());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/health/more_show.js"), getHealthMoreShow());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/health/rotation.js"), getHealthRotation());
    }

    public static IAnimation<EntityMaidRenderState> getArmExtra() {
        return (state, models) -> {
            BedrockPart armLeft = models.get("armLeftExtraA");
            BedrockPart armRight = models.get("armRightExtraA");

            double f1 = 1.0 - Math.pow(1.0 - state.attackAnim, 4);
            double f2 = Math.sin(f1 * Math.PI);
            double f3 = Math.sin(state.attackAnim * Math.PI) * -0.7 * 0.75;
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = state.walkAnimationSpeed;
            float ageInTicks = state.ageInTicks;

            if (armLeft != null) {
                armLeft.xRot = (float) (-Math.cos(limbSwing * 0.67) * 0.7 * limbSwingAmount);
                armLeft.yRot = 0;
                armLeft.zRot = (float) (Math.cos(ageInTicks * 0.05) * 0.05 - 0.4);
                // 手部攻击动画
                if (state.attackAnim > 0.0 && isSwingLeftHand(state)) {
                    armLeft.xRot = (float) (armLeft.xRot - (f2 * 1.2 + f3));
                    armLeft.zRot = (float) (armLeft.zRot + Math.sin(state.attackAnim * Math.PI) * -0.4);
                }
                // 使用动画
                if (state.usingItem && state.usedItemHand == InteractionHand.OFF_HAND) {
                    armLeft.xRot = armLeft.getInitRotX() - (float) Math.PI * 80 / 180.0f;
                    armLeft.yRot = armLeft.getInitRotY() + (float) Math.PI * 25 / 180.0f;
                }
            }

            if (armRight != null) {
                armRight.xRot = (float) (Math.cos(limbSwing * 0.67) * 0.7 * limbSwingAmount);
                armRight.yRot = 0;
                armRight.zRot = (float) (-Math.cos(ageInTicks * 0.05) * 0.05 + 0.4);
                // 手部攻击动画
                if (state.attackAnim > 0.0 && !isSwingLeftHand(state)) {
                    armRight.xRot = (float) (armRight.xRot - (f2 * 1.2 + f3));
                    armRight.zRot = (float) (armRight.zRot + Math.sin(state.attackAnim * Math.PI) * -0.4);
                }
                // 使用动画
                if (state.usingItem && state.usedItemHand == InteractionHand.MAIN_HAND) {
                    armRight.xRot = armRight.getInitRotX() - (float) Math.PI * 80 / 180.0f;
                    armRight.yRot = armRight.getInitRotY() - (float) Math.PI * 20 / 180.0f;
                }
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getArmVertical() {
        return (state, models) -> {
            BedrockPart armLeftVertical = models.get("armLeftVertical");
            BedrockPart armLeft = models.get("armLeft");
            if (armLeftVertical != null && armLeft != null) {
                armLeftVertical.xRot = -armLeft.xRot;
                armLeftVertical.zRot = -armLeft.zRot;
            }

            BedrockPart armRightVertical = models.get("armRightVertical");
            BedrockPart armRight = models.get("armRight");
            if (armRightVertical != null && armRight != null) {
                armRightVertical.xRot = -armRight.xRot;
                armRightVertical.zRot = -armRight.zRot;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getHeadExtra() {
        return (state, models) -> {
            float headPitch = state.xRot * DEG_TO_RAD;
            float netHeadYaw = state.yRot * DEG_TO_RAD;
            setHeadRotation(models.get("headExtraA"), headPitch, netHeadYaw);
            setHeadRotation(models.get("headExtraB"), headPitch, netHeadYaw);
            setHeadRotation(models.get("headExtraC"), headPitch, netHeadYaw);
        };
    }

    public static IAnimation<EntityMaidRenderState> getHeadHurt() {
        return (state, models) -> {
            BedrockPart hurtBlink = models.get("hurtBlink");
            if (hurtBlink != null) {
                hurtBlink.visible = state.hurt;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getHeadReverseBlink() {
        return (state, models) -> {
            float remainder = (state.ageInTicks + Math.abs(state.uuidLeastSignificantBits) % 10) % 60;
            boolean visible = !(55 < remainder && remainder < 60);

            BedrockPart reverseBlink = models.get("_bink");
            if (reverseBlink != null) {
                reverseBlink.visible = visible;
            }

            BedrockPart reverseBlinkCorrect = models.get("_blink");
            if (reverseBlinkCorrect != null) {
                reverseBlinkCorrect.visible = visible;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getLegExtra() {
        return (state, models) -> {
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = state.walkAnimationSpeed;

            BedrockPart legLeftExtraA = models.get("legLeftExtraA");
            if (legLeftExtraA != null) {
                legLeftExtraA.xRot = (float) (Math.cos(limbSwing * 0.67) * 0.3 * limbSwingAmount);
                legLeftExtraA.yRot = 0;
                legLeftExtraA.zRot = 0;
            }

            BedrockPart legRightExtraA = models.get("legRightExtraA");
            if (legRightExtraA != null) {
                legRightExtraA.xRot = (float) (-Math.cos(limbSwing * 0.67) * 0.3 * limbSwingAmount);
                legRightExtraA.yRot = 0;
                legRightExtraA.zRot = 0;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getLegVertical() {
        return (state, models) -> {
            BedrockPart legLeftVertical = models.get("legLeftVertical");
            BedrockPart legLeft = models.get("legLeft");
            if (legLeftVertical != null && legLeft != null) {
                legLeftVertical.xRot = -legLeft.xRot;
                legLeftVertical.zRot = -legLeft.zRot;
            }

            BedrockPart legRightVertical = models.get("legRightVertical");
            BedrockPart legRight = models.get("legRight");
            if (legRightVertical != null && legRight != null) {
                legRightVertical.xRot = -legRight.xRot;
                legRightVertical.zRot = -legRight.zRot;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getHealthLessShow() {
        return (state, models) -> {
            if (state.maxHealth <= 0) {
                return;
            }

            double ratio = state.health / state.maxHealth;
            setVisible(models.get("healthLessQuarterShow"), ratio <= 0.25);
            setVisible(models.get("healthLessHalfShow"), ratio <= 0.5);
            setVisible(models.get("healthLessThreeQuartersShow"), ratio <= 0.75);
        };
    }

    public static IAnimation<EntityMaidRenderState> getHealthMoreShow() {
        return (state, models) -> {
            if (state.maxHealth <= 0) {
                return;
            }

            double ratio = state.health / state.maxHealth;
            setVisible(models.get("healthMoreQuarterShow"), ratio > 0.25);
            setVisible(models.get("healthMoreHalfShow"), ratio > 0.5);
            setVisible(models.get("healthMoreThreeQuartersShow"), ratio > 0.75);
        };
    }

    public static IAnimation<EntityMaidRenderState> getHealthRotation() {
        return (state, models) -> {
            if (state.maxHealth <= 0) {
                return;
            }

            BedrockPart healthRotationX90 = models.get("healthRotationX90");
            if (healthRotationX90 != null) {
                double deg = (Math.PI / 4) - (Math.PI / 2) * (state.health / state.maxHealth);
                healthRotationX90.xRot = (float) deg;
            }
        };
    }

    private static void setHeadRotation(BedrockPart head, float headPitch, float headYaw) {
        if (head != null) {
            head.xRot = headPitch;
            head.yRot = headYaw;
        }
    }

    private static void setVisible(BedrockPart part, boolean visible) {
        if (part != null) {
            part.visible = visible;
        }
    }

    private static boolean isSwingLeftHand(EntityMaidRenderState state) {
        return state.swingingArm == InteractionHand.OFF_HAND;
    }
}
