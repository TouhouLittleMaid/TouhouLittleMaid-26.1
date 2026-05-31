package com.github.tartaricacid.touhoulittlemaid.client.proxy;

import com.github.tartaricacid.touhoulittlemaid.client.gui.item.ServantBellSetScreen;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.client.Minecraft;

public class ItemServantBellProxy {
    public static void openServantBellSetScreen(EntityMaid maid) {
        if (maid.level.isClientSide()) {
            Minecraft.getInstance().setScreen(new ServantBellSetScreen(maid));
        }
    }
}
