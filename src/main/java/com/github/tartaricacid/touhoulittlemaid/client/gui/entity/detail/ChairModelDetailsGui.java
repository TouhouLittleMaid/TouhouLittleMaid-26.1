package com.github.tartaricacid.touhoulittlemaid.client.gui.entity.detail;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.model.ChairModelGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.ModelDetailsButton;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ChairModelDetailsGui extends AbstractModelDetailsGui<EntityChair, ChairModelInfo> {
    private EntityMaid maid;
    private boolean showPassenger = false;

    public ChairModelDetailsGui(EntityChair sourceEntity, ChairModelInfo modelInfo) {
        super(sourceEntity, InitEntities.CHAIR.get().create(sourceEntity.level(), EntitySpawnReason.COMMAND), modelInfo);
        guiEntity.setModelId(modelInfo.getModelId().toString());
        if (Minecraft.getInstance().level != null) {
            this.maid = InitEntities.MAID.get().create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
            if (this.maid != null) {
                this.maid.components().profile.setModelId("authors_and_credits:wine_fox_maid");
            }
        }
    }

    @Override
    protected void applyReturnButtonLogic() {
        Minecraft.getInstance().setScreen(new ChairModelGui(sourceEntity));
    }

    @Override
    protected void initSideButton() {
        ModelDetailsButton showPassengerButton = new ModelDetailsButton(2, 17, "gui.touhou_little_maid.skin_details.show_passenger",
                this::applyShowPassengerLogic);
        this.addRenderableWidget(showPassengerButton);
    }

    @Override
    public void tick() {
        guiEntity.tickCount++;
        if (maid != null) {
            maid.tickCount++;
        }
    }

    @Override
    protected void renderExtraEntity(GuiGraphicsExtractor graphics, float scale, Quaternionf rot, Quaternionf xRot, int x0, int y0, int x1, int y1) {
        if (showPassenger && maid != null) {
            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<? super LivingEntity, ?> renderer = dispatcher.getRenderer(maid);
            EntityRenderState renderState = renderer.createRenderState(maid, 1.0F);
            renderState.shadowPieces.clear();
            renderState.outlineColor = 0;
            float yOffset = -0.375F + modelInfo.getMountedYOffset();
            Vector3f translation = new Vector3f(posX, posY + renderState.boundingBoxHeight / 2.0F + yOffset, 0);
            graphics.entity(renderState, scale, translation, rot, xRot, x0, y0, x1, y1);
        }
    }

    private void applyShowPassengerLogic(boolean isStateTriggered) {
        this.showPassenger = isStateTriggered;
        if (isStateTriggered && maid != null) {
            maid.startRiding(guiEntity, true, false);
        } else {
            guiEntity.ejectPassengers();
        }
    }
}
