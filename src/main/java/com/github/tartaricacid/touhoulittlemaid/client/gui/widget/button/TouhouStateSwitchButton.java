package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
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

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        this.defaultButtonNarrationText(pNarrationElementOutput);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = this.xTexStart;
        int j = this.yTexStart;
        if (this.isStateTriggered) {
            i += this.xDiffTex;
        }

        if (this.isHoveredOrFocused()) {
            j += this.yDiffTex;
        }

        GuiTools.blit(pGuiGraphics, this.resourceLocation, this.getX(), this.getY(), this.width, this.height, i, j);
    }
}
