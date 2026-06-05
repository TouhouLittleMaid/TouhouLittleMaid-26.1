package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.network.message.SetBeaconPotionPackage;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityMaidBeacon;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.function.Consumer;

public class BeaconEffectButton extends TouhouStateSwitchButton {
    private static final Identifier BG = IdentifierUtil.modLoc("textures/gui/maid_beacon.png");
    private final Component tooltips;
    private final int potionIndex;
    private final BlockPos pos;
    private final Consumer<Boolean> onClick;
    private Identifier sprite;

    public BeaconEffectButton(BlockEntityMaidBeacon.BeaconEffect effect, int xIn, int yIn, int potionIndex, BlockEntityMaidBeacon beacon, Consumer<Boolean> onClick) {
        super(xIn, yIn, 22, 22, potionIndex == effect.ordinal());
        this.initTextureValues(0, 111, 22, 22, BG);
        Holder<MobEffect> effectHolder = effect.getEffect();
        this.tooltips = effectHolder.value().getDisplayName();
        this.potionIndex = effect.ordinal();
        this.pos = beacon.getBlockPos();
        this.onClick = onClick;
        this.sprite = Gui.getMobEffectSprite(effectHolder);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.isStateTriggered = !this.isStateTriggered;
        ClientPacketDistributor.sendToServer(new SetBeaconPotionPackage(pos, isStateTriggered ? potionIndex : -1));
        this.onClick.accept(this.isStateTriggered);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
    }

    public void renderToolTip(GuiGraphicsExtractor graphics, Screen screen, int pMouseX, int pMouseY) {
        if (this.isHovered) {
            graphics.setTooltipForNextFrame(screen.getMinecraft().font, tooltips, pMouseX, pMouseY);
        }
    }
}