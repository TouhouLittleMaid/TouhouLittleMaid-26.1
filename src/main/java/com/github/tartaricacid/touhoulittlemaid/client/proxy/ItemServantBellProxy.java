package com.github.tartaricacid.touhoulittlemaid.client.proxy;

import com.github.tartaricacid.touhoulittlemaid.client.gui.item.ServantBellSetScreen;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ScreenUtil;
import net.minecraft.client.Minecraft;

public class ItemServantBellProxy {
    public static void openServantBellSetScreen(EntityMaid maid) {
        if (maid.level.isClientSide()) {
            ScreenUtil.setScreen(new ServantBellSetScreen(maid));
        }
    }
}
