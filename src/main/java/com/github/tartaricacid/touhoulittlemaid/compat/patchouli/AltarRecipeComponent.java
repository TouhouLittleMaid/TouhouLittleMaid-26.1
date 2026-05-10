package com.github.tartaricacid.touhoulittlemaid.compat.patchouli;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes;
import com.google.common.collect.Lists;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

public class AltarRecipeComponent implements IComponentProcessor {
    private static final String RECIPE_ID = "recipe_id";
    private static final String INPUT = "input";
    private static final String POWER_COST = "power_cost";
    private static final String OUTPUT_ITEM = "output_item";
    private static final String OUTPUT_ENTITY = "output_entity";
    private static final String OUTPUT_DESC = "output_desc";

    private @Nullable AltarRecipe recipe;

    @SuppressWarnings("all")
    @Override
    public void setup(Level level, IVariableProvider variables) {
        ResourceLocation recipeId = ResourceLocation.parse(variables.get(RECIPE_ID, level.registryAccess()).asString());
        List<RecipeHolder<AltarRecipe>> allAltarRecipes = level.getRecipeManager().getAllRecipesFor(InitRecipes.ALTAR_CRAFTING.get());
        for (RecipeHolder<AltarRecipe> recipe : allAltarRecipes) {
            if (recipe.id().equals(recipeId)) {
                this.recipe = recipe.value();
                return;
            }
        }
        this.recipe = new AltarRecipe("altar_recipe", CraftingBookCategory.MISC, NonNullList.of(Ingredient.EMPTY),
                0, ItemStack.EMPTY, ResourceLocation.withDefaultNamespace("item"), "");
        TouhouLittleMaid.LOGGER.error("Altar recipe not found: {}", recipeId);
    }

    @Nullable
    @Override
    public IVariable process(Level level, String key) {
        if (key.startsWith(INPUT)) {
            int index = Integer.parseInt(key.substring(INPUT.length())) - 1;
            if (index < 0 || index >= recipe.getIngredients().size()) {
                return IVariable.from(ItemStack.EMPTY, level.registryAccess());
            }
            Ingredient ingredient = recipe.getIngredients().get(index);
            ItemStack[] stacks = ingredient.getItems();
            if (stacks.length == 0) {
                return IVariable.from(ItemStack.EMPTY, level.registryAccess());
            }
            List<String> stackNames = Lists.newArrayList();
            for (ItemStack stack : stacks) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                stackNames.add(itemId.toString());
            }
            return IVariable.wrap(StringUtils.join(stackNames, ","), level.registryAccess());
        }

        switch (key) {
            case POWER_COST -> {
                float powerCost = recipe.getPower();
                return IVariable.wrap(String.format("x%.2f", powerCost), level.registryAccess());
            }
            case OUTPUT_ITEM -> {
                if (!recipe.isItemCraft()) {
                    return IVariable.from(ItemStack.EMPTY, level.registryAccess());
                }
                return IVariable.from(recipe.getResultItem(level.registryAccess()), level.registryAccess());
            }
            case OUTPUT_DESC -> {
                return IVariable.wrap(I18n.get(recipe.getLangKey()), level.registryAccess());
            }
            case OUTPUT_ENTITY -> {
                String entityId = recipe.getEntityType().toString();
                // 特判，女仆生成是实体对象是盒子，这里纠正为女仆
                if ("touhou_little_maid:box".equals(entityId)) {
                    entityId = "touhou_little_maid:maid";
                }
                return IVariable.wrap(entityId, level.registryAccess());
            }
        }

        return null;
    }
}
