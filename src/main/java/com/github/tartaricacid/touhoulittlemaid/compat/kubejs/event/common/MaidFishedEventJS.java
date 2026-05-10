package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidFishedEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.MaidFishingHook;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnegative;

public class MaidFishedEventJS implements KubeEvent {
    private final EntityMaid maid;
    private final NonNullList<ItemStack> drops = NonNullList.create();
    private final MaidFishingHook hook;
    private int rodDamage;

    public MaidFishedEventJS(MaidFishedEvent event) {
        this.maid = event.getMaid();
        this.drops.addAll(event.getDrops());
        this.hook = event.getHook();
        this.rodDamage = event.getRodDamage();
    }

    public void damageRodBy(@Nonnegative int rodDamage) {
        this.rodDamage = rodDamage;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public NonNullList<ItemStack> getDrops() {
        return drops;
    }

    public MaidFishingHook getHook() {
        return hook;
    }

    public int getRodDamage() {
        return rodDamage;
    }
}
