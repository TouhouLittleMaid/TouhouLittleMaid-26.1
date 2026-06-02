package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.List;

public class HistoryChatWidget extends AbstractWidget {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/gui/maid_history_chat.png");
    private static final long TICKS_PER_DAY = 24000;
    private static final long TICKS_PER_HOUR = 1000;

    /**
     * 普通的 LLM 返回的聊天消息
     */
    private final boolean isLeft;
    /**
     * 工具调用等类似于系统消息的内容
     */
    private final boolean isTool;

    private final ResolvableProfile playerProfile;
    private final Component time;

    public HistoryChatWidget(int pX, int pY, int width, int height, Component message, ResolvableProfile playerProfile,
                             long gameTime, boolean isLeft, boolean isTool) {
        super(pX, pY, width, height, message);
        this.isLeft = isLeft;
        this.isTool = isTool;
        this.playerProfile = playerProfile;
        this.time = convertGameTime(gameTime);
    }

    private Component convertGameTime(long inputGameTime) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return Component.empty();
        }
        long currentGameTime = level.getGameTime();
        long diff = currentGameTime - inputGameTime;
        if (diff < 0) {
            return Component.empty();
        }

        long days = diff / TICKS_PER_DAY;
        diff %= TICKS_PER_DAY;
        long hours = diff / TICKS_PER_HOUR;
        if (days > 0) {
            return Component.translatable("gui.touhou_little_maid.button.maid_ai_chat_config.history_chat.days", days);
        } else if (hours > 0) {
            return Component.translatable("gui.touhou_little_maid.button.maid_ai_chat_config.history_chat.hours", hours);
        } else {
            return Component.translatable("gui.touhou_little_maid.button.maid_ai_chat_config.history_chat.just_now");
        }
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isTool) {
            this.renderToolText(graphics, Minecraft.getInstance().font);
        } else {
            this.drawBackground(graphics);
            this.drawAvatar(graphics);
            this.renderString(graphics, Minecraft.getInstance().font);
        }
    }

    private void renderToolText(GuiGraphicsExtractor graphics, Font font) {
        float scale = 0.5f;
        int width = (int) (this.getWidth() / scale);
        float posX = this.getX() / scale + width / 2f;
        float posY = this.getY() / scale;

        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);

        List<FormattedCharSequence> lines = font.split(this.getMessage(), width);

        for (int i = 0; i < lines.size(); i++) {
            graphics.centeredText(font, lines.get(i), (int) posX, (int) posY + i * font.lineHeight, 0xFF999999);
        }

        graphics.pose().popMatrix();
    }

    private void drawAvatar(GuiGraphicsExtractor graphics) {
        int size = 16;
        int offset = 6;
        int xOffset = this.isLeft ? (-size - offset) : this.getWidth() + offset;
        if (isLeft) {
            GuiTools.guiBlit(graphics, TEXTURE, this.getX() + xOffset, this.getHeightMiddle(size), 0, 16, size, size,128,128);
        } else {
            PlayerSkin playerSkin = Minecraft.getInstance().playerSkinRenderCache().getOrDefault(this.playerProfile).playerSkin();
            PlayerFaceExtractor.extractRenderState(graphics, playerSkin,this.getX() + xOffset, this.getHeightMiddle(size), size, 0xFFFFFFFF);
        }
    }

    private void drawBackground(GuiGraphicsExtractor graphics) {
        int heightMiddle = this.getHeightMiddle(14);
        GuiTools.blitNineSliced(graphics, TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                8, 4, 100, 16, 0, this.getTextureY());
        if (isLeft) {
            GuiTools.guiBlit(graphics, TEXTURE, this.getX() - 4, heightMiddle, 100, 16, 6, 14);
        } else {
            GuiTools.guiBlit(graphics, TEXTURE, this.getX() + this.getWidth() - 2, heightMiddle, 100, 0, 6, 14);
        }
    }

    public void renderString(GuiGraphicsExtractor graphics, Font font) {
        Component message = this.getMessage();
        List<FormattedCharSequence> lines = font.split(message, this.getWidth() - 10);
        int color = isLeft ? 0xFF555555 : 0xFFFFFFFF;
        for (int i = 0; i < lines.size(); i++) {
            graphics.text(font, lines.get(i), this.getX() + 5, this.getY() + 5 + i * font.lineHeight, color, false);
        }

        float scale = 0.5f;
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        if (isLeft) {
            graphics.text(font, this.time.getVisualOrderText(),
                    (int) ((this.getX() + 2) / scale),
                    (int) ((this.getY() - 5) / scale),
                    0xFF999999, false);
        } else {
            float width = font.width(this.time) * scale;
            graphics.text(font, this.time.getVisualOrderText(),
                    (int) ((this.getX() + this.getWidth() - width - 2) / scale),
                    (int) ((this.getY() - 5) / scale),
                    0xFF999999, false);
        }
        graphics.pose().popMatrix();
    }

    private int getTextureY() {
        return this.isLeft ? 16 : 0;
    }

    private int getHeightMiddle(int height) {
        return this.getY() + (this.getHeight() - height) / 2;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
