package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.compat.curios.CuriosCompat;
import com.github.tartaricacid.touhoulittlemaid.network.message.CuriosS2CUpdatePacket;

public final class CuriosS2CUpdatePacketProxy {
    public static void handle(CuriosS2CUpdatePacket message) {
        CuriosCompat.clientUpdatePage(message.page());
    }
}
