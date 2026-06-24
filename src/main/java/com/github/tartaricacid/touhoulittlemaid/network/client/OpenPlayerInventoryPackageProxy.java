package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.network.message.OpenPlayerInventoryPackage;
import com.github.tartaricacid.touhoulittlemaid.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;

public final class OpenPlayerInventoryPackageProxy {
    public static void handle(OpenPlayerInventoryPackage message) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (message.action() == OpenPlayerInventoryPackage.OPEN_PLAYER_INVENTORY) {
            // 打开玩家背包
            ScreenUtil.setScreen(new InventoryScreen(player));
        }
    }
}
