package com.github.tartaricacid.touhoulittlemaid.client.gui.entity.detail;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.model.MaidModelGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.ModelDetailsButton;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MaidModelDetailsGui extends AbstractModelDetailsGui<EntityMaid, MaidModelInfo> {
    private static final ItemStack MAIN_HAND_SWORD = Items.DIAMOND_SWORD.getDefaultInstance();
    private static final ItemStack OFF_HAND_SHIELD = Items.SHIELD.getDefaultInstance();
    private static final ItemStack ARMOR_ITEM = Items.GOLDEN_HELMET.getDefaultInstance();
    private EntityChair chair;
    private volatile boolean isEnableWalk = false;

    public MaidModelDetailsGui(EntityMaid sourceEntity, MaidModelInfo modelInfo) {
        super(sourceEntity, InitEntities.MAID.get().create(sourceEntity.level(), EntitySpawnReason.COMMAND), modelInfo);
        this.guiEntity.setModelId(modelInfo.getModelId().toString());
        this.guiEntity.setOnGround(true);
        this.guiEntity.yHeadRot = 0;
        this.guiEntity.yHeadRotO = 0;
        this.initChair();
    }

    private void initChair() {
        if (Minecraft.getInstance().level != null) {
            this.chair = InitEntities.CHAIR.get().create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
            if (this.chair != null) {
                this.chair.setModelId("touhou_little_maid:low_stool");
            }
        }
    }

    @Override
    protected void applyReturnButtonLogic() {
        ScreenUtil.setScreen(new MaidModelGui(sourceEntity));
    }

    @Override
    protected void initSideButton() {
        ModelDetailsButton begButton = new ModelDetailsButton(2, 17, "gui.touhou_little_maid.skin_details.beg",
                (isStateTriggered) -> guiEntity.setBegging(isStateTriggered));
        ModelDetailsButton walkButton = new ModelDetailsButton(2, 17 + 13, "gui.touhou_little_maid.skin_details.walk",
                (isStateTriggered) -> isEnableWalk = isStateTriggered);
        ModelDetailsButton sitButton = new ModelDetailsButton(2, 17 + 13 * 2, "gui.touhou_little_maid.skin_details.sit",
                (isStateTriggered) -> guiEntity.setInSittingPose(isStateTriggered));
        ModelDetailsButton rideButton = new ModelDetailsButton(2, 17 + 13 * 3, "gui.touhou_little_maid.skin_details.ride",
                this::applyRideButtonLogic);
        ModelDetailsButton helmetButton = new ModelDetailsButton(2, 17 + 13 * 4, "gui.touhou_little_maid.skin_details.helmet",
                (isStateTriggered) -> applyEquipmentButtonLogic(EquipmentSlot.HEAD, isStateTriggered));
        ModelDetailsButton chestPlateButton = new ModelDetailsButton(2, 17 + 13 * 5, "gui.touhou_little_maid.skin_details.chest_plate",
                (isStateTriggered) -> applyEquipmentButtonLogic(EquipmentSlot.CHEST, isStateTriggered));
        ModelDetailsButton leggingsButton = new ModelDetailsButton(2, 17 + 13 * 6, "gui.touhou_little_maid.skin_details.leggings",
                (isStateTriggered) -> applyEquipmentButtonLogic(EquipmentSlot.LEGS, isStateTriggered));
        ModelDetailsButton bootsButton = new ModelDetailsButton(2, 17 + 13 * 7, "gui.touhou_little_maid.skin_details.boots",
                (isStateTriggered) -> applyEquipmentButtonLogic(EquipmentSlot.FEET, isStateTriggered));
        ModelDetailsButton mainHandButton = new ModelDetailsButton(2, 17 + 13 * 8, "gui.touhou_little_maid.skin_details.main_hand",
                (isStateTriggered) -> applyEquipmentButtonLogic(EquipmentSlot.MAINHAND, isStateTriggered));
        ModelDetailsButton offHandButton = new ModelDetailsButton(2, 17 + 13 * 9, "gui.touhou_little_maid.skin_details.off_hand",
                (isStateTriggered) -> applyEquipmentButtonLogic(EquipmentSlot.OFFHAND, isStateTriggered));
        this.addRenderableWidget(begButton);
        this.addRenderableWidget(walkButton);
        this.addRenderableWidget(sitButton);
        this.addRenderableWidget(rideButton);
        this.addRenderableWidget(helmetButton);
        this.addRenderableWidget(chestPlateButton);
        this.addRenderableWidget(leggingsButton);
        this.addRenderableWidget(bootsButton);
        this.addRenderableWidget(mainHandButton);
        this.addRenderableWidget(offHandButton);
    }

    @Override
    public void tick() {
        // Tick count increment for some animations
        guiEntity.tickCount++;
        // For entity walk
        // Update walk speed
        float speed = isEnableWalk ? 0.5f : 0;
        //TODO PositionScale值确认
        guiEntity.walkAnimation.update(speed, 0.4f, 1.0F);
    }

    @Override
    protected void renderExtraEntity(GuiGraphicsExtractor graphics, float scale, Quaternionf rot, Quaternionf xRot, int x0, int y0, int x1, int y1) {
        if (guiEntity.isPassenger() && chair != null) {
            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<? super LivingEntity, ?> renderer = dispatcher.getRenderer(chair);
            EntityRenderState renderState = renderer.createRenderState(chair, 1.0F);
            renderState.shadowPieces.clear();
            renderState.outlineColor = 0;
            Vector3f translation = new Vector3f(posX, posY + renderState.boundingBoxHeight / 2.0F - 0.95F, 0);
            graphics.entity(renderState, scale, translation, rot, xRot, x0, y0, x1, y1);
        }
    }

    private void applyRideButtonLogic(boolean isStateTriggered) {
        if (isStateTriggered && chair != null) {
            guiEntity.startRiding(chair, true, false);
        } else {
            guiEntity.removeVehicle();
        }
    }

    private void applyEquipmentButtonLogic(EquipmentSlot slot, boolean isStateTriggered) {
        if (isStateTriggered) {
            if (slot == EquipmentSlot.MAINHAND) {
                guiEntity.setItemSlot(slot, MAIN_HAND_SWORD);
            } else if (slot == EquipmentSlot.OFFHAND) {
                guiEntity.setItemSlot(slot, OFF_HAND_SHIELD);
            } else {
                guiEntity.setItemSlot(slot, ARMOR_ITEM);
            }
        } else {
            guiEntity.setItemSlot(slot, ItemStack.EMPTY);
        }
    }
}
