package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl;

import com.github.tartaricacid.touhoulittlemaid.entity.data.ConfigData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.PickType;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponentDef;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.CONFIG;

/**
 * 配置管理器，负责管理女仆的各种配置项
 */
@MaidComponentDef("config")
public class MaidConfigComponent implements MaidComponent {
    private final EntityMaid maid;

    public MaidConfigComponent(EntityMaid maid) {
        this.maid = maid;
    }

    public boolean isHomeModeEnable() {
        return this.getConfigData().isHomeModeEnable();
    }

    public void setHomeModeEnable(boolean enable) {
        this.setConfigData(this.getConfigData().setHomeModeEnable(enable));
    }

    public boolean isPickup() {
        return this.getConfigData().isPickup();
    }

    public void setPickup(boolean isPickup) {
        this.setConfigData(this.getConfigData().setPickup(isPickup));
    }

    public boolean isRideable() {
        return this.getConfigData().isRideable();
    }

    public void setRideable(boolean rideable) {
        this.setConfigData(this.getConfigData().setRideable(rideable));
    }

    public boolean isShowBackpack() {
        return this.getConfigData().isShowBackpack();
    }

    public void setShowBackpack(boolean show) {
        this.setConfigData(this.getConfigData().setShowBackpack(show));
    }

    public boolean isShowBackItem() {
        return this.getConfigData().isShowBackItem();
    }

    public void setShowBackItem(boolean show) {
        this.setConfigData(this.getConfigData().setShowBackItem(show));
    }

    public boolean isChatBubbleShow() {
        return this.getConfigData().isChatBubbleShow();
    }

    public void setChatBubbleShow(boolean show) {
        this.setConfigData(this.getConfigData().setChatBubbleShow(show));
    }

    public float getSoundFreq() {
        return this.getConfigData().soundFreq();
    }

    public void setSoundFreq(float freq) {
        this.setConfigData(this.getConfigData().setSoundFreq(freq));
    }

    public PickType getPickupType() {
        return this.getConfigData().getPickupType();
    }

    public void setPickupType(PickType type) {
        this.setConfigData(this.getConfigData().setPickupType(type));
    }

    public boolean isOpenDoor() {
        return this.getConfigData().isOpenDoor();
    }

    public void setOpenDoor(boolean openDoor) {
        this.setConfigData(this.getConfigData().setOpenDoor(openDoor));
    }

    public boolean isOpenFenceGate() {
        return this.getConfigData().isOpenFenceGate();
    }

    public void setOpenFenceGate(boolean openFenceGate) {
        this.setConfigData(this.getConfigData().setOpenFenceGate(openFenceGate));
    }

    public boolean isActiveClimbing() {
        return this.getConfigData().isActiveClimbing();
    }

    public void setActiveClimbing(boolean activeClimbing) {
        this.setConfigData(this.getConfigData().setActiveClimbing(activeClimbing));
    }

    private ConfigData getConfigData() {
        return this.maid.getData(CONFIG);
    }

    private void setConfigData(ConfigData data) {
        this.maid.setData(CONFIG, data);
    }
}
