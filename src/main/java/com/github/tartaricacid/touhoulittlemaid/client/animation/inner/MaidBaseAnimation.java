package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.api.animation.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.EntityBaseAnimation.getBaseFloatDefault;
import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation.INNER_ANIMATION;
import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.MaidArmorAnimation.getArmorDefault;
import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.MaidArmorAnimation.getArmorReverse;
import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.MaidExtraAnimation.getArmVertical;

public final class MaidBaseAnimation {
    private static final float DEG_TO_RAD = 0.017453292f;

    public static void init() {
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/arm/default.js"), getArmDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/arm/swing.js"), getArmSwing());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/beg.js"), getHeadBeg());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/blink.js"), getHeadBlink());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/default.js"), getHeadDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/ear_shake.js"), getEarShake());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/ear_beg_shake.js"), getEarBegShake());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/hair_swing.js"), getHairSwing());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/head/hair_ponytail_swing.js"), getHairPonytailSwing());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/leg/default.js"), getLegDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/sit/default.js"), getSitDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/sit/no_leg.js"), getSitNoLeg());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/sit/skirt_hidden.js"), getSitSkirtHidden());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/sit/skirt_rotation.js"), getSitSkirtRotation());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/sit/skirt_rotation_swing.js"), getSitSkirtRotationSwing());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/status/backpack.js"), getStatusBackpack());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/tail/default.js"), getTailDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/wing/default.js"), getWingDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/sleep/default.js"), getSleepDefault());

        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid.default.js"), getMaidDefault());
    }

    public static IAnimation<EntityMaidRenderState> getHeadDefault() {
        return (state, models) -> {
            BedrockPart head = models.get("head");
            if (head != null) {
                head.xRot = state.xRot * DEG_TO_RAD;
                head.yRot = state.yRot * DEG_TO_RAD;
                if (isSleeping(state)) {
                    head.xRot = 15 * DEG_TO_RAD;
                }
            }

            BedrockPart hat = models.get("hat");
            if (hat != null) {
                hat.visible = !isSleeping(state);
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getHeadBlink() {
        return (state, models) -> {
            float remainder = (state.ageInTicks + Math.abs(state.randomNumber) % 10) % 60;
            boolean visible = state.sleeping || (55 < remainder && remainder < 60);
            setVisible(models.get("blink"), visible);
            setVisible(models.get("blink2"), visible);
        };
    }

    public static IAnimation<EntityMaidRenderState> getHeadBeg() {
        return (state, models) -> {
            BedrockPart head = models.get("head");
            BedrockPart ahoge = models.get("ahoge");
            BedrockPart begShow = models.get("begShow");

            if (state.begging) {
                if (head != null) {
                    head.zRot = 0.139f;
                }
                if (ahoge != null) {
                    ahoge.xRot = (float) (Math.cos(state.ageInTicks * 1.0) * 0.05) + ahoge.getInitRotX();
                    ahoge.zRot = (float) (Math.sin(state.ageInTicks * 1.0) * 0.05) + ahoge.getInitRotZ();
                }
                setVisible(begShow, true);
            } else {
                setVisible(begShow, false);
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getHairPonytailSwing() {
        return (state, models) -> {
            BedrockPart hairPonytailSwing = models.get("hairPonytailSwing");
            if (hairPonytailSwing != null) {
                float rotationZ = (float) Math.sin(state.ageInTicks * 0.05) * 0.06f;
                hairPonytailSwing.zRot = rotationZ + hairPonytailSwing.getInitRotZ();
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getHairSwing() {
        return (state, models) -> {
            BedrockPart hairLeftSwing = models.get("hairLeftSwing");
            BedrockPart hairRightSwing = models.get("hairRightSwing");
            float rotationZ = (float) Math.sin(state.ageInTicks * 0.05) * 0.04f;

            if (hairLeftSwing != null) {
                hairLeftSwing.zRot = hairLeftSwing.getInitRotZ() + rotationZ;
            }
            if (hairRightSwing != null) {
                hairRightSwing.zRot = hairRightSwing.getInitRotZ() - rotationZ;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getEarShake() {
        return (state, models) -> {
            BedrockPart earLeftShake = models.get("earLeftShake");
            BedrockPart earRightShake = models.get("earRightShake");
            float time = (state.ageInTicks + Math.abs(state.randomNumber) % 10) % 40;
            if (time < Math.PI * 4) {
                float rotationZ = (float) Math.abs(Math.sin(time * 0.25)) * 0.4f;
                if (earLeftShake != null) {
                    earLeftShake.zRot = earLeftShake.getInitRotZ() + rotationZ;
                }
                if (earRightShake != null) {
                    earRightShake.zRot = earRightShake.getInitRotZ() - rotationZ;
                }
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getEarBegShake() {
        return (state, models) -> {
            BedrockPart earLeftShake = models.get("earLeftBegShake");
            BedrockPart earRightShake = models.get("earRightBegShake");
            float time = (state.ageInTicks + Math.abs(state.randomNumber) % 10) % 40;
            if (state.begging && time < Math.PI * 4) {
                float rotationZ = (float) Math.abs(Math.sin(time * 0.25)) * 0.4f;
                if (earLeftShake != null) {
                    earLeftShake.zRot = earLeftShake.getInitRotZ() + rotationZ;
                }
                if (earRightShake != null) {
                    earRightShake.zRot = earRightShake.getInitRotZ() - rotationZ;
                }
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getLegDefault() {
        return (state, models) -> {
            BedrockPart legLeft = models.get("legLeft");
            BedrockPart legRight = models.get("legRight");

            boolean isFarm = "farm".equals(state.taskId) && state.swingTime > 0;
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = state.walkAnimationSpeed;

            if (legLeft != null) {
                double leftRad = Math.cos(limbSwing * 0.67) * 0.3 * limbSwingAmount;
                if (isFarm) {
                    leftRad -= 0.3927;
                }
                legLeft.xRot = (float) leftRad;
            }
            if (legRight != null) {
                double rightRad = -Math.cos(limbSwing * 0.67) * 0.3 * limbSwingAmount;
                if (isFarm) {
                    rightRad -= 0.3927;
                }
                legRight.xRot = (float) rightRad;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getArmDefault() {
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
                armLeft.zRot = (float) (Math.cos(ageInTicks * 0.05) * 0.05 + armLeft.getInitRotZ());
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
                armRight.zRot = (float) (-Math.cos(ageInTicks * 0.05) * 0.05 + armRight.getInitRotZ());
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

    public static IAnimation<EntityMaidRenderState> getArmSwing() {
        return (state, models) -> {
            BedrockPart armLeft = models.get("armLeft");
            BedrockPart armRight = models.get("armRight");

            if (!state.rightHandItemStack.isEmpty() && state.swingingArms) {
                if (armLeft != null) {
                    armLeft.xRot = -1.396f;
                    armLeft.yRot = 0.785f;
                }
                if (armRight != null) {
                    armRight.xRot = -1.396f;
                    armRight.yRot = -0.174f;
                }
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getSitDefault() {
        return (state, models) -> {
            BedrockPart legLeft = models.get("legLeft");
            BedrockPart legRight = models.get("legRight");
            BedrockPart armLeft = models.get("armLeft");
            BedrockPart armRight = models.get("armRight");
            BedrockPart root = IAnimation.root(models);

            if (state.isPassenger) {
                ridingPosture(legLeft, legRight);
                root.offsetY = 0.3f;
            } else if (state.sitting) {
                sittingPosture(armLeft, armRight, legLeft, legRight);
                root.offsetY = 0.3f;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getSitNoLeg() {
        return (state, models) -> {
            BedrockPart legLeft = models.get("legLeft");
            BedrockPart legRight = models.get("legRight");
            BedrockPart armLeft = models.get("armLeft");
            BedrockPart armRight = models.get("armRight");

            if (state.isPassenger) {
                ridingPosture(legLeft, legRight);
            } else if (state.sitting) {
                sittingNoLegPosture(armLeft, armRight);
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getSitSkirtHidden() {
        return (state, models) -> {
            boolean sitting = state.isPassenger || state.sitting;
            setVisible(models.get("sittingHiddenSkirt"), !sitting);
            setVisible(models.get("_sittingHiddenSkirt"), sitting);
        };
    }

    public static IAnimation<EntityMaidRenderState> getSitSkirtRotation() {
        return (state, models) -> {
            BedrockPart sittingRotationSkirt = models.get("sittingRotationSkirt");
            if (sittingRotationSkirt != null && (state.isPassenger || state.sitting)) {
                sittingRotationSkirt.xRot = -0.567f;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getSitSkirtRotationSwing() {
        return (state, models) -> {
            BedrockPart sittingRotationSwingSkirt = models.get("sittingRotationSwingSkirt");
            if (sittingRotationSwingSkirt != null) {
                if (state.isPassenger || state.sitting) {
                    sittingRotationSwingSkirt.xRot = -0.567f;
                } else {
                    float rotationZ = (float) Math.sin(state.ageInTicks * 0.05) * 0.03f;
                    sittingRotationSwingSkirt.zRot = sittingRotationSwingSkirt.getInitRotZ() + rotationZ;
                }
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getWingDefault() {
        return (state, models) -> {
            boolean sleeping = isSleeping(state);
            BedrockPart wingLeft = models.get("wingLeft");
            BedrockPart wingRight = models.get("wingRight");

            if (wingLeft != null) {
                wingLeft.yRot = (float) (-Math.cos(state.ageInTicks * 0.3) * 0.2 + wingLeft.getInitRotY());
                wingLeft.visible = !sleeping;
            }
            if (wingRight != null) {
                wingRight.yRot = (float) (Math.cos(state.ageInTicks * 0.3) * 0.2 + wingRight.getInitRotY());
                wingRight.visible = !sleeping;
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getSleepDefault() {
        return (state, models) -> {
            boolean sleeping = isSleeping(state);
            setVisible(models.get("sleepHide"), !sleeping);
            setVisible(models.get("sleepShow"), sleeping);
        };
    }

    public static IAnimation<EntityMaidRenderState> getStatusBackpack() {
        return (state, models) -> {
            setVisible(models.get("backpackShow"), state.hasBackpack);
            setVisible(models.get("backpackHidden"), !state.hasBackpack);
        };
    }

    public static IAnimation<EntityMaidRenderState> getTailDefault() {
        return (state, models) -> {
            BedrockPart tail = models.get("tail");
            if (tail != null) {
                tail.xRot = (float) (Math.sin(state.ageInTicks * 0.2) * 0.05) + tail.getInitRotX();
                tail.zRot = (float) (Math.cos(state.ageInTicks * 0.2) * 0.1) + tail.getInitRotZ();
                tail.visible = !isSleeping(state);
            }
        };
    }

    public static IAnimation<EntityMaidRenderState> getMaidDefault() {
        return (state, models) -> {
            getHeadDefault().setupAnimation(state, models);
            getHeadBlink().setupAnimation(state, models);
            getHeadBeg().setupAnimation(state, models);
            getLegDefault().setupAnimation(state, models);
            getArmDefault().setupAnimation(state, models);
            getArmSwing().setupAnimation(state, models);
            getArmVertical().setupAnimation(state, models);
            getSitDefault().setupAnimation(state, models);
            getArmorDefault().setupAnimation(state, models);
            getArmorReverse().setupAnimation(state, models);
            getWingDefault().setupAnimation(state, models);
            getTailDefault().setupAnimation(state, models);
            getSitSkirtRotation().setupAnimation(state, models);
            getBaseFloatDefault().setupAnimation(state, models);
        };
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

    private static void sittingPosture(BedrockPart armLeft, BedrockPart armRight, BedrockPart legLeft, BedrockPart legRight) {
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

    private static void sittingNoLegPosture(BedrockPart armLeft, BedrockPart armRight) {
        if (armLeft != null) {
            armLeft.xRot = -0.798f;
            armLeft.zRot = 0.274f;
        }
        if (armRight != null) {
            armRight.xRot = -0.798f;
            armRight.zRot = -0.274f;
        }
    }

    private static boolean isSleeping(EntityMaidRenderState state) {
        return state.sleeping;
    }

    private static void setVisible(BedrockPart part, boolean visible) {
        if (part != null) {
            part.visible = visible;
        }
    }

    private static boolean isSwingLeftHand(EntityMaidRenderState state) {
        return state.attackArm == HumanoidArm.LEFT;
    }
}
