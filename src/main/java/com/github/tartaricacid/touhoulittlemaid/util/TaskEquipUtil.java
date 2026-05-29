package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import java.util.function.Predicate;

public final class TaskEquipUtil {
    private TaskEquipUtil() {
    }

    /**
     * 尝试将背包中满足条件的物品放入主手。
     * 若当前主手已满足条件，直接返回 true。
     */
    public static boolean tryEquipFromBackpack(EntityMaid maid, Predicate<ItemStack> predicate) {
        if (predicate.test(maid.getMainHandItem())) {
            return true;
        }
        var backpack = maid.components().item.getAvailableBackpackInv();
        int slot = ItemsUtil.findStackSlot(backpack, predicate::test);
        if (slot >= 0) {
            int count = ItemUtil.getStack(backpack, slot).getCount();
            ItemStack output = ItemsUtil.extractItem(backpack, slot, count, false, null);
            if (!maid.getMainHandItem().isEmpty()) {
                ItemStack mainhand = maid.getMainHandItem();
                ItemsUtil.setStackInSlot(backpack, slot, mainhand);
            }
            maid.setItemInHand(InteractionHand.MAIN_HAND, output);
            return true;
        }
        return false;
    }

    /**
     * 将主手物品放回背包的第一个空槽位。
     *
     * @return 若成功移动物品进背包返回 true
     */
    public static boolean putMainHandBack(EntityMaid maid) {
        if (maid.getMainHandItem().isEmpty()) {
            return false;
        }
        var backpack = maid.components().item.getAvailableBackpackInv();
        ItemStack mainHandItem = maid.getMainHandItem();
        for (int i = 0; i < backpack.size(); i++) {
            ItemStack stackInSlot = ItemUtil.getStack(backpack, i);
            if (stackInSlot.isEmpty()) {
                ItemsUtil.setStackInSlot(backpack, i, mainHandItem);
                maid.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                return true;
            }
        }
        return false;
    }
}
