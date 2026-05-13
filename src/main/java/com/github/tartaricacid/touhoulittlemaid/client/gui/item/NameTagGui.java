package com.github.tartaricacid.touhoulittlemaid.client.gui.item;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.SendNameTagPackage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.StringUtils;

public class NameTagGui extends Screen {
    final Identifier CONFIRM_SPRITE = Identifier.withDefaultNamespace("container/beacon/confirm");
    final Identifier CANCEL_SPRITE = Identifier.withDefaultNamespace("container/beacon/cancel");
    private final EntityMaid maid;
    private EditBox textField;
    private Button alwaysShowButton;
    private boolean alwaysShow = false;

    public NameTagGui(EntityMaid maid) {
        super(Component.empty());
        this.maid = maid;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        int middleX = this.width / 2;
        int middleY = this.height / 2;
        textField = new EditBox(getMinecraft().font, middleX - 99, middleY - 26, 176, 20,
                Component.translatable("gui.touhou_little_maid.name_tag.edit_box"));
        this.addWidget(this.textField);
        this.setInitialFocus(this.textField);
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), this::sendDoneMessage)
                .pos(middleX - 100, middleY).size(98, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .pos(middleX + 2, middleY).size(98, 20).build());
        alwaysShowButton = Button.builder(Component.empty(), b -> alwaysShow = !alwaysShow)
                .pos(middleX + 80, middleY - 26).size(20, 20).build();
        addRenderableWidget(alwaysShowButton);
    }

    @Override
    public void resize(int width, int height) {
        String value = this.textField.getValue();
        super.resize(width, height);
        this.textField.setValue(value);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int middleX = this.width / 2;
        int middleY = this.height / 2;
        this.extractBackground(graphics, mouseX, mouseY, partialTicks);
        textField.render(graphics, mouseX, mouseY, partialTicks);
        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
        }
        if (!alwaysShow) {
            graphics.blitSprite(CANCEL_SPRITE, middleX + 82, middleY - 26, 18, 18);
        } else {
            graphics.blitSprite(CONFIRM_SPRITE, middleX + 82, middleY - 26, 18, 18);
        }
        if (alwaysShowButton.isHovered()) {
            graphics.renderTooltip(font, Component.translatable("gui.touhou_little_maid.tag.always_show"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.textField.mouseClicked(event, doubleClick)) {
            this.setFocused(this.textField);
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void insertText(String text, boolean overwrite) {
        if (overwrite) {
            this.textField.setValue(text);
        } else {
            this.textField.insertText(text);
        }
    }

    private void sendDoneMessage(Button button) {
        if (StringUtils.isNotBlank(textField.getValue())) {
            ClientPacketDistributor.sendToServer(new SendNameTagPackage(maid.getId(), textField.getValue(), alwaysShow));
        }
        this.onClose();
    }
}
