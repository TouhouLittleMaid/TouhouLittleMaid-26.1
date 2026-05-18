package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemEntityPlaceholder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public final class JERIUtil {
    public static void recipeWarpHolder(List<RecipeHolder<AltarRecipe>> altarRecipes, AltarRecipeMaker maker) {
        for (RecipeHolder<AltarRecipe> holder : altarRecipes) {
            recipeMaker(maker, holder.id().identifier(), holder.value());
        }
    }

    public static void recipeWarp(List<RecipeHolder<AltarRecipe>> altarRecipes, AltarRecipeMaker maker) {
        for (RecipeHolder<AltarRecipe> holder : altarRecipes) {
            recipeMaker(maker, holder.id().identifier(), holder.value());
        }
    }

    private static void recipeMaker(AltarRecipeMaker maker, Identifier recipeId, AltarRecipe altarRecipe) {
        ItemStack output = altarRecipe.getResult();
        if (!altarRecipe.isItemCraft()) {
            output = InitItems.ENTITY_PLACEHOLDER.get().getDefaultInstance();
            ItemEntityPlaceholder.setRecipeId(output, altarRecipe.getRecipeString());
        }
        maker.accept(recipeId, altarRecipe.getIngredients(), output, altarRecipe.getPower(), altarRecipe.getLangKey());
    }

    public interface AltarRecipeMaker {
        void accept(Identifier recipeId, NonNullList<Ingredient> inputs, ItemStack output, float powerCost, String langKey);
    }
}
