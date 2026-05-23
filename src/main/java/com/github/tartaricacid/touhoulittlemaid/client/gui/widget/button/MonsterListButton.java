package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.task.AttackTaskConfigGui;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MonsterListButton extends Button {
    private static final Identifier ICON = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/attack_task_config.png");
    private final AttackTaskConfigGui parents;
    private final Identifier entityId;

    public MonsterListButton(Component entityName, int x, int y, Identifier entityId, AttackTaskConfigGui parents) {
        super(Button.builder(entityName, b -> {
        }).pos(x, y).size(164, 13));
        this.parents = parents;
        this.entityId = entityId;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pPartialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (deleteClick(mouseX, mouseY)) {
            GuiTools.guiBlit(graphics, ICON, this.getX(), this.getY(), this.width, this.height, 0, 163, this.width, this.height, 256, 256);
        } else if (leftClick(mouseX, mouseY) || rightClick(mouseX, mouseY)) {
            GuiTools.guiBlit(graphics, ICON, this.getX(), this.getY(), this.width, this.height, 0, 150, this.width, this.height, 256, 256);
        } else {
            GuiTools.guiBlit(graphics, ICON, this.getX(), this.getY(), this.width, this.height, 0, 137, this.width, this.height, 256, 256);
        }
        graphics.text(mc.font, this.getMessage(), this.getX() + 5, this.getY() + 3, 0xFF444444, false);
        graphics.centeredText(mc.font, this.parents.getAttackGroups().get(entityId).getComponent(), this.getX() + 142, this.getY() + 3, 0xFFFFFFFF);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (deleteClick(mouseX, mouseY)) {
            this.parents.removeMonsterType(this.entityId);
        } else if (leftClick(mouseX, mouseY)) {
            this.parents.getAttackGroups().computeIfPresent(this.entityId, (k, monsterType) -> monsterType.getPrevious());
        } else if (rightClick(mouseX, mouseY)) {
            this.parents.getAttackGroups().computeIfPresent(this.entityId, (k, monsterType) -> monsterType.getNext());
        }
    }

    private boolean deleteClick(double mouseX, double mouseY) {
        boolean clickY = this.getY() <= mouseY && mouseY <= (this.getY() + this.getHeight());
        boolean deleteClickX = (this.getX() + 107) <= mouseX && mouseX <= (this.getX() + 120);
        return clickY && deleteClickX;
    }

    private boolean leftClick(double mouseX, double mouseY) {
        boolean clickY = this.getY() <= mouseY && mouseY <= (this.getY() + this.getHeight());
        boolean leftClickX = (this.getX() + 120) <= mouseX && mouseX <= (this.getX() + 130);
        return clickY && leftClickX;
    }

    private boolean rightClick(double mouseX, double mouseY) {
        boolean clickY = this.getY() <= mouseY && mouseY <= (this.getY() + this.getHeight());
        boolean rightClickX = (this.getX() + 154) <= mouseX && mouseX <= (this.getX() + 164);
        return clickY && rightClickX;
    }
}
