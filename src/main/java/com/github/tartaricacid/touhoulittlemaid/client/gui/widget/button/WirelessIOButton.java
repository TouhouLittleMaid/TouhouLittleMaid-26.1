package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.BiConsumer;

public class WirelessIOButton extends TouhouStateSwitchButton {
    protected final WirelessIOButton.ITooltip onTooltip;
    private final BiConsumer<Double, Double> onClick;

    public WirelessIOButton(int xIn, int yIn, int widthIn, int heightIn, boolean triggered, BiConsumer<Double, Double> onClick, WirelessIOButton.ITooltip onTooltip) {
        super(xIn, yIn, widthIn, heightIn, triggered);
        this.onClick = onClick;
        this.onTooltip = onTooltip;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.isStateTriggered = !this.isStateTriggered;
        onClick.accept(event.x(), event.y());
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTicks);
        if (this.isHovered()) {
            this.onTooltip.onTooltip(graphics, mouseX, mouseY);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface ITooltip {
        void onTooltip(GuiGraphicsExtractor graphics, int x, int y);
    }
}
