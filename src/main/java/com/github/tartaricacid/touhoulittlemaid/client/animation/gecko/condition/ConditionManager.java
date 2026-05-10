package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition;

import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;

import java.util.Map;

public class ConditionManager {
    public static Map<Identifier, ConditionalSwing> SWING = Maps.newHashMap();
    public static Map<Identifier, ConditionalSwing> SWING_OFFHAND = Maps.newHashMap();
    public static Map<Identifier, ConditionalUse> USE_MAINHAND = Maps.newHashMap();
    public static Map<Identifier, ConditionalUse> USE_OFFHAND = Maps.newHashMap();
    public static Map<Identifier, ConditionalHold> HOLD_MAINHAND = Maps.newHashMap();
    public static Map<Identifier, ConditionalHold> HOLD_OFFHAND = Maps.newHashMap();
    public static Map<Identifier, ConditionTAC> TAC = Maps.newHashMap();
    public static Map<Identifier, ConditionArmor> ARMOR = Maps.newHashMap();
    public static Map<Identifier, ConditionalVehicle> VEHICLE = Maps.newHashMap();
    public static Map<Identifier, ConditionalPassenger> PASSENGER = Maps.newHashMap();
    public static Map<Identifier, ConditionalChair> CHAIR = Maps.newHashMap();

    public static void addTest(Identifier id, String name) {
        SWING.computeIfAbsent(id, k -> new ConditionalSwing(InteractionHand.MAIN_HAND)).addTest(name);
        SWING_OFFHAND.computeIfAbsent(id, k -> new ConditionalSwing(InteractionHand.OFF_HAND)).addTest(name);
        USE_MAINHAND.computeIfAbsent(id, k -> new ConditionalUse(InteractionHand.MAIN_HAND)).addTest(name);
        USE_OFFHAND.computeIfAbsent(id, k -> new ConditionalUse(InteractionHand.OFF_HAND)).addTest(name);
        HOLD_MAINHAND.computeIfAbsent(id, k -> new ConditionalHold(InteractionHand.MAIN_HAND)).addTest(name);
        HOLD_OFFHAND.computeIfAbsent(id, k -> new ConditionalHold(InteractionHand.OFF_HAND)).addTest(name);
        TAC.computeIfAbsent(id, k -> new ConditionTAC()).addTest(name);
        ARMOR.computeIfAbsent(id, k -> new ConditionArmor()).addTest(name);
        VEHICLE.computeIfAbsent(id, k -> new ConditionalVehicle()).addTest(name);
        PASSENGER.computeIfAbsent(id, k -> new ConditionalPassenger()).addTest(name);
        CHAIR.computeIfAbsent(id, k -> new ConditionalChair()).addTest(name);
    }

    public static void clear() {
        SWING.clear();
        SWING_OFFHAND.clear();
        USE_MAINHAND.clear();
        USE_OFFHAND.clear();
        HOLD_MAINHAND.clear();
        HOLD_OFFHAND.clear();
        TAC.clear();
        ARMOR.clear();
        VEHICLE.clear();
        PASSENGER.clear();
        CHAIR.clear();
    }

    public static ConditionalSwing getSwingMainhand(Identifier id) {
        return SWING.get(id);
    }

    public static ConditionalSwing getSwingOffhand(Identifier id) {
        return SWING_OFFHAND.get(id);
    }

    public static ConditionalUse getUseMainhand(Identifier id) {
        return USE_MAINHAND.get(id);
    }

    public static ConditionalUse getUseOffhand(Identifier id) {
        return USE_OFFHAND.get(id);
    }

    public static ConditionalHold getHoldMainhand(Identifier id) {
        return HOLD_MAINHAND.get(id);
    }

    public static ConditionalHold getHoldOffhand(Identifier id) {
        return HOLD_OFFHAND.get(id);
    }

    public static ConditionArmor getArmor(Identifier id) {
        return ARMOR.get(id);
    }

    public static ConditionTAC getTAC(Identifier id) {
        return TAC.get(id);
    }

    public static ConditionalVehicle getVehicle(Identifier id) {
        return VEHICLE.get(id);
    }

    public static ConditionalPassenger getPassenger(Identifier id) {
        return PASSENGER.get(id);
    }

    public static ConditionalChair getChair(Identifier id) {
        return CHAIR.get(id);
    }
}
