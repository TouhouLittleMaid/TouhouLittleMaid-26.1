package com.github.tartaricacid.touhoulittlemaid.geckolib3.resource;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition.*;
import net.minecraft.world.InteractionHand;

public class ConditionManager {
    public final ConditionalSwing swing = new ConditionalSwing(InteractionHand.MAIN_HAND);
    public final ConditionalSwing swingOffhand = new ConditionalSwing(InteractionHand.OFF_HAND);
    public final ConditionalUse useMainhand = new ConditionalUse(InteractionHand.MAIN_HAND);
    public final ConditionalUse useOffhand = new ConditionalUse(InteractionHand.OFF_HAND);
    public final ConditionalHold holdMainhand = new ConditionalHold(InteractionHand.MAIN_HAND);
    public final ConditionalHold holdOffhand = new ConditionalHold(InteractionHand.OFF_HAND);
    public final ConditionTAC tac = new ConditionTAC();
    public final ConditionArmor armor = new ConditionArmor();
    public final ConditionalVehicle vehicle = new ConditionalVehicle();
    public final ConditionalPassenger passenger = new ConditionalPassenger();
    public final ConditionalChair chair = new ConditionalChair();

    public void addTest(String name) {
        swing.addTest(name);
        swingOffhand.addTest(name);
        useMainhand.addTest(name);
        useOffhand.addTest(name);
        holdMainhand.addTest(name);
        holdOffhand.addTest(name);
        tac.addTest(name);
        armor.addTest(name);
        vehicle.addTest(name);
        passenger.addTest(name);
        chair.addTest(name);
    }
}
