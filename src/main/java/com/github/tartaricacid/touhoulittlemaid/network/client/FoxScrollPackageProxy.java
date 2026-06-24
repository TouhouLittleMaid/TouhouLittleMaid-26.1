package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.client.gui.item.FoxScrollScreen;
import com.github.tartaricacid.touhoulittlemaid.network.message.FoxScrollPackage;
import com.github.tartaricacid.touhoulittlemaid.util.ScreenUtil;
import net.minecraft.client.Minecraft;

public final class FoxScrollPackageProxy {
    public static void handle(FoxScrollPackage message) {
        ScreenUtil.setScreen(new FoxScrollScreen(message.data()));
    }
}
