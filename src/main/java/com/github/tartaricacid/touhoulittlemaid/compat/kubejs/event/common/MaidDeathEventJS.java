package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.entity.KubeLivingEntityEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class MaidDeathEventJS implements KubeLivingEntityEvent {
    private final EntityMaid maid;
    private final DamageSource source;

    public MaidDeathEventJS(MaidDeathEvent event) {
        this.maid = event.getMaid();
        this.source = event.getSource();
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public DamageSource getSource() {
        return source;
    }

    @Override
    public LivingEntity getEntity() {
        return this.maid;
    }
}
