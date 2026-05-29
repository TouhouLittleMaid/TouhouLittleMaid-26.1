package com.github.tartaricacid.touhoulittlemaid.client.event;


import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes;
import com.google.common.collect.Lists;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = TouhouLittleMaid.MOD_ID)
public class ClientRecipeEvent {
    public static List<RecipeHolder<AltarRecipe>> ALTAR_RECIPES = Collections.emptyList();

    @SubscribeEvent
    public static void onRecipeReceived(RecipesReceivedEvent event) {
        RecipeMap recipeMap = event.getRecipeMap();
        ALTAR_RECIPES = Lists.newArrayList(recipeMap.byType(InitRecipes.ALTAR_RECIPE.get()));
    }
}