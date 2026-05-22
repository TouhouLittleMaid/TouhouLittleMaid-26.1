package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.entity.LivingEntityFunction;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.MolangUtils;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.util.EquipmentUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EquippedEnchantmentLevel extends LivingEntityFunction {
    @Override
    protected Object eval(ExecutionContext<IContext<LivingEntity>> context, ArgumentCollection arguments) {
        EquipmentSlot slotType = MolangUtils.parseSlotType(context.entity(), arguments.getAsString(context, 0));
        if (slotType == null) {
            return null;
        }
        var enchantmentsOpt = context.entity().entity().registryAccess().lookup(Registries.ENCHANTMENT);
        if (enchantmentsOpt.isEmpty()) {
            return null;
        }
        var enchantments = enchantmentsOpt.get();

        ItemStack itemStack = EquipmentUtil.getEquippedItem(context.entity().entity(), slotType);
        if (itemStack.isEmpty()) {
            return 0;
        }

        int sum = 0;
        for (var i = 1; i < arguments.size(); ++i) {
            Identifier id = arguments.getAsResourceLocation(context, 1);
            if (id != null) {
                var holder = enchantments.get(id);
                if (holder.isPresent()) {
                    sum += EnchantmentHelper.getTagEnchantmentLevel(holder.get(), itemStack);
                }
            }
        }

        return sum;
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size >= 2;
    }
}
