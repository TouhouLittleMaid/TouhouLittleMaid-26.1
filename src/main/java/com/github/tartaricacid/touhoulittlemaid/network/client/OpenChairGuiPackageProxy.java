package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.model.ChairModelGui;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.network.message.OpenChairGuiPackage;
import com.github.tartaricacid.touhoulittlemaid.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public final class OpenChairGuiPackageProxy {
    public static void handle(OpenChairGuiPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity e = mc.level.getEntity(message.id());
        if (mc.player != null && mc.player.isAlive() && e instanceof EntityChair chair && e.isAlive()) {
            ScreenUtil.setScreen(new ChairModelGui(chair));
        }
    }
}
