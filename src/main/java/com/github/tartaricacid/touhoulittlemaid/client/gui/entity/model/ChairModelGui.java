package com.github.tartaricacid.touhoulittlemaid.client.gui.entity.model;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.detail.ChairModelDetailsGui;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.network.message.ChairModelPackage;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

public class ChairModelGui extends AbstractModelGui<EntityChair, ChairModelInfo> {
    private static int PAGE_INDEX = 0;
    private static int PACK_INDEX = 0;
    private static int ROW_INDEX = 0;

    public ChairModelGui(EntityChair entity) {
        super(entity, CustomPackLoader.CHAIR_MODELS.getPackList());
    }

    @Override
    protected void drawLeftEntity(GuiGraphicsExtractor graphics, int middleX, int middleY, float mouseX, float mouseY) {
        float renderItemScale = CustomPackLoader.CHAIR_MODELS.getModelRenderItemScale(entity.getModelId());
        int centerX = (middleX - 256 / 2) / 2;
        int centerY = middleY + 80;
        InventoryScreen.extractEntityInInventoryFollowsMouse(
                graphics,
                centerX - 68,
                centerY - 100,
                centerX + 68,
                centerY + 80,
                (int) (45 * renderItemScale),
                0F,
                centerX + 25,
                centerY + 5,
                entity);
    }

    @Override
    protected void drawRightEntity(GuiGraphicsExtractor graphics, int posX, int posY, ChairModelInfo modelItem) {
        drawEntity(graphics, posX, posY, modelItem);
    }

    @Override
    protected void openDetailsGui(EntityChair entity, ChairModelInfo modelInfo) {
        if (minecraft != null) {
            minecraft.setScreen(new ChairModelDetailsGui(entity, modelInfo));
        }
    }

    @Override
    protected void notifyModelChange(EntityChair entity, ChairModelInfo modelInfo) {
        ClientPacketDistributor.sendToServer(new ChairModelPackage(entity.getId(), modelInfo.getModelId(), modelInfo.getMountedYOffset(),
                modelInfo.isTameableCanRide(), modelInfo.isNoGravity()));
    }

    @Override
    protected void addModelCustomTips(ChairModelInfo modelItem, List<Component> tooltips) {
    }

    @Override
    protected int getPageIndex() {
        return PAGE_INDEX;
    }

    @Override
    protected void setPageIndex(int pageIndex) {
        PAGE_INDEX = pageIndex;
    }

    @Override
    protected int getPackIndex() {
        return PACK_INDEX;
    }

    @Override
    protected void setPackIndex(int packIndex) {
        PACK_INDEX = packIndex;
    }

    @Override
    protected int getRowIndex() {
        return ROW_INDEX;
    }

    @Override
    protected void setRowIndex(int rowIndex) {
        ROW_INDEX = rowIndex;
    }

    private void drawEntity(GuiGraphicsExtractor graphics, int posX, int posY, ChairModelInfo modelItem) {
        Level world = getMinecraft().level;
        if (world == null) {
            return;
        }

        EntityChair chair = EntityCacheUtil.getChair(world, EntitySpawnReason.COMMAND);

        chair.setModelId(modelItem.getModelId().toString());
        InventoryScreen.extractEntityInInventoryFollowsMouse(
                graphics,
                posX - 18,
                posY - 30,
                posX + 18,
                posY + 18,
                (int) (12 * modelItem.getRenderItemScale()),
                0F,
                posX + 25,
                posY + 5,
                chair);
    }
}
