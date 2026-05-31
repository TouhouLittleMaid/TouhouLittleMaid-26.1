package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.SyncBaublePackage;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public final class SyncBaublePackageProxy {
    public static void handle(SyncBaublePackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(message.entityId());
        if (entity instanceof EntityMaid maid) {
            BaubleItemHandler maidBauble = maid.getMaidBauble();
            // 全量同步前需要清空
            if (message.isFull()) {
                maidBauble.clearAll();
            }
            message.baubles().forEach((slot, itemStack) -> ItemsUtil.setStackInSlot(maidBauble, slot, itemStack));
        }
    }
}
