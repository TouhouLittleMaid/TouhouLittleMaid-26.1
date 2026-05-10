package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidPickupEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

public abstract class MaidPickupEventJS implements KubeEvent {
    private final EntityMaid maid;
    private final boolean simulate;
    private boolean canPickup = false;

    public MaidPickupEventJS(EntityMaid maid, boolean simulate) {
        this.maid = maid;
        this.simulate = simulate;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public boolean isSimulate() {
        return simulate;
    }

    public boolean isCanPickup() {
        return canPickup;
    }

    public void setCanPickup(boolean canPickup) {
        this.canPickup = canPickup;
    }

    public static class ItemResultPre extends MaidPickupEventJS {
        private final ItemEntity entityItem;

        public ItemResultPre(MaidPickupEvent.ItemResultPre event) {
            super(event.getMaid(), event.isSimulate());
            this.entityItem = event.getEntityItem();
        }

        public ItemEntity getEntityItem() {
            return entityItem;
        }
    }

    public static class ItemResultPost extends MaidPickupEventJS {
        private final ItemStack pickupItem;

        public ItemResultPost(MaidPickupEvent.ItemResultPost event) {
            super(event.getMaid(), event.isSimulate());
            this.pickupItem = event.getPickupItem();
        }

        public ItemStack getPickupItem() {
            return pickupItem;
        }
    }

    public static class ExperienceResult extends MaidPickupEventJS {
        private final ExperienceOrb experienceOrb;

        public ExperienceResult(MaidPickupEvent.ExperienceResult event) {
            super(event.getMaid(), event.isSimulate());
            this.experienceOrb = event.getExperienceOrb();
        }

        public ExperienceOrb getExperienceOrb() {
            return experienceOrb;
        }
    }

    public static class ArrowResult extends MaidPickupEventJS {
        private final AbstractArrow arrow;

        public ArrowResult(MaidPickupEvent.ArrowResult event) {
            super(event.getMaid(), event.isSimulate());
            this.arrow = event.getArrow();
        }

        public AbstractArrow getArrow() {
            return arrow;
        }
    }

    public static class PowerPointResult extends MaidPickupEventJS {
        private final EntityPowerPoint powerPoint;

        public PowerPointResult(MaidPickupEvent.PowerPointResult event) {
            super(event.getMaid(), event.isSimulate());
            this.powerPoint = event.getPowerPoint();
        }

        public EntityPowerPoint getPowerPoint() {
            return powerPoint;
        }
    }
}
