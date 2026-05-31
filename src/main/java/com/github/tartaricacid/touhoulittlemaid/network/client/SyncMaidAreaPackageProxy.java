package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.client.event.MaidAreaRenderEvent;
import com.github.tartaricacid.touhoulittlemaid.network.message.SyncMaidAreaPackage;

public final class SyncMaidAreaPackageProxy {
    public static void handle(SyncMaidAreaPackage message) {
        MaidAreaRenderEvent.addSchedulePos(message.id(), message.schedulePos());
    }
}
