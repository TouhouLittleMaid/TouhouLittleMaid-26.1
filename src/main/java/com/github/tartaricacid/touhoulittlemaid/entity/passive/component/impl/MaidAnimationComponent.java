package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl;

import com.github.tartaricacid.touhoulittlemaid.entity.data.AnimationData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponentDef;
import net.minecraft.world.item.ItemStack;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.ANIMATION;

/**
 * 动画管理器，提供一些方法来设置、获取、同步女仆的动画状态
 */
@MaidComponentDef("animation")
public class MaidAnimationComponent implements MaidComponent {
    /**
     * 用于方便特殊动画播放的变量，目前仅支持捡雪球
     */
    public int animationId = 0;
    public long animationRecordTime = -1L;
    public boolean shouldReset = false;

    public final ItemStack[] handItemsForAnimation = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};

    private final EntityMaid maid;

    public MaidAnimationComponent(EntityMaid maid) {
        this.maid = maid;
    }

    public boolean isBegging() {
        return this.getAnimationData().isBegging();
    }

    public void setBegging(boolean begging) {
        this.setAnimationData(this.getAnimationData().setBegging(begging));
    }

    public boolean isChargingCrossbow() {
        return this.getAnimationData().isChargingCrossbow();
    }

    public void setChargingCrossbow(boolean charging) {
        this.setAnimationData(this.getAnimationData().setChargingCrossbow(charging));
    }

    public boolean isSwingingArms() {
        return this.getAnimationData().isSwingingArms();
    }

    public void setSwingingArms(boolean swingingArms) {
        this.setAnimationData(this.getAnimationData().setSwingingArms(swingingArms));
    }

    public boolean isAiming() {
        return this.getAnimationData().isAiming();
    }

    public void setAiming(boolean aiming) {
        this.setAnimationData(this.getAnimationData().setAiming(aiming));
    }

    public ItemStack[] getHandItemsForAnimation() {
        return handItemsForAnimation;
    }

    private AnimationData getAnimationData() {
        return this.maid.getData(ANIMATION);
    }

    private void setAnimationData(AnimationData data) {
        this.maid.setData(ANIMATION, data);
    }
}

