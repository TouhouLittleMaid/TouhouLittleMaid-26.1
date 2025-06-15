package com.github.tartaricacid.touhoulittlemaid.api.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

/**
 * 这个事件在女仆被驯服时触发。
 * 事件的触发时机在 EntityMaid#tameMaid 方法中。
 * 事件无法取消
 */
public class MaidTamedEvent extends LivingEvent {
    private final EntityMaid maid;
    private final Player player;
    /**
     * 当女仆是通过主人转换工具强制转换主人时，此值为 true
     */
    private final boolean isOwnerConversion;

    public MaidTamedEvent(EntityMaid maid, Player player, boolean isOwnerConversion) {
        super(maid);
        this.maid = maid;
        this.player = player;
        this.isOwnerConversion = isOwnerConversion;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isOwnerConversion() {
        return isOwnerConversion;
    }
}
