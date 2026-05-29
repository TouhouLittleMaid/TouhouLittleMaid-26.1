package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import net.minecraft.world.phys.Vec3;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public interface ClimbHook extends MaidComponent {
    Vec3 handleOnClimbable(Vec3 deltaMovement);

    boolean onClimbable(BooleanSupplier superOnClimbable);

    Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 deltaMovement, float friction);

    void travel(Vec3 travelVector, Consumer<Vec3> superTravel);
}
