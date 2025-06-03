package com.github.tartaricacid.touhoulittlemaid.api.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

/**
 * 用于修改女仆名称的事件。
 */
public class MaidTypeNameEvent extends Event {
    private final EntityMaid maid;
    private @Nullable Component typeName = null;

    public MaidTypeNameEvent(EntityMaid maid) {
        this.maid = maid;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    /**
     * 当女仆类型名称为 null 时，表示回退使用默认名称。
     */
    @Nullable
    public Component getTypeName() {
        return typeName;
    }

    public void setTypeName(@Nullable Component typeName) {
        this.typeName = typeName;
    }
}
