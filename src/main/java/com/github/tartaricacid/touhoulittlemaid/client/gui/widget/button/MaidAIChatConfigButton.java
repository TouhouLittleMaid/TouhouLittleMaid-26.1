package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

public class MaidAIChatConfigButton extends Button {
    private static final Identifier ICON = IdentifierUtil.modLoc("textures/gui/maid_ai_chat_config.png");
    private final MaidAIChatConfigButton.OnPress leftPress;
    private final MaidAIChatConfigButton.OnPress rightPress;
    private boolean leftClicked = false;
    private Component value;

    public MaidAIChatConfigButton(int x, int y, Component title, Component value, MaidAIChatConfigButton.OnPress onLeftPressIn, MaidAIChatConfigButton.OnPress onRightPressIn) {
        super(Button.builder(title, b -> {
        }).pos(x, y).size(164, 13));
        this.leftPress = onLeftPressIn;
        this.rightPress = onRightPressIn;
        this.value = value;
    }

    public MaidAIChatConfigButton(int x, int y, Component title, Component value, MaidAIChatConfigButton.OnPress onPress) {
        this(x, y, title, value, onPress, onPress);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (this.isHovered) {
            GuiTools.blit(graphics, ICON, this.getX(), this.getY(), this.width, this.height, 6, 150, this.width, this.height, 256, 256);
        } else {
            GuiTools.blit(graphics, ICON, this.getX(), this.getY(), this.width, this.height, 6, 137, this.width, this.height, 256, 256);
        }
        drawButtonText(graphics, mc.font);
    }

    public void setValue(Component value) {
        this.value = value;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (!this.active || !this.visible) {
            return;
        }
        double mouseX = event.x();
        double mouseY = event.y();
        boolean leftClickX = (this.getX() + 62) <= mouseX && mouseX <= (this.getX() + 72);
        boolean rightClickX = (this.getX() + 154) <= mouseX && mouseX <= (this.getX() + 164);
        boolean clickY = this.getY() <= mouseY && mouseY <= (this.getY() + this.getHeight());
        if (leftClickX && clickY) {
            leftClicked = true;
        } else if (rightClickX && clickY) {
            leftClicked = false;
        }
        super.onClick(event, doubleClick);
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (leftClicked) {
            leftPress.onPress(this);
        } else {
            rightPress.onPress(this);
        }
    }

    public void drawButtonText(GuiGraphicsExtractor graphics, Font font) {
        float scale = 0.75f;

        FormattedCharSequence leftText = this.getMessage().getVisualOrderText();
        FormattedCharSequence rightText = this.value.getVisualOrderText();

        float leftTextX = (this.getX() + 5) / scale;
        float leftTextY = (this.getY() + 4) / scale;
        float rightTextX = (this.getX() + 113 - font.width(rightText) * scale / 2f) / scale;
        float rightTextY = (this.getY() + 4) / scale;

        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.text(font, leftText, (int) leftTextX, (int) leftTextY, 0x444444, false);
        graphics.text(font, rightText, (int) rightTextX, (int) rightTextY, 0x55ff55, false);
        graphics.pose().popMatrix();
    }

    public interface OnPress {
        void onPress(MaidAIChatConfigButton button);
    }
}
