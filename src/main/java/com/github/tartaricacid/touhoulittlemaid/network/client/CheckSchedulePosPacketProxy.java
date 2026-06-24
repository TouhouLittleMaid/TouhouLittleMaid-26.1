package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.other.CheckSchedulePosGui;
import com.github.tartaricacid.touhoulittlemaid.network.message.CheckSchedulePosPacket;
import com.github.tartaricacid.touhoulittlemaid.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class CheckSchedulePosPacketProxy {
    public static void handle(CheckSchedulePosPacket message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        if (ScreenUtil.getScreen() instanceof AbstractMaidContainerGui<?> parent) {
            ScreenUtil.setScreen(new CheckSchedulePosGui(parent, Component.translatable(message.tips())));
        }
    }
}
