package com.github.tartaricacid.touhoulittlemaid.item.bauble;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ItemMagnetBauble implements IMaidBauble {
    private static final int DELAY = 3 * 20;
    private static final int RANGE = 6;

    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.tickCount % DELAY == 0) {
            handlePickup(maid);
        }
    }

    private void handlePickup(EntityMaid maid) {
        Level world = maid.level();
        if (!maid.isPickup() || !maid.isTame() || !maid.isAlive()) {
            return;
        }

        AABB aabb = maid.getBoundingBox().inflate(RANGE);
        List<Entity> entities = world.getEntities(maid, aabb, maid::canPickup);
        if (entities.isEmpty()) {
            return;
        }

        for (Entity entityPickup : entities) {
            // 如果是物品
            if (entityPickup instanceof ItemEntity) {
                maid.pickupItem((ItemEntity) entityPickup, false);
            }
            // 如果是经验
            if (entityPickup instanceof ExperienceOrb) {
                maid.pickupXPOrb((ExperienceOrb) entityPickup);
            }
            // 如果是 P 点
            if (entityPickup instanceof EntityPowerPoint) {
                maid.pickupPowerPoint((EntityPowerPoint) entityPickup);
            }
            // 如果是箭
            if (entityPickup instanceof AbstractArrow) {
                maid.pickupArrow((AbstractArrow) entityPickup, false);
            }
        }
        if (maid.getOwner() instanceof ServerPlayer serverPlayer) {
            InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.USE_ITEM_MAGNET_BAUBLE);
        }
    }
}
