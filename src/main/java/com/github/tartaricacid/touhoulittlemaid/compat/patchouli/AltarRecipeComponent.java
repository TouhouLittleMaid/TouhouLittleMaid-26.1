package com.github.tartaricacid.touhoulittlemaid.compat.patchouli;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.event.ClientRecipeEvent;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBox;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
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
        Identifier recipeId = Identifier.parse(variables.get(RECIPE_ID, level.registryAccess()).asString());
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeId);
        for (RecipeHolder<AltarRecipe> recipe : ClientRecipeEvent.ALTAR_RECIPES) {
            if (recipe.id().equals(recipeKey)) {
                this.recipe = recipe.value();
                return;
            }
        }
        this.recipe = new AltarRecipe(List.of(), 0f, new ItemStackTemplate(Items.APPLE), EntityMaid.ENTITY_ID, "");
        if (!ClientRecipeEvent.ALTAR_RECIPES.isEmpty()) {
            TouhouLittleMaid.LOGGER.error("Altar recipe not found: {}", recipeId);
        }
    }

    @Nullable
    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null) {
            return null;
        }

        if (key.startsWith(INPUT)) {
            int index = Integer.parseInt(key.substring(INPUT.length())) - 1;
            if (index < 0 || index >= recipe.getIngredients().size()) {
                return IVariable.from(ItemStack.EMPTY, level.registryAccess());
            }
            Ingredient ingredient = recipe.getIngredients().get(index);
            if (ingredient.isEmpty()) {
                return IVariable.from(ItemStack.EMPTY, level.registryAccess());
            }
            List<String> stackNames = Lists.newArrayList();
            HolderSet<Item> stacks = ingredient.getValues();
            for (Holder<Item> holder : stacks) {
                stackNames.add(holder.getRegisteredName());
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
                return IVariable.from(recipe.getResult().create(), level.registryAccess());
            }
            case OUTPUT_DESC -> {
                return IVariable.wrap(I18n.get(recipe.getLangKey()), level.registryAccess());
            }
            case OUTPUT_ENTITY -> {
                Identifier entityId = recipe.getEntityType();
                // 特判，女仆生成是实体对象是盒子，这里纠正为女仆
                if (EntityBox.ENTITY_ID.equals(entityId)) {
                    entityId = EntityMaid.ENTITY_ID;
                }
                return IVariable.wrap(entityId.toString(), level.registryAccess());
            }
        }

        return null;
    }
}
