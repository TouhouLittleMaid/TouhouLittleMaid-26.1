package com.github.tartaricacid.touhoulittlemaid.network.client;

import com.github.tartaricacid.touhoulittlemaid.client.gui.block.ModelSwitcherGui;
import com.github.tartaricacid.touhoulittlemaid.network.message.OpenSwitcherGuiPackage;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityModelSwitcher;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class OpenSwitcherGuiPackageProxy {
    public static void handle(OpenSwitcherGuiPackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        BlockEntity te = mc.level.getBlockEntity(message.pos());
        if (mc.player != null && mc.player.isAlive() && te instanceof TileEntityModelSwitcher) {
            mc.setScreen(new ModelSwitcherGui((TileEntityModelSwitcher) te));
        }
    }
}
