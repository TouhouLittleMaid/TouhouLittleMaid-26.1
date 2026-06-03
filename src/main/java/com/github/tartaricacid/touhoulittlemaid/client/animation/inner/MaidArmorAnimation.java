package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.api.animation.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation.INNER_ANIMATION;

public final class MaidArmorAnimation {
    private static final String[] HELMET_PARTS = {"helmet"};
    private static final String[] CHEST_PARTS = {"chestPlate", "chestPlateLeft", "chestPlateMiddle", "chestPlateRight"};
    private static final String[] LEGGING_PARTS = {"leggings", "leggingsLeft", "leggingsMiddle", "leggingsRight"};
    private static final String[] BOOT_PARTS = {"bootsLeft", "bootsRight"};

    public static void init() {
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/armor/default.js"), getArmorDefault());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/armor/reverse.js"), getArmorReverse());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/armor/value/value_full.js"), getArmorValueFull());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/armor/value/value_high.js"), getArmorValueHigh());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/armor/value/value_low.js"), getArmorValueLow());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/armor/value/value_normal.js"), getArmorValueNormal());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/armor/weather/raining.js"), getArmorWeatherRaining());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/armor/weather/thundering.js"), getArmorWeatherThundering());
    }

    public static IAnimation<EntityMaidRenderState> getArmorDefault() {
        return (state, models) ->
                setArmorVisible(models,
                        StringUtils.EMPTY, StringUtils.EMPTY,
                        !state.headEquipment.isEmpty(),
                        !state.chestEquipment.isEmpty(),
                        !state.legsEquipment.isEmpty(),
                        !state.feetEquipment.isEmpty()
                );
    }

    public static IAnimation<EntityMaidRenderState> getArmorReverse() {
        return (state, models) ->
                setArmorVisible(models,
                        "_", StringUtils.EMPTY,
                        state.headEquipment.isEmpty(),
                        state.chestEquipment.isEmpty(),
                        state.legsEquipment.isEmpty(),
                        state.feetEquipment.isEmpty()
                );
    }

    public static IAnimation<EntityMaidRenderState> getArmorValueFull() {
        return (state, models) ->
                setArmorVisible(models, "ValueFull", state.armorValue > 15);
    }

    public static IAnimation<EntityMaidRenderState> getArmorValueHigh() {
        return (state, models) -> {
            int armorValue = state.armorValue;
            boolean match = 10 < armorValue && armorValue <= 15;
            setArmorVisible(models, "ValueHigh", match);
        };
    }

    public static IAnimation<EntityMaidRenderState> getArmorValueLow() {
        return (state, models) -> {
            int armorValue = state.armorValue;
            boolean match = 0 < armorValue && armorValue <= 5;
            setArmorVisible(models, "ValueLow", match);
        };
    }

    public static IAnimation<EntityMaidRenderState> getArmorValueNormal() {
        return (state, models) -> {
            int armorValue = state.armorValue;
            boolean match = 5 < armorValue && armorValue <= 10;
            setArmorVisible(models, "ValueNormal", match);
        };
    }

    public static IAnimation<EntityMaidRenderState> getArmorWeatherRaining() {
        return (state, models) ->
                setArmorVisible(models, "WeatherRaining", state.raining);
    }

    public static IAnimation<EntityMaidRenderState> getArmorWeatherThundering() {
        return (state, models) ->
                setArmorVisible(models, "WeatherThundering", state.thundering);
    }

    private static void setArmorVisible(HashMap<String, BedrockPart> models, String suffix, boolean visible) {
        setArmorVisible(models, "", suffix, visible, visible, visible, visible);
    }

    private static void setArmorVisible(HashMap<String, BedrockPart> models,
                                        String prefix, String suffix,
                                        boolean helmetVisible, boolean chestVisible,
                                        boolean leggingsVisible, boolean bootsVisible) {
        setPartsVisible(models, prefix, suffix, helmetVisible, HELMET_PARTS);
        setPartsVisible(models, prefix, suffix, chestVisible, CHEST_PARTS);
        setPartsVisible(models, prefix, suffix, leggingsVisible, LEGGING_PARTS);
        setPartsVisible(models, prefix, suffix, bootsVisible, BOOT_PARTS);
    }

    private static void setPartsVisible(HashMap<String, BedrockPart> models,
                                        String prefix, String suffix,
                                        boolean visible, String... parts) {
        for (String partName : parts) {
            BedrockPart part = models.get(prefix + partName + suffix);
            if (part != null) {
                part.visible = visible;
            }
        }
    }
}
