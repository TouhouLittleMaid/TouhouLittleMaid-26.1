package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl.MaidItemComponent;
import com.google.common.collect.MapMaker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;


/**
 * 原版的LivingEntityEquipmentWrapper限定了实体的主副手槽的装备，这里我们将其修改为不限制类型
 * Copy From {@link net.neoforged.neoforge.transfer.item.LivingEntityEquipmentWrapper}
 */
public class EntityMaidEquipmentWrapper {

    private static final Map<EntityMaid, EntityMaidEquipmentWrapper> wrappers = new MapMaker().weakKeys().weakValues().makeMap();

    public static ResourceHandler<ItemResource> of(EntityMaid entity, EquipmentSlot.Type equipmentType) {
        return internalOf(entity, equipmentType);
    }

    public static ResourceHandler<ItemResource> of(EntityMaid entity, EquipmentSlot equipmentSlot) {
        return internalOf(entity, equipmentSlot.getType()).getSlotWrapper(equipmentSlot.getIndex());
    }

    private static EntityMaidEquipmentWrapper.EquipmentTypeWrapper internalOf(EntityMaid entity, EquipmentSlot.Type equipmentType) {
        var wrapper = wrappers.computeIfAbsent(entity, EntityMaidEquipmentWrapper::new);
        return wrapper.byType.get(equipmentType);
    }

    private final EntityMaid maid;
    private final Map<EquipmentSlot.Type, EntityMaidEquipmentWrapper.EquipmentTypeWrapper> byType;

    private EntityMaidEquipmentWrapper(EntityMaid maid) {
        this.maid = maid;
        this.byType = new EnumMap<>(EquipmentSlot.Type.class);
        for (var equipmentType : EquipmentSlot.Type.values()) {
            var slotWrappers = new ArrayList<EntityMaidEquipmentWrapper.SlotWrapper>();
            for (var equipmentSlot : EquipmentSlot.VALUES) {
                if (equipmentSlot.getType() == equipmentType) {
                    slotWrappers.add(new EntityMaidEquipmentWrapper.SlotWrapper(equipmentSlot));
                }
            }
            this.byType.put(equipmentType, new EquipmentTypeWrapper(slotWrappers.toArray(SlotWrapper[]::new)));
        }
    }


    private static class EquipmentTypeWrapper extends CombinedResourceHandler<ItemResource> {
        EquipmentTypeWrapper(EntityMaidEquipmentWrapper.SlotWrapper... handlers) {
            super(handlers);
        }

        EntityMaidEquipmentWrapper.SlotWrapper getSlotWrapper(int index) {
            return (EntityMaidEquipmentWrapper.SlotWrapper) getHandlerFromIndex(index);
        }
    }

    private class SlotWrapper extends ItemStackResourceHandler {
        private final EquipmentSlot slot;

        private SlotWrapper(EquipmentSlot slot) {
            this.slot = slot;
        }

        @Override
        protected ItemStack getStack() {
            return maid.getItemBySlot(slot);
        }

        @Override
        protected void setStack(ItemStack stack) {
            maid.setItemSlot(slot, stack, true);
        }

        @Override
        protected boolean isValid(ItemResource resource) {
            return MaidItemComponent.canInsertItem(resource.toStack());
        }

        @Override
        protected int getCapacity(ItemResource resource) {
            int slotLimit = slot.countLimit == 0 ? Item.ABSOLUTE_MAX_STACK_SIZE : slot.countLimit;
            return resource.isEmpty() ? slotLimit : Math.min(slotLimit, resource.getMaxStackSize());
        }

        @Override
        protected void onRootCommit(ItemStack originalState) {
            maid.onEquipItem(slot, originalState, getStack());
        }

        @Override
        public String toString() {
            return "entity equipment wrapper[entity=" + maid + ",slot=" + slot + "]";
        }
    }
}
