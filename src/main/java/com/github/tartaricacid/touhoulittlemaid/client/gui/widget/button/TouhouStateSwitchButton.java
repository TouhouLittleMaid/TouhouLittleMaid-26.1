package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;

public class TouhouStateSwitchButton extends AbstractWidget {
    protected Identifier resourceLocation;
    protected boolean isStateTriggered;
    protected int xTexStart;
    protected int yTexStart;
    protected int xDiffTex;
    protected int yDiffTex;

    public TouhouStateSwitchButton(int pX, int pY, int pWidth, int pHeight, boolean pInitialState) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY);
        this.isStateTriggered = pInitialState;
    }

    public void initTextureValues(int pXTexStart, int pYTexStart, int pXDiffTex, int pYDiffTex, Identifier pResourceLocation) {
        this.xTexStart = pXTexStart;
        this.yTexStart = pYTexStart;
        this.xDiffTex = pXDiffTex;
        this.yDiffTex = pYDiffTex;
        this.resourceLocation = pResourceLocation;
    }

    public void setStateTriggered(boolean pTriggered) {
        this.isStateTriggered = pTriggered;
    }

    public boolean isStateTriggered() {
        return this.isStateTriggered;
    }

    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        this.defaultButtonNarrationText(pNarrationElementOutput);
    }

    public void extractWidgetRenderState(GuiGraphicsExtractor pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.disableDepthTest();
        int i = this.xTexStart;
        int j = this.yTexStart;
        if (this.isStateTriggered) {
            i += this.xDiffTex;
        }

        if (this.isHoveredOrFocused()) {
            j += this.yDiffTex;
        }

        pGuiGraphics.blit(this.resourceLocation, this.getX(), this.getY(), i, j, this.width, this.height);
        RenderSystem.enableDepthTest();
    }
}
