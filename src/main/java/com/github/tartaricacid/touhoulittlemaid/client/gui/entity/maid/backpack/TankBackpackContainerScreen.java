package com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.backpack;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.BaubleButton;
import com.github.tartaricacid.touhoulittlemaid.compat.curios.CuriosCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.data.TankBackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.backpack.TankBackpackContainer;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import com.github.tartaricacid.touhoulittlemaid.util.MaidFluidRender;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.anti_ad.mc.ipn.api.IPNButton;
import org.anti_ad.mc.ipn.api.IPNGuiHint;
import org.anti_ad.mc.ipn.api.IPNPlayerSideOnly;

@IPNPlayerSideOnly
@IPNGuiHint(button = IPNButton.SORT, horizontalOffset = -36, bottom = -12)
@IPNGuiHint(button = IPNButton.SORT_COLUMNS, horizontalOffset = -24, bottom = -24)
@IPNGuiHint(button = IPNButton.SORT_ROWS, horizontalOffset = -12, bottom = -36)
@IPNGuiHint(button = IPNButton.SHOW_EDITOR, horizontalOffset = -5)
@IPNGuiHint(button = IPNButton.SETTINGS, horizontalOffset = -5)
public class TankBackpackContainerScreen extends AbstractMaidContainerGui<TankBackpackContainer> implements IBackpackContainerScreen {
    private static final Identifier BACKPACK = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/maid_tank.png");
    private final EntityMaid maid;

    public TankBackpackContainerScreen(TankBackpackContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn, 256, 256);
        this.maid = menu.getMaid();
    }

    @Override
    protected void initAdditionWidgets() {
        BaubleButton button = this.getBaubleButton(maid, leftPos, topPos);
        this.addRenderableWidget(button);

        // 添加 curios 兼容按钮
        if (CuriosCompat.isLoadedOrEnable()) {
            this.addRenderableWidget(this.getCuriosButton(maid, leftPos, topPos));
        }
    }

    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pPartialTick) {
        super.extractBackground(graphics, mouseX, mouseY, pPartialTick);
        GuiTools.blit(graphics, BACKPACK, leftPos + 85, topPos + 36, 0, 0, 165, 128);

        MaidFluidRender.drawFluid(graphics, leftPos + 200, topPos + 108, 29, 50, maid.getBackpackFluid(), this.menu.getFluidCount(), TankBackpackData.CAPACITY);
        GuiTools.blit(graphics, BACKPACK, leftPos + 197, topPos + 104, 165, 0, 34, 50);

        boolean xInRange = leftPos + 196 <= mouseX && mouseX <= leftPos + 196 + 29;
        boolean yInRange = topPos + 108 <= mouseY && mouseY <= topPos + 108 + 50;
        if (xInRange && yInRange) {
            MutableComponent fluidInfo = Component.translatable("tooltips.touhou_little_maid.tank_backpack.fluid",
                    MaidFluidRender.getFluidName(maid.getBackpackFluid(), this.menu.getFluidCount()),
                    this.menu.getFluidCount()).withStyle(ChatFormatting.GRAY);
            MutableComponent capacityInfo = Component.translatable("tooltips.touhou_little_maid.tank_backpack.capacity", TankBackpackData.CAPACITY)
                    .withStyle(ChatFormatting.GRAY);
            graphics.text(font, FormattedCharSequence.fromList(Lists.newArrayList(
                    fluidInfo.getVisualOrderText(),
                    capacityInfo.getVisualOrderText()
            )), mouseX, mouseY, 0xffffffff);
        }
    }
}
