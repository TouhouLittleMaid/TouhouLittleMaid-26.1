package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTickEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.entity.KubeLivingEntityEvent;
import net.minecraft.world.entity.LivingEntity;

public class MaidTickEventJS implements KubeLivingEntityEvent {
    private final EntityMaid maid;

    public MaidTickEventJS(MaidTickEvent event) {
        this.maid = event.getMaid();
    }

    public EntityMaid getMaid() {
        return maid;
    }

    @Override
    public LivingEntity getEntity() {
        return maid;
    }
}
