package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.entity.LivingEntityFunction;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.MolangUtils;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.util.EquipmentUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;


public class DumpEquippedItem extends LivingEntityFunction {
    @Override
    protected Object eval(ExecutionContext<IContext<LivingEntity>> context, ArgumentCollection arguments) {
        if (!context.entity().isDebugEnabled()) {
            return null;
        }

        EquipmentSlot slotType = MolangUtils.parseSlotType(context.entity(), arguments.getAsString(context, 0));
        if (slotType == null) {
            return null;
        }

        ItemStack itemStack = EquipmentUtil.getEquippedItem(context.entity().entity(), slotType);
        if (itemStack.isEmpty()) {
            return null;
        }

        Identifier id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        if (id == BuiltInRegistries.ITEM.getDefaultKey()) {
            return null;
        }
        context.entity().debugPrint(Component.literal("Display ").append(ComponentUtils.copyOnClickText(itemStack.getItem().getName(itemStack).getString(99))));
        context.entity().debugPrint(Component.literal("Name ").append(ComponentUtils.copyOnClickText(id.toString())));

        itemStack.tags().forEach(key -> {
            context.entity().debugPrint(Component.literal("Tag ").append(ComponentUtils.copyOnClickText(key.location().toString())));
        });

        var lookup = net.neoforged.neoforge.common.CommonHooks.resolveLookup(net.minecraft.core.registries.Registries.ENCHANTMENT);
        if (lookup != null) {
            for (var enchantmentEntry : itemStack.getAllEnchantments(lookup).entrySet()) {
                enchantmentEntry.getKey().unwrapKey().ifPresent(key -> {
                    context.entity().debugPrint(Component.literal("Enchantment: display ")
                            .append(ComponentUtils.copyOnClickText(Enchantment.getFullname(enchantmentEntry.getKey(), enchantmentEntry.getIntValue()).getString(99)))
                            .append(Component.literal("  name ").append(ComponentUtils.copyOnClickText(key.identifier().toString()))));
                });
            }
        }

        return null;
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size == 1;
    }
}
