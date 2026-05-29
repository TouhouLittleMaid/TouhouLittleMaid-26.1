package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import net.minecraft.world.entity.EntityDimensions;

public interface SwimHook extends MaidComponent {
    boolean wantToSwim();

    EntityDimensions getSwimmingDimensions();

    void updateSwimming();

    void resetEatBreatheItem();
}
