package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public interface ItemUseHook extends MaidComponent {
    void pickupEntities();

    void onEquipItem(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem);

    void updateUsingItem(ItemStack usingItem);

    void backCurrentHandItemStack(com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid maid);

    void handleExtraItemsCreatedOnUse(ItemStack convertedStack);
}
