package com.github.tartaricacid.touhoulittlemaid.client.overlay;

import com.github.tartaricacid.touhoulittlemaid.data.PowerAttachment;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemGohei;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.ScreenUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.GuiLayer;

public class ShowPowerOverlay implements GuiLayer {
    private static ItemStack POWER_POINT;

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || ScreenUtil.isHideGui()) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!ItemGohei.isGohei(stack)) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        if (POWER_POINT == null) {
            POWER_POINT = InitItems.POWER_POINT.get().getDefaultInstance();
        }
        guiGraphics.item(POWER_POINT, 5, 5);
        PowerAttachment cap = player.getData(InitDataAttachment.POWER_NUM);
        guiGraphics.text(font, String.format("%s×%.2f", ChatFormatting.BOLD, cap.get()), 20, 10, 0xFFFFFFFF);
    }
}
