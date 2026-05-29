package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.MOD_ID;

public interface InitRecipes {
    DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, MOD_ID);
    DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MOD_ID);

    Supplier<RecipeBookCategory> ALTAR_RECIPE_CATEGORY = RECIPE_BOOK_CATEGORIES.register("altar", RecipeBookCategory::new);

    Supplier<RecipeSerializer<AltarRecipe>> ALTAR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
            "altar", () -> AltarRecipeSerializer.SERIALIZER
    );

    Supplier<RecipeType<AltarRecipe>> ALTAR_RECIPE = RECIPE_TYPES.register(
            "altar", () -> RecipeType.simple(Identifier.fromNamespaceAndPath(MOD_ID, "altar_recipe"))
    );
}
