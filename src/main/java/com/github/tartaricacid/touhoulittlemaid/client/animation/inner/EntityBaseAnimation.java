package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation.INNER_ANIMATION;

public final class EntityBaseAnimation {
    private static final float DEG_TO_RAD = 0.017453292F;

    public static void init() {
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/dimension/default.js"), getBaseDimDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/float/default.js"), getBaseFloatDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/time/day_night_hidden.js"), getBaseTimeDayNight());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/time/game_rotation.js"), getBaseTimeGameRotation());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/time/system_rotation.js"), getBaseTimeSysRotation());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/reciprocate.js"), getBaseRotationReciprocate());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/x_high_speed.js"), getBaseRotationXH());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/x_normal_speed.js"), getBaseRotationXN());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/x_low_speed.js"), getBaseRotationXL());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/y_high_speed.js"), getBaseRotationYH());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/y_normal_speed.js"), getBaseRotationYN());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/y_low_speed.js"), getBaseRotationYL());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/z_high_speed.js"), getBaseRotationZH());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/z_normal_speed.js"), getBaseRotationZN());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/rotation/z_low_speed.js"), getBaseRotationZL());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/base/random/select.js"), getRandomSelect());
    }

    public static IAnimation<EntityMaidRenderState> getBaseDimDefault() {
        return (state, models) -> {
            ResourceKey<Level> dim = state.dimension;
            if (dim == null) {
                return;
            }
            setVisible(models.get("overWorldHidden"), !dim.equals(Level.OVERWORLD));
            setVisible(models.get("overWorldShow"), dim.equals(Level.OVERWORLD));

            setVisible(models.get("netherWorldHidden"), !dim.equals(Level.NETHER));
            setVisible(models.get("netherWorldShow"), dim.equals(Level.NETHER));

            setVisible(models.get("endWorldHidden"), !dim.equals(Level.END));
            setVisible(models.get("endWorldShow"), dim.equals(Level.END));
        };
    }

    public static IAnimation<LivingEntityRenderState> getBaseFloatDefault() {
        return (state, models) -> {
            float ageInTicks = state.ageInTicks;
            setOffsetY(models.get("sinFloat"), Math.sin(ageInTicks * 0.1) * 0.05);
            setOffsetY(models.get("cosFloat"), Math.cos(ageInTicks * 0.1) * 0.05);
            setOffsetY(models.get("_sinFloat"), -Math.sin(ageInTicks * 0.1) * 0.05);
            setOffsetY(models.get("_cosFloat"), -Math.cos(ageInTicks * 0.1) * 0.05);
        };
    }

    public static IAnimation<EntityMaidRenderState> getBaseTimeDayNight() {
        return (state, models) -> {
            long dayTime = state.gameTime % 24000;
            setVisible(models.get("dayShow"), dayTime < 13000);
            setVisible(models.get("nightShow"), dayTime >= 13000);
        };
    }

    public static IAnimation<EntityMaidRenderState> getBaseTimeGameRotation() {
        return (state, models) -> {
            long time = state.gameTime % 24000;
            double hourDeg = Math.PI + ((time / 1000.0) % 12) * (Math.PI / 6);
            double minDeg = ((time % 1000) / (50 / 3.0)) * (Math.PI / 30);

            setXRot(models.get("gameHourRotationX"), hourDeg);
            setXRot(models.get("gameMinuteRotationX"), minDeg);

            setYRot(models.get("gameHourRotationY"), hourDeg);
            setYRot(models.get("gameMinuteRotationY"), minDeg);

            setZRot(models.get("gameHourRotationZ"), hourDeg);
            setZRot(models.get("gameMinuteRotationZ"), minDeg);
        };
    }

    public static IAnimation<LivingEntityRenderState> getBaseTimeSysRotation() {
        return (ignored, models) -> {
            LocalTime now = LocalTime.now();
            float hourDeg = ((now.getHour() + now.getMinute() / 60f) % 12) * ((float) Math.PI / 6);
            float minDeg = (now.getMinute() + now.getSecond() / 60f) * ((float) Math.PI / 30);
            float secDeg = now.getSecond() * ((float) Math.PI / 30);

            setXRot(models.get("systemHourRotationX"), hourDeg);
            setXRot(models.get("systemMinuteRotationX"), minDeg);
            setXRot(models.get("systemSecondRotationX"), secDeg);

            setYRot(models.get("systemHourRotationY"), hourDeg);
            setYRot(models.get("systemMinuteRotationY"), minDeg);
            setYRot(models.get("systemSecondRotationY"), secDeg);

            setZRot(models.get("systemHourRotationZ"), hourDeg);
            setZRot(models.get("systemMinuteRotationZ"), minDeg);
            setZRot(models.get("systemSecondRotationZ"), secDeg);
        };
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationReciprocate() {
        return (state, models) -> {
            double angle = Math.cos(state.ageInTicks * 0.3) * 0.2;
            setXRot(models.get("xReciprocate"), angle);
            setYRot(models.get("yReciprocate"), angle);
            setZRot(models.get("zReciprocate"), angle);
        };
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationXH() {
        return (state, models) ->
                rotatePartsX(models, (state.ageInTicks * 4) % 360 * DEG_TO_RAD,
                        "xRotationHighA",
                        "xRotationHighB",
                        "xRotationHighC",
                        "xRotationHighD",
                        "xRotationHighE"
                );
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationXN() {
        return (state, models) ->
                rotatePartsX(models, state.ageInTicks % 360 * DEG_TO_RAD,
                        "xRotationNormalA",
                        "xRotationNormalB",
                        "xRotationNormalC",
                        "xRotationNormalD",
                        "xRotationNormalE"
                );
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationXL() {
        return (state, models) ->
                rotatePartsX(models, (state.ageInTicks / 4) % 360 * DEG_TO_RAD,
                        "xRotationLowA",
                        "xRotationLowB",
                        "xRotationLowC",
                        "xRotationLowD",
                        "xRotationLowE"
                );
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationYH() {
        return (state, models) ->
                rotatePartsY(models, (state.ageInTicks * 4) % 360 * DEG_TO_RAD,
                        "yRotationHighA",
                        "yRotationHighB",
                        "yRotationHighC",
                        "yRotationHighD",
                        "yRotationHighE"
                );
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationYN() {
        return (state, models) ->
                rotatePartsY(models, state.ageInTicks % 360 * DEG_TO_RAD,
                        "yRotationNormalA",
                        "yRotationNormalB",
                        "yRotationNormalC",
                        "yRotationNormalD",
                        "yRotationNormalE"
                );
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationYL() {
        return (state, models) ->
                rotatePartsY(models, (state.ageInTicks / 4) % 360 * DEG_TO_RAD,
                        "yRotationLowA",
                        "yRotationLowB",
                        "yRotationLowC",
                        "yRotationLowD",
                        "yRotationLowE"
                );
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationZH() {
        return (state, models) ->
                rotatePartsZ(models, (state.ageInTicks * 4) % 360 * DEG_TO_RAD,
                        "zRotationHighA",
                        "zRotationHighB",
                        "zRotationHighC",
                        "zRotationHighD",
                        "zRotationHighE"
                );
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationZN() {
        return (state, models) ->
                rotatePartsZ(models, state.ageInTicks % 360 * DEG_TO_RAD,
                        "zRotationNormalA",
                        "zRotationNormalB",
                        "zRotationNormalC",
                        "zRotationNormalD",
                        "zRotationNormalE"
                );
    }

    public static IAnimation<LivingEntityRenderState> getBaseRotationZL() {
        return (state, models) ->
                rotatePartsZ(models, (state.ageInTicks / 4) % 360 * DEG_TO_RAD,
                        "zRotationLowA",
                        "zRotationLowB",
                        "zRotationLowC",
                        "zRotationLowD",
                        "zRotationLowE"
                );
    }

    public static IAnimation<EntityMaidRenderState> getRandomSelect() {
        return (state, models) -> {
            BedrockPart[] randomSelect = Arrays.stream(new BedrockPart[]{
                    models.get("randomSelect1"),
                    models.get("randomSelect2"),
                    models.get("randomSelect3"),
                    models.get("randomSelect4"),
                    models.get("randomSelect5")
            }).filter(Objects::nonNull).toArray(BedrockPart[]::new);

            if (randomSelect.length == 0) {
                return;
            }

            long index = Math.abs(state.randomNumber) % randomSelect.length;
            for (int i = 0; i < randomSelect.length; i++) {
                randomSelect[i].visible = i == index;
            }
        };
    }

    private static void rotatePartsX(HashMap<String, BedrockPart> models, float angle, String... partNames) {
        for (String partName : partNames) {
            setXRot(models.get(partName), angle);
        }
    }

    private static void rotatePartsY(HashMap<String, BedrockPart> models, float angle, String... partNames) {
        for (String partName : partNames) {
            setYRot(models.get(partName), angle);
        }
    }

    private static void rotatePartsZ(HashMap<String, BedrockPart> models, float angle, String... partNames) {
        for (String partName : partNames) {
            setZRot(models.get(partName), angle);
        }
    }

    private static void setVisible(BedrockPart part, boolean visible) {
        if (part != null) {
            part.visible = visible;
        }
    }

    private static void setXRot(BedrockPart part, double angle) {
        if (part != null) {
            part.xRot = (float) angle;
        }
    }

    private static void setYRot(BedrockPart part, double angle) {
        if (part != null) {
            part.yRot = (float) angle;
        }
    }

    private static void setZRot(BedrockPart part, double angle) {
        if (part != null) {
            part.zRot = (float) angle;
        }
    }

    private static void setOffsetY(BedrockPart part, double offsetY) {
        if (part != null) {
            part.offsetY = (float) offsetY;
        }
    }
}
