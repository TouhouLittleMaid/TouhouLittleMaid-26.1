package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTamedEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.entity.KubeLivingEntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;


public class MaidTamedEventJS implements KubeLivingEntityEvent {
    private final EntityMaid maid;
    private final Player player;
    private final boolean isOwnerConversion;

    public MaidTamedEventJS(MaidTamedEvent event) {
        this.maid = event.getMaid();
        this.player = event.getPlayer();
        this.isOwnerConversion = event.isOwnerConversion();
    }

    public EntityMaid getMaid() {
        return maid;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public boolean isOwnerConversion() {
        return isOwnerConversion;
    }

    @Override
    public LivingEntity getEntity() {
        return maid;
    }
}
