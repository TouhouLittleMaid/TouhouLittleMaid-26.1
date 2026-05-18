package com.github.tartaricacid.touhoulittlemaid.loot;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemSmartSlab;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.UUID;

public class SetInitMaidOwnerFunction extends LootItemConditionalFunction {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "set_init_maid_owner");
    public static final MapCodec<SetInitMaidOwnerFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance).apply(instance, SetInitMaidOwnerFunction::new)
    );

    protected SetInitMaidOwnerFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    public static SetInitMaidOwnerFunction.Builder create() {
        return new SetInitMaidOwnerFunction.Builder();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (stack.is(InitItems.SMART_SLAB_INIT.get())) {
            Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
            if (entity instanceof Player player) {
                UUID uuid = player.getUUID();
                ItemSmartSlab.setInitMaidOwner(stack, uuid);
            }
        }
        return stack;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetInitMaidOwnerFunction.Builder> {
        @Override
        protected SetInitMaidOwnerFunction.Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetInitMaidOwnerFunction(this.getConditions());
        }
    }
}
