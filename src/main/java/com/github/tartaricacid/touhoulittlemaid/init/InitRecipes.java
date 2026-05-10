package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipeSerializer;
import com.github.tartaricacid.touhoulittlemaid.crafting.FallbackIngredient;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class InitRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, TouhouLittleMaid.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AltarRecipe>> ALTAR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("altar_recipe_serializers", AltarRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<AltarRecipe>> ALTAR_CRAFTING = RECIPE_TYPES.register("altar_recipe", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "altar_crafting")));
    public static final DeferredHolder<IngredientType<?>, IngredientType<FallbackIngredient>> FALLBACK_INGREDIENT_TYPE = INGREDIENT_TYPES.register("fallback_ingredient", () -> new IngredientType<>(FallbackIngredient.CODEC));
}
