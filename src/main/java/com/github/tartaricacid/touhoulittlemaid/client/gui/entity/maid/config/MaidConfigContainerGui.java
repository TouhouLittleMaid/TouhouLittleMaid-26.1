package com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.config;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.MaidConfigButton;
import com.github.tartaricacid.touhoulittlemaid.entity.data.ConfigData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.PickType;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.config.MaidConfigContainer;
import com.github.tartaricacid.touhoulittlemaid.network.message.MaidSubConfigPackage;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.anti_ad.mc.ipn.api.IPNButton;
import org.anti_ad.mc.ipn.api.IPNGuiHint;
import org.anti_ad.mc.ipn.api.IPNPlayerSideOnly;
import org.jspecify.annotations.NonNull;

@IPNPlayerSideOnly
@IPNGuiHint(button = IPNButton.SORT, horizontalOffset = -36, bottom = -12)
@IPNGuiHint(button = IPNButton.SORT_COLUMNS, horizontalOffset = -24, bottom = -24)
@IPNGuiHint(button = IPNButton.SORT_ROWS, horizontalOffset = -12, bottom = -36)
@IPNGuiHint(button = IPNButton.SHOW_EDITOR, horizontalOffset = -5)
@IPNGuiHint(button = IPNButton.SETTINGS, horizontalOffset = -5)
public class MaidConfigContainerGui extends AbstractMaidContainerGui<MaidConfigContainer> {
    private static final Identifier ICON = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_config.png");
    private ConfigData configData;

    public MaidConfigContainerGui(MaidConfigContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.configData = getMaid().getData(InitDataAttachment.CONFIG);
    }

    @Override
    public void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        GuiTools.guiBlit(graphics, ICON, leftPos + 80, topPos + 28, 0, 0, imageWidth, imageHeight);
        super.extractContents(graphics, mouseX, mouseY, a);
    }

    private void syncConfigData(ConfigData newData) {
        this.configData = newData;
        ClientPacketDistributor.sendToServer(new MaidSubConfigPackage(this.maid.getId(), this.configData));
    }

    @Override
    protected void initAdditionWidgets() {
        int buttonLeft = leftPos + 86;
        int buttonTop = topPos + 52;

        this.addRenderableWidget(new MaidConfigButton(buttonLeft, buttonTop,
                Component.translatable("gui.touhou_little_maid.maid_config.show_backpack"),
                Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isShowBackpack()),
                button -> {
                    this.syncConfigData(this.configData.setShowBackpack(!this.configData.isShowBackpack()));
                    button.setValue(Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isShowBackpack()));
                }
        ));
        buttonTop += 13;

        this.addRenderableWidget(new MaidConfigButton(buttonLeft, buttonTop,
                Component.translatable("gui.touhou_little_maid.maid_config.show_back_item"),
                Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isShowBackItem()),
                button -> {
                    this.syncConfigData(this.configData.setShowBackItem(!this.configData.isShowBackItem()));
                    button.setValue(Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isShowBackItem()));
                }
        ));
        buttonTop += 13;

        this.addRenderableWidget(new MaidConfigButton(buttonLeft, buttonTop,
                Component.translatable("gui.touhou_little_maid.maid_config.show_chat_bubble"),
                Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isChatBubbleShow()),
                button -> {
                    this.syncConfigData(this.configData.setChatBubbleShow(!this.configData.isChatBubbleShow()));
                    button.setValue(Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isChatBubbleShow()));
                }
        ));
        buttonTop += 13;


        this.addRenderableWidget(new MaidConfigButton(buttonLeft, buttonTop,
                Component.translatable("gui.touhou_little_maid.maid_config.sound_frequency"),
                Component.literal(Math.round(this.configData.soundFreq() * 100) + "%").withStyle(ChatFormatting.YELLOW),
                button -> {
                    this.syncConfigData(this.configData.setSoundFreq(this.configData.soundFreq() - 0.1f));
                    button.setValue(Component.literal(Math.round(this.configData.soundFreq() * 100) + "%").withStyle(ChatFormatting.YELLOW));
                },
                button -> {
                    this.syncConfigData(this.configData.setSoundFreq(this.configData.soundFreq() + 0.1f));
                    button.setValue(Component.literal(Math.round(this.configData.soundFreq() * 100) + "%").withStyle(ChatFormatting.YELLOW));
                }
        ));
        buttonTop += 13;

        this.addRenderableWidget(new MaidConfigButton(buttonLeft, buttonTop,
                Component.translatable("gui.touhou_little_maid.maid_config.pick_type"),
                Component.translatable(PickType.getTransKey(this.configData.getPickupType())).withStyle(ChatFormatting.DARK_RED),
                button -> {
                    this.syncConfigData(this.configData.setPickupType(PickType.getPreviousPickType(this.configData.getPickupType())));
                    button.setValue(Component.translatable(PickType.getTransKey(this.configData.getPickupType())).withStyle(ChatFormatting.DARK_RED));
                },
                button -> {
                    this.syncConfigData(this.configData.setPickupType(PickType.getNextPickType(this.configData.getPickupType())));
                    button.setValue(Component.translatable(PickType.getTransKey(this.configData.getPickupType())).withStyle(ChatFormatting.DARK_RED));
                }
        ));
        buttonTop += 13;

        this.addRenderableWidget(new MaidConfigButton(buttonLeft, buttonTop,
                Component.translatable("gui.touhou_little_maid.maid_config.open_door"),
                Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isOpenDoor()),
                button -> {
                    this.syncConfigData(this.configData.setOpenDoor(!this.configData.isOpenDoor()));
                    button.setValue(Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isOpenDoor()));
                }
        ));
        buttonTop += 13;

        this.addRenderableWidget(new MaidConfigButton(buttonLeft, buttonTop,
                Component.translatable("gui.touhou_little_maid.maid_config.open_fence_gate"),
                Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isOpenFenceGate()),
                button -> {
                    this.syncConfigData(this.configData.setOpenFenceGate(!this.configData.isOpenFenceGate()));
                    button.setValue(Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isOpenFenceGate()));
                }
        ));
        buttonTop += 13;

        this.addRenderableWidget(new MaidConfigButton(buttonLeft, buttonTop,
                Component.translatable("gui.touhou_little_maid.maid_config.active_climbing"),
                Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isActiveClimbing()),
                button -> {
                    this.syncConfigData(this.configData.setActiveClimbing(!this.configData.isActiveClimbing()));
                    button.setValue(Component.translatable("gui.touhou_little_maid.maid_config.value." + this.configData.isActiveClimbing()));
                }
        ));
    }

    @Override
    protected void renderAddition(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.centeredText(font, Component.translatable("gui.touhou_little_maid.button.maid_config"), leftPos + 167, topPos + 41, 0xFFFFFF);
    }
}