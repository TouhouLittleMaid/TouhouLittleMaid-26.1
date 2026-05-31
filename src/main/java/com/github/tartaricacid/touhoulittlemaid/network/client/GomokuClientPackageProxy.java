package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Point;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidGomokuAI;
import com.github.tartaricacid.touhoulittlemaid.network.message.GomokuClientPackage;
import com.github.tartaricacid.touhoulittlemaid.network.message.GomokuServerPackage;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.concurrent.CompletableFuture;

public final class GomokuClientPackageProxy {
    public static void handle(GomokuClientPackage message) {
        CompletableFuture.runAsync(() -> onHandle(message), Util.backgroundExecutor());
    }

    private static void onHandle(GomokuClientPackage message) {
        Point aiPoint = MaidGomokuAI.getService(message.count()).getPoint(message.chessData().toArray(new byte[15][]), message.point());
        int time = (int) (Math.random() * 1250) + 250;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Minecraft.getInstance().submitAsync(() -> ClientPacketDistributor.sendToServer(new GomokuServerPackage(message.pos(), aiPoint)));
    }
}
