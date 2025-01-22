package com.github.tartaricacid.touhoulittlemaid.entity.ai.control;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

/**
 * 参考：net.minecraft.world.entity.monster.Drowned.DrownedMoveControl
 */
public class MaidMoveControl extends MoveControl {
    private final EntityMaid maid;

    public MaidMoveControl(EntityMaid maid) {
        super(maid);
        this.maid = maid;
    }

    @Override
    public void tick() {
        // 用 underwater 是因为如果是 inWater 的话，女仆在登陆的时候，就会一直卡住，上不来
        if (this.maid.isUnderWater() && this.hasWanted()) {
            PathNavigation navigation = this.maid.getNavigation();
            if (navigation.isDone()) {
                this.maid.getSwimManager().setWantToSwim(false);
            } else if (navigation.getPath() != null || maid.getTarget() != null) {
                this.maid.getSwimManager().setWantToSwim(true);
            }

            if (this.operation != MoveControl.Operation.MOVE_TO || this.maid.getNavigation().isDone()) {
                this.maid.setSpeed(0.0F);
                return;
            }

            double x = this.wantedX - this.maid.getX();
            double y = this.wantedY - this.maid.getY();
            double z = this.wantedZ - this.maid.getZ();
            double sqrt = Math.sqrt(x * x + y * y + z * z);
            float yRot = (float) (Math.toDegrees(Mth.atan2(z, x)) - 90);

            this.maid.setYRot(this.rotlerp(this.maid.getYRot(), yRot, 90.0F));
            this.maid.yBodyRot = this.maid.getYRot();
            float speed = (float) (this.speedModifier * this.maid.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float speedLerp = Mth.lerp(1, this.maid.getSpeed(), speed);
            // 太慢了，3 倍基础速度
            this.maid.setSpeed(speedLerp * 3);
            this.maid.setDeltaMovement(this.maid.getDeltaMovement().add(speedLerp * x * 0.005, speedLerp * y / sqrt * 0.25, speedLerp * z * 0.005));
        } else {
            super.tick();
        }
    }
}