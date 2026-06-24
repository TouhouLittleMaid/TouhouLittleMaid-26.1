package com.github.tartaricacid.touhoulittlemaid.crafting;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBox;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes;
import com.github.tartaricacid.touhoulittlemaid.item.ItemFilm;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.EntityTypeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AltarRecipe implements Recipe<CraftingInput> {
    private final ItemStackTemplate result;
    private final List<Ingredient> ingredients;
    private final float power;
    private final Identifier entityType;
    private final String langKey;

    public AltarRecipe(List<Ingredient> ingredients, float power, ItemStackTemplate result,
                       Identifier entityType, String langKey
    ) {
        this.result = result;
        this.ingredients = ingredients;
        this.power = power;
        this.entityType = entityType;
        this.langKey = langKey;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        var nonEmptyItems = new ArrayList<ItemStack>(input.ingredientCount());
        for (var item : input.items()) {
            if (!item.isEmpty()) {
                nonEmptyItems.add(item);
            }
        }
        if (nonEmptyItems.size() != this.ingredients.size()) {
            return false;
        }
        return RecipeMatcher.findMatches(nonEmptyItems, this.ingredients) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return this.result.create();
    }

    public void spawnOutputEntity(ServerLevel world, BlockPos pos, @Nullable List<ItemStack> list) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(entityType);

        if (type == EntityTypeUtil.item()) {
            this.spawnItem(world, pos);
            return;
        }

        if (type == InitEntities.BOX.get()) {
            this.spawnBoxMaid(world, pos);
            return;
        }

        if (type == InitEntities.MAID.get()) {
            this.rebornMaid(world, pos, list);
            return;
        }

        // 生成类型为 EVENT 也许更合适
        type.spawn(world, pos, EntitySpawnReason.EVENT);
    }

    private void rebornMaid(ServerLevel world, BlockPos pos, @Nullable List<ItemStack> list) {
        ItemStack itemFilm = ItemStack.EMPTY;
        if (list != null) {
            itemFilm = list.stream()
                    .filter(stack -> stack.getItem() instanceof ItemFilm)
                    .findFirst()
                    .orElse(ItemStack.EMPTY);
        }

        EntityMaid maid = new EntityMaid(world);
        CustomData compoundData = itemFilm.get(InitDataComponent.MAID_INFO);
        if (compoundData != null) {
            var input = TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), compoundData.copyTag());
            maid.readAdditionalSaveData(input);
        } else {
            EventHooks.finalizeMobSpawn(maid, world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.EVENT, null);
        }

        maid.setPos(pos.getX(), pos.getY(), pos.getZ());
        world.addFreshEntity(maid);
    }

    private void spawnBoxMaid(ServerLevel world, BlockPos pos) {
        EntityBox box = new EntityBox(world);
        box.setPos(pos.getX(), pos.getY(), pos.getZ());

        EntityMaid maid = new EntityMaid(world);
        maid.setPos(pos.getX(), pos.getY(), pos.getZ());
        EventHooks.finalizeMobSpawn(maid, world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.EVENT, null);
        maid.startRiding(box, true, true);

        world.tryAddFreshEntityWithPassengers(box);
    }

    private void spawnItem(ServerLevel world, BlockPos pos) {
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), this.result.create());
        world.addFreshEntity(itemEntity);
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<AltarRecipe> getSerializer() {
        return InitRecipes.ALTAR_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<AltarRecipe> getType() {
        return InitRecipes.ALTAR_RECIPE.get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return InitRecipes.ALTAR_RECIPE_CATEGORY.get();
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public float getPower() {
        return power;
    }

    public ItemStackTemplate getResult() {
        return result;
    }

    public Identifier getEntityType() {
        return entityType;
    }

    public String getLangKey() {
        return langKey;
    }
}
