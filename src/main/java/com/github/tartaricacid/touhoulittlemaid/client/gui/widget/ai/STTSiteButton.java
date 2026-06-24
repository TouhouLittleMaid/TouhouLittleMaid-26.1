package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.ai;

import com.github.tartaricacid.touhoulittlemaid.ai.service.stt.STTSite;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.settings.AIChatSettingsSTTSiteScreen;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.I18nUtil;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class STTSiteButton extends Button {
    private static final Identifier MISC = IdentifierUtil.modLoc("textures/gui/ai_chat/misc.png");

    private final STTSite site;
    private final AIChatSettingsSTTSiteScreen parent;

    public STTSiteButton(STTSite site, AIChatSettingsSTTSiteScreen parent, int x, int y, int width) {
        super(x, y, width, 24, Component.empty(), b -> {
        }, DEFAULT_NARRATION);
        this.site = site;
        this.parent = parent;

        String nameKey = this.site.getNameKey();
        this.setMessage(Component.literal(I18nUtil.getOrDefault(nameKey, this.site.id())));
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xbf_090909, 0xbf_090909);
        if (this.isHoveredOrFocused()) {
            graphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x2f_F3EFE0, 0x2f_F3EFE0);
        }

        this.renderString(graphics, Minecraft.getInstance().font, 0xF3EFE0);

        // 站点图标（左侧）
        GuiTools.guiBlit(graphics, this.site.icon(), this.getX() + 6, this.getY() + 4, 0, 0, 16, 16, 16, 16);
        // 启用按钮（右侧）
        GuiTools.guiBlit(graphics, MISC, this.getX() + this.width - 46, this.getY() + 4, this.site.enabled() ? 16 : 0, 0, 16, 16);
        // 编辑按钮（最右侧）
        GuiTools.guiBlit(graphics, MISC, this.getX() + this.width - 24, this.getY() + 4, 16, 16, 16, 16);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        int right = this.getX() + this.width;

        // 启用按钮
        if (right - 50 <= mouseX && mouseX <= right - 26) {
            this.parent.toggleSTTSite(this.site.id());
            return;
        }

        // 编辑按钮
        if (right - 28 <= mouseX && mouseX <= right - 4) {
            this.parent.openSTTSiteEditor(this.site.id());
        }
    }

    public void renderString(GuiGraphicsExtractor graphics, Font font, int color) {
        graphics.text(font, this.getMessage(), this.getX() + 28, this.getY() + (this.height - 8) / 2,
                this.site.enabled() ? 0xFF999999 : 0xFF444444, false);
    }
}
