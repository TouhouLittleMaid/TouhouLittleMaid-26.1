package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.navigation.MaidPathNavigation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

public class MaidSwimManager {
    /**
     * 游泳碰撞箱
     */
    private static final EntityDimensions SWIMMING_DIMENSIONS = EntityDimensions.scalable(0.6F, 0.6F);
    private final MaidPathNavigation groundNavigation;
    private final AmphibiousPathNavigation waterNavigation;
    private final EntityMaid maid;
    /**
     * 正在食用可提供水下呼吸的食物标志位
     */
    private boolean isEatBreatheItem = false;
    /**
     * 主动游泳标志位
     */
    private boolean wantToSwim = false;

    public MaidSwimManager(EntityMaid maid) {
        this.maid = maid;
        this.groundNavigation = new MaidPathNavigation(maid, maid.level);
        this.waterNavigation = new AmphibiousPathNavigation(maid, maid.level);
        maid.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    /**
     * 重置状态，从而控制饮用药水
     */
    public void resetEatBreatheItem() {
        if (this.isEatBreatheItem) {
            this.isEatBreatheItem = false;
        }
    }

    /**
     * 依据游泳状态，切换成游泳的寻路
     */
    public void updateSwimming() {
        if (!maid.level.isClientSide) {
            if (maid.isEffectiveAi() && maid.isInWater()) {
                maid.setNavigation(this.waterNavigation);
            } else {
                maid.setNavigation(this.groundNavigation);
                this.setWantToSwim(false);
            }

            this.updatePose();
        }
    }

    /**
     * 更新游泳姿势同时更新碰撞箱
     */
    private void updatePose() {
        if (this.wantToSwim() && !maid.onGround()) {
            maid.setSwimming(true);
            maid.setPose(Pose.SWIMMING);
        } else {
            maid.setSwimming(false);
            // 也许有更好的方式?
            if (!maid.isSleeping()) {
                maid.setPose(Pose.STANDING);
            }
        }
    }

    public void setWantToSwim(boolean pSearchingForLand) {
        this.wantToSwim = pSearchingForLand;
    }

    public boolean wantToSwim() {
        return this.wantToSwim;
    }

    public boolean isEatBreatheItem() {
        return isEatBreatheItem;
    }

    public void setEatBreatheItem(boolean eatBreatheItem) {
        isEatBreatheItem = eatBreatheItem;
    }

    public EntityDimensions getSwimmingDimensions() {
        return SWIMMING_DIMENSIONS;
    }
}
