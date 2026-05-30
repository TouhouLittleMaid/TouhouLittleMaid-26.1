package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.entity.data.AnimationData;
import net.minecraft.world.item.ItemStack;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.ANIMATION;

/**
 * 动画管理器，提供一些方法来设置、获取、同步女仆的动画状态
 */
@MaidManagerDef(alias = "animationManager", exposeView = true)
public class MaidAnimationManager {
    /**
     * 用于方便特殊动画播放的变量，目前仅支持捡雪球
     */
    public int animationId = 0;
    public long animationRecordTime = -1L;
    public boolean shouldReset = false;

    public final ItemStack[] handItemsForAnimation = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};

    private final EntityMaid maid;

    MaidAnimationManager(EntityMaid maid) {
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

    public interface View {
        MaidAnimationManager getAnimationManager();

        default boolean isBegging() {
            return getAnimationManager().isBegging();
        }

        default void setBegging(boolean begging) {
            getAnimationManager().setBegging(begging);
        }

        default boolean isChargingCrossbow() {
            return getAnimationManager().isChargingCrossbow();
        }

        default boolean isSwingingArms() {
            return getAnimationManager().isSwingingArms();
        }

        default void setSwingingArms(boolean swingingArms) {
            getAnimationManager().setSwingingArms(swingingArms);
        }

        default boolean isAiming() {
            return getAnimationManager().isAiming();
        }

        default void setAiming(boolean aiming) {
            getAnimationManager().setAiming(aiming);
        }

        default ItemStack[] getHandItemsForAnimation() {
            return getAnimationManager().getHandItemsForAnimation();
        }
    }
}

