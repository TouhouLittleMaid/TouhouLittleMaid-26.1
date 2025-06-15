package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidEquipEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.entity.KubeLivingEntityEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MaidEquipEventJS implements KubeLivingEntityEvent {
    private final EntityMaid maid;
    private final EquipmentSlot slot;
    private final ItemStack stack;

    public MaidEquipEventJS(MaidEquipEvent event) {
        this.maid = event.getMaid();
        this.slot = event.getSlot();
        this.stack = event.getStack();
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public LivingEntity getEntity() {
        return this.maid;
    }
}
