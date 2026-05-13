package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.ai;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.ai.service.tts.TTSSite;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.settings.AIChatSettingsTTSSiteScreen;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.SaveTTSSitePacket;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;

public class TTSSiteButton extends Button {
    private static final Identifier MISC = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/ai_chat/misc.png");

    private final TTSSite site;
    private final AIChatSettingsTTSSiteScreen parent;

    public TTSSiteButton(TTSSite site, AIChatSettingsTTSSiteScreen parent, int x, int y, int width) {
        super(x, y, width, 24, Component.empty(), b -> {
        }, DEFAULT_NARRATION);
        this.site = site;
        this.parent = parent;

        String nameKey = this.site.getNameKey();
        if (I18n.exists(nameKey)) {
            this.setMessage(Component.literal(I18n.get(nameKey)));
        } else {
            this.setMessage(Component.literal(this.site.id()));
        }
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xbf_090909, 0xbf_090909);
        if (this.isHoveredOrFocused()) {
            graphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x2f_F3EFE0, 0x2f_F3EFE0);
        }

        this.renderString(graphics, Minecraft.getInstance().font, 0xF3EFE0);

        // 站点图标（左侧）
        GuiTools.blit(graphics,this.site.icon(), this.getX() + 6, this.getY() + 4, 0, 0, 16, 16, 16, 16);
        // 启用按钮（右侧）
        GuiTools.blit(graphics,MISC, this.getX() + this.width - 46, this.getY() + 4, this.site.enabled() ? 16 : 0, 0, 16, 16);
        // 编辑按钮（最右侧）
        GuiTools.blit(graphics,MISC, this.getX() + this.width - 24, this.getY() + 4, 16, 16, 16, 16);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        int right = this.getX() + this.width;

        // 启用按钮
        if (right - 50 <= mouseX && mouseX <= right - 26) {
            ClientPacketDistributor.sendToServer(SaveTTSSitePacket.toggle(this.site.id(), !this.site.enabled()));
            return;
        }

        // 编辑按钮
        if (right - 28 <= mouseX && mouseX <= right - 4) {
            this.parent.openTTSSiteEditor(this.site.id());
        }
    }

    public void renderString(GuiGraphicsExtractor graphics, Font font, int color) {
        graphics.text(font, this.getMessage(), this.getX() + 28, this.getY() + (this.height - 8) / 2,
                this.site.enabled() ? 0xFF999999 : 0xFF444444, false);
    }
}
