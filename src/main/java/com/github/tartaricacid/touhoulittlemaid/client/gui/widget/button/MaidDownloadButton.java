package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.client.download.InfoGetManager;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.ModelDownloadGui;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.ScreenUtil;
import com.github.tartaricacid.touhoulittlemaid.util.TipsHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class MaidDownloadButton extends TouhouImageButton {
    public MaidDownloadButton(int pX, int pY, Identifier texture, EntityMaid maid) {
        super(pX, pY, 41, 20, 0, 86, 20, texture, (b) -> {
            InfoGetManager.STATUE = InfoGetManager.Statue.NOT_UPDATE;
            ScreenUtil.setScreen(new ModelDownloadGui(maid));
        });
    }

    public void renderExtraTips(GuiGraphicsExtractor graphics) {
        TipsHelper.renderTips(graphics, this, getText());
    }

    private MutableComponent getText() {
        if (InfoGetManager.STATUE == InfoGetManager.Statue.FIRST) {
            return Component.translatable("gui.touhou_little_maid.button.model_download.statue.first");
        }
        if (InfoGetManager.STATUE == InfoGetManager.Statue.UPDATE) {
            return Component.translatable("gui.touhou_little_maid.button.model_download.statue.update");
        }
        return Component.empty();
    }
}
