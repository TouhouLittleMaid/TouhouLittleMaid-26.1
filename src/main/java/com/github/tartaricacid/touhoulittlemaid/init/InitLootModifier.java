package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.loot.LootTableTypeCondition;
import com.github.tartaricacid.touhoulittlemaid.loot.RandomBoardStateFunction;
import com.github.tartaricacid.touhoulittlemaid.loot.SetInitMaidOwnerFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class InitLootModifier {
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITION_TYPES =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, TouhouLittleMaid.MOD_ID);

    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, TouhouLittleMaid.MOD_ID);

    public static final Supplier<MapCodec<? extends LootItemCondition>> LOOT_TABLE_TYPE =
            LOOT_CONDITION_TYPES.register("loot_table_type", () -> LootTableTypeCondition.CODEC);

    public static final Supplier<MapCodec<? extends LootItemConditionalFunction>> BOARD_STATE_RANDOMLY =
            LOOT_FUNCTION_TYPES.register("board_state_randomly", () -> RandomBoardStateFunction.CODEC);

    public static final Supplier<MapCodec<? extends LootItemConditionalFunction>> SET_INIT_MAID_OWNER_FUNCTION =
            LOOT_FUNCTION_TYPES.register("set_init_maid_owner", () -> SetInitMaidOwnerFunction.CODEC);
}
