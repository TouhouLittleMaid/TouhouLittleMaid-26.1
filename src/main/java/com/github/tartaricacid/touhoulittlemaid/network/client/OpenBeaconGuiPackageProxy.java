package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.client.gui.block.MaidBeaconGui;
import com.github.tartaricacid.touhoulittlemaid.network.message.OpenBeaconGuiPackage;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityMaidBeacon;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class OpenBeaconGuiPackageProxy {
    public static void handle(OpenBeaconGuiPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        BlockEntity te = mc.level.getBlockEntity(message.pos());
        if (mc.player != null && mc.player.isAlive() && te instanceof TileEntityMaidBeacon) {
            mc.setScreen(new MaidBeaconGui((TileEntityMaidBeacon) te));
        }
    }
}
