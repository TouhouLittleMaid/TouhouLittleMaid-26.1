package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDamageEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.entity.KubeLivingEntityEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class MaidDamageEventJS implements KubeLivingEntityEvent {
    private final EntityMaid maid;
    private final DamageSource source;
    private float amount;

    public MaidDamageEventJS(MaidDamageEvent event) {
        this.maid = event.getMaid();
        this.source = event.getSource();
        this.amount = event.getAmount();
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public LivingEntity getEntity() {
        return this.maid;
    }
}
