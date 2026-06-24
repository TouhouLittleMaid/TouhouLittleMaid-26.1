package com.github.tartaricacid.touhoulittlemaid.client.gui.item;

import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.TouhouImageButton;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.WirelessIOButton;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.other.WirelessIOContainer;
import com.github.tartaricacid.touhoulittlemaid.item.ItemWirelessIO;
import com.github.tartaricacid.touhoulittlemaid.network.message.WirelessIOGuiPackage;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.ScreenUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.anti_ad.mc.ipn.api.IPNIgnore;

@IPNIgnore
public class WirelessIOContainerGui extends AbstractContainerScreen<WirelessIOContainer> {
    private static final Identifier MAIN = IdentifierUtil.modLoc("textures/gui/wireless_io.png");
    private boolean isMaidToChest;
    private boolean isBlacklist;

    public WirelessIOContainerGui(WirelessIOContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.isMaidToChest = ItemWirelessIO.isMaidToChest(container.getStack());
        this.isBlacklist = ItemWirelessIO.isBlacklist(container.getStack());
    }

    @Override
    protected void init() {
        super.init();

        WirelessIOButton ioModeToggle = new WirelessIOButton(leftPos + 23, topPos + 34, 18, 18, isMaidToChest,
                (x, y) -> {
                    isMaidToChest = !isMaidToChest;
                    ClientPacketDistributor.sendToServer(new WirelessIOGuiPackage(isMaidToChest, isBlacklist));
                }, (m, x, y) -> m.setTooltipForNextFrame(font, Component.translatable("gui.touhou_little_maid.wireless_io.io_mode"), x, y));

        ioModeToggle.initTextureValues(194, 32, -18, 18, MAIN);
        WirelessIOButton filterModeToggle = new WirelessIOButton(leftPos + 136, topPos + 26, 16, 16, isBlacklist,
                (x, y) -> {
                    isBlacklist = !isBlacklist;
                    ClientPacketDistributor.sendToServer(new WirelessIOGuiPackage(isMaidToChest, isBlacklist));
                }, (m, x, y) -> m.setTooltipForNextFrame(font, Component.translatable("gui.touhou_little_maid.wireless_io.filter_mode"), x, y));
        filterModeToggle.initTextureValues(176, 0, 16, 16, MAIN);

        TouhouImageButton configButton = new TouhouImageButton(leftPos + 136, topPos + 44, 16, 16, 208, 0, 16,
                MAIN, 256, 256, buttons -> ScreenUtil.setScreen(new WirelessIOConfigSlotGui(menu.getStack())));
        configButton.setTooltip(Tooltip.create(Component.translatable("gui.touhou_little_maid.wireless_io.config_slot")));

        addRenderableWidget(filterModeToggle);
        addRenderableWidget(configButton);
        addRenderableWidget(ioModeToggle);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        GuiTools.guiBlit(graphics, MAIN, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (isBlacklist) {
            GuiTools.guiBlit(graphics, MAIN, leftPos + 61, topPos + 15, 0, 166, 54, 55);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int x, int y) {
    }
}
