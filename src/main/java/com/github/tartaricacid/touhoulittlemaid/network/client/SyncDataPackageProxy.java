package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.data.MaidNumAttachment;
import com.github.tartaricacid.touhoulittlemaid.data.PowerAttachment;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import com.github.tartaricacid.touhoulittlemaid.network.message.SyncDataPackage;
import net.minecraft.client.Minecraft;

public final class SyncDataPackageProxy {
    public static void handle(SyncDataPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        mc.player.setData(InitDataAttachment.POWER_NUM, new PowerAttachment(message.power()));
        mc.player.setData(InitDataAttachment.MAID_NUM, new MaidNumAttachment(message.maidNum()));
    }
}
