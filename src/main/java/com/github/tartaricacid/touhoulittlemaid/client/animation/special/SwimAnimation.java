package com.github.tartaricacid.touhoulittlemaid.client.animation.special;

import com.github.tartaricacid.touhoulittlemaid.api.animation.ICustomAnimation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class SwimAnimation implements ICustomAnimation<Mob> {
    @Override
    public void setupRotations(Mob mob, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        boolean inSwimFluidType = mob.isInFluidType((type, height) -> mob.canSwimInFluidType(type));
        boolean inSwim = mob.isInWater() || inSwimFluidType;
        float xRot = 90 + (inSwim ? mob.getXRot() : 0);
        float xRotLerp = Mth.lerp(mob.getSwimAmount(partialTicks), 0, -xRot);
        poseStack.mulPose(Axis.XP.rotationDegrees(xRotLerp));
        if (mob.isVisuallySwimming()) {
            poseStack.translate(0F, -1F, 0.3F);
        }
    }
}
