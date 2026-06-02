package com.github.tartaricacid.touhoulittlemaid.datagen.builder;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.google.common.collect.Lists;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.List;

public class AltarRecipeBuilder implements RecipeBuilder {
    private static final String NAME = "altar_recipe";

    private final HolderGetter<Item> items;

    private final List<Ingredient> ingredients;
    private final ItemStackTemplate result;
    private float power;
    private Identifier entityType;
    private String langKey;

    public AltarRecipeBuilder(HolderGetter<Item> items, ItemLike result, int count) {
        this(items, new ItemStackTemplate(result.asItem(), count));
    }

    public AltarRecipeBuilder(HolderGetter<Item> items, ItemStackTemplate result) {
        this.items = items;
        this.power = 0;
        this.result = result;
        this.entityType = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ITEM);
        this.ingredients = Lists.newArrayList();
        this.langKey = "jei.touhou_little_maid.altar_craft.item_craft.result";
    }

    public static AltarRecipeBuilder shapeless(HolderGetter<Item> items, ItemStackTemplate result) {
        return new AltarRecipeBuilder(items, result);
    }

    public static AltarRecipeBuilder shapeless(HolderGetter<Item> items, ItemLike result) {
        return shapeless(items, result, 1);
    }

    public static AltarRecipeBuilder shapeless(HolderGetter<Item> items, ItemLike result, int count) {
        return new AltarRecipeBuilder(items, result, count);
    }

    public AltarRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(this.items.getOrThrow(tag)));
    }

    public AltarRecipeBuilder requires(int count, TagKey<Item> tag) {
        return this.requires(Ingredient.of(this.items.getOrThrow(tag)), count);
    }

    public AltarRecipeBuilder requires(ItemLike item) {
        return this.requires(1, item);
    }

    public AltarRecipeBuilder requires(int quantity, ItemLike item) {
        for (int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }
        return this;
    }

    public AltarRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public AltarRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    public AltarRecipeBuilder power(float power) {
        this.power = power;
        return this;
    }

    public AltarRecipeBuilder entity(Identifier entityType) {
        this.entityType = entityType;
        return this;
    }

    public AltarRecipeBuilder langKey(String langKey) {
        this.langKey = langKey;
        return this;
    }

    @Override
    public AltarRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public AltarRecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        String path = RecipeBuilder.getDefaultRecipeId(this.result).identifier().getPath();
        Identifier filePath = IdentifierUtil.modLoc(NAME + "/" + path);
        return ResourceKey.create(Registries.RECIPE, filePath);
    }

    @Override
    public void save(RecipeOutput output) {
        this.save(output, this.defaultId());
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        List<Ingredient> copyOf = List.copyOf(this.ingredients);
        AltarRecipe recipe = new AltarRecipe(copyOf, power, result, entityType, langKey);
        output.accept(id, recipe, null);
    }
}
