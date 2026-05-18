package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiGraphicsExtractor.HoveredTextEffects;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ButtonWithId extends Button {
    private final Consumer<Integer> onClick;
    private final int id;

    public ButtonWithId(int id, int x, int y, int width, int height, Component title, Consumer<Integer> onClick) {
        super(Button.builder(title, (b) -> {
        }).pos(x, y).size(width, height));
        this.id = id;
        this.onClick = onClick;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        super.onPress(input);
        this.onClick.accept(this.id);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.extractDefaultSprite(graphics);
        this.extractDefaultLabel(graphics.textRendererForWidget(this, HoveredTextEffects.NONE));
    }
}
