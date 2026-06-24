package com.github.tartaricacid.touhoulittlemaid.datagen;

import com.github.tartaricacid.touhoulittlemaid.datagen.builder.AltarRecipeBuilder;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagItem;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.EntityTypeUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @SuppressWarnings("all")
    @Override
    protected void buildRecipes() {
        RecipeOutput recipeOutput = this.output;
        AltarRecipeBuilder.shapeless(items, InitItems.BOOKSHELF)
                .power(0.1F)
                .requires(4, ItemTags.PLANKS)
                .requires(Items.BOOK)
                .requires(Tags.Items.GEMS_DIAMOND)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.BROOM)
                .power(0.2F)
                .requires(3, Items.HAY_BLOCK)
                .requires(2, Tags.Items.RODS_WOODEN)
                .requires(Items.ENDER_EYE)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.CAMERA)
                .power(0.2F)
                .requires(4, Items.QUARTZ_BLOCK)
                .requires(2, Tags.Items.OBSIDIANS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.CHISEL)
                .power(0.2F)
                .requires(2, Tags.Items.RODS_WOODEN)
                .requires(2, Tags.Items.INGOTS_IRON)
                .requires(Tags.Items.DYES_YELLOW)
                .requires(Tags.Items.DYES_RED)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.COMPUTER)
                .power(0.1F)
                .requires(3, ItemTags.PLANKS)
                .requires(Items.NOTE_BLOCK)
                .requires(Items.LEVER)
                .requires(Tags.Items.GEMS_DIAMOND)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.DROWN_PROTECT_BAUBLE)
                .power(0.2F)
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(Tags.Items.DYES_LIME)
                .requires(4, ItemTags.FISHES)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.EXPLOSION_PROTECT_BAUBLE)
                .power(0.2F)
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(Tags.Items.DYES_ORANGE)
                .requires(4, Tags.Items.OBSIDIANS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.EXTINGUISHER)
                .power(0.2F)
                .requires(4, Items.CLAY_BALL)
                .requires(Tags.Items.DYES_ORANGE)
                .requires(Tags.Items.DYES_RED)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.FALL_PROTECT_BAUBLE)
                .power(0.2F)
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(Tags.Items.DYES_YELLOW)
                .requires(4, Tags.Items.FEATHERS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.FIRE_PROTECT_BAUBLE)
                .power(0.2F)
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(Tags.Items.DYES_RED)
                .requires(4, Items.BLAZE_POWDER)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.GOMOKU)
                .power(0.1F)
                .requires(3, ItemTags.PLANKS)
                .requires(Tags.Items.DYES_BLACK)
                .requires(Tags.Items.DYES_WHITE)
                .requires(Tags.Items.GEMS_DIAMOND)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.CCHESS)
                .power(0.1F)
                .requires(3, ItemTags.PLANKS)
                .requires(Tags.Items.DYES_BLACK)
                .requires(Tags.Items.DYES_RED)
                .requires(Tags.Items.GEMS_DIAMOND)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.WCHESS)
                .power(0.1F)
                .requires(3, ItemTags.PLANKS)
                .requires(Tags.Items.DYES_BLACK)
                .requires(Tags.Items.DYES_WHITE)
                .requires(Tags.Items.GEMS_EMERALD)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.HAKUREI_GOHEI)
                .power(0.15F)
                .requires(3, Tags.Items.RODS_WOODEN)
                .requires(3, Items.PAPER)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.ITEM_MAGNET_BAUBLE)
                .power(0.2F)
                .requires(3, Tags.Items.DUSTS_REDSTONE)
                .requires(3, Tags.Items.INGOTS_IRON)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.KAPPA_COMPASS)
                .power(0.1F)
                .requires(3, Tags.Items.OBSIDIANS)
                .requires(Tags.Items.DYES_CYAN)
                .requires(2, Tags.Items.DUSTS_REDSTONE)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.KEYBOARD)
                .power(0.1F)
                .requires(4, ItemTags.PLANKS)
                .requires(Items.NOTE_BLOCK)
                .requires(Tags.Items.GEMS_DIAMOND)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.MAGIC_PROTECT_BAUBLE)
                .power(0.2F)
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(Tags.Items.DYES_CYAN)
                .requires(4, Items.SUGAR)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.MAID_BACKPACK_BIG)
                .power(0.3F)
                .requires(4, Items.GRAY_WOOL)
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Items.GRAY_WOOL)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.MAID_BACKPACK_MIDDLE)
                .power(0.2F)
                .requires(4, Items.PINK_WOOL)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Items.PINK_WOOL)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.MAID_BACKPACK_SMALL)
                .power(0.1F)
                .requires(4, Items.RED_WOOL)
                .requires(Tags.Items.INGOTS_IRON)
                .requires(Items.RED_WOOL)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.MAID_BEACON)
                .power(0.2F)
                .requires(ItemTags.PLANKS)
                .requires(Tags.Items.DYES_RED)
                .requires(ItemTags.PLANKS)
                .requires(Tags.Items.OBSIDIANS)
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.OBSIDIANS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.PINK_MAID_BED)
                .power(0.2F)
                .requires(Items.PINK_WOOL)
                .requires(ItemTags.PLANKS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.WHITE_MAID_BED)
                .power(0.2F)
                .requires(Items.WHITE_WOOL)
                .requires(ItemTags.PLANKS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.BLACK_MAID_BED)
                .power(0.2F)
                .requires(Items.BLACK_WOOL)
                .requires(ItemTags.PLANKS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.YELLOW_MAID_BED)
                .power(0.2F)
                .requires(Items.YELLOW_WOOL)
                .requires(ItemTags.PLANKS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.BLUE_MAID_BED)
                .power(0.2F)
                .requires(Items.BLUE_WOOL)
                .requires(ItemTags.PLANKS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.GREEN_MAID_BED)
                .power(0.2F)
                .requires(Items.GREEN_WOOL)
                .requires(ItemTags.PLANKS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.PURPLE_MAID_BED)
                .power(0.2F)
                .requires(Items.PURPLE_WOOL)
                .requires(ItemTags.PLANKS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.MUTE_BAUBLE)
                .power(0.2F)
                .requires(ItemTags.WOOL)
                .requires(Items.CLAY_BALL)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.NIMBLE_FABRIC)
                .power(0.2F)
                .requires(Tags.Items.ENDER_PEARLS)
                .requires(ItemTags.WOOL)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.PICNIC_BASKET)
                .power(0.2F)
                .requires(Tags.Items.CHESTS_WOODEN)
                .requires(4, Items.BAMBOO)
                .requires(ItemTags.WOOL_CARPETS)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.PROJECTILE_PROTECT_BAUBLE)
                .power(0.2F)
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(Tags.Items.DYES_BLUE)
                .requires(4, Items.SHIELD)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.RED_FOX_SCROLL)
                .power(0.1F)
                .requires(4, Items.PAPER)
                .requires(Tags.Items.DYES_RED)
                .requires(Tags.Items.GEMS_DIAMOND)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.SANAE_GOHEI)
                .power(0.15F)
                .requires(4, Tags.Items.RODS_WOODEN)
                .requires(2, Items.PAPER)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.SCARECROW)
                .power(0.2F)
                .requires(2, Items.HAY_BLOCK)
                .requires(2, Items.GRANITE)
                .requires(2, Tags.Items.DUSTS_REDSTONE)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.TRUMPET)
                .power(0.2F)
                .requires(2, Tags.Items.INGOTS_GOLD)
                .requires(3, Tags.Items.INGOTS_IRON)
                .requires(Items.NOTE_BLOCK)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.ULTRAMARINE_ORB_ELIXIR)
                .power(0.3F)
                .requires(Tags.Items.GEMS_EMERALD)
                .requires(Tags.Items.ENDER_PEARLS)
                .requires(4, Tags.Items.DYES_CYAN)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.WHITE_FOX_SCROLL)
                .power(0.1F)
                .requires(4, Items.PAPER)
                .requires(Tags.Items.DYES_WHITE)
                .requires(Tags.Items.GEMS_DIAMOND)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.WIRELESS_IO)
                .power(0.2F)
                .requires(Tags.Items.ENDER_PEARLS)
                .requires(Tags.Items.CHESTS_WOODEN)
                .requires(Items.HOPPER)
                .save(recipeOutput);

        AltarRecipeBuilder.shapeless(items, InitItems.SERVANT_BELL)
                .power(0.2F)
                .requires(2, Tags.Items.INGOTS_GOLD)
                .requires(2, Tags.Items.NUGGETS_GOLD)
                .requires(2, Tags.Items.RODS_WOODEN)
                .save(recipeOutput);

        // TODO 生成普通实体的配方，需要一个更加可视化的物品占位符

        AltarRecipeBuilder.shapeless(items, InitItems.MAID_SPAWN_EGG)
                .power(0.5F)
                .requires(InitItems.FILM)
                .requires(Tags.Items.GEMS_LAPIS)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Tags.Items.DUSTS_REDSTONE)
                .requires(Tags.Items.INGOTS_IRON)
                .requires(Items.COAL)
                .entity(EntityMaid.ENTITY_ID)
                .langKey("jei.touhou_little_maid.altar_craft.reborn_maid.result")
                .save(recipeOutput, "reborn_maid");

        AltarRecipeBuilder.shapeless(items, InitItems.MAID_SPAWN_EGG)
                .power(0.5F)
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.GEMS_LAPIS)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Tags.Items.DUSTS_REDSTONE)
                .requires(Tags.Items.INGOTS_IRON)
                .requires(Items.COAL)
                .entity(EntityType.getKey(InitEntities.BOX.get()))
                .langKey("jei.touhou_little_maid.altar_craft.spawn_box.result")
                .save(recipeOutput, "spawn_box");

        AltarRecipeBuilder.shapeless(items, Items.LIGHT)
                .power(0.2F)
                .requires(3, Tags.Items.GUNPOWDERS)
                .requires(3, Items.BLAZE_POWDER)
                .entity(EntityType.getKey(EntityTypeUtil.lightningBolt()))
                .langKey("jei.touhou_little_maid.altar_craft.spawn_lightning_bolt.result")
                .save(recipeOutput, "spawn_lightning_bolt");

        AltarRecipeBuilder.shapeless(items, InitItems.SNACK_CABINET)
                .power(0.1F)
                .requires(3, ItemTags.PLANKS)
                .requires(2, Tags.Items.GLASS_PANES)
                .requires(Items.BARREL)
                .save(recipeOutput);

        this.shaped(RecipeCategory.MISC, InitItems.HAKUREI_GOHEI)
                .pattern("  D")
                .pattern(" SP")
                .pattern("S P")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('P', Items.PAPER)
                .unlockedBy(getHasName(Items.DIAMOND), has(Tags.Items.GEMS_DIAMOND))
                .save(recipeOutput);

        this.shaped(RecipeCategory.MISC, InitItems.SANAE_GOHEI)
                .pattern(" PD")
                .pattern(" SP")
                .pattern("S  ")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('P', Items.PAPER)
                .unlockedBy(getHasName(Items.DIAMOND), has(Tags.Items.GEMS_DIAMOND))
                .save(recipeOutput);

//        ModLoadedCondition modLoadedCondition = new ModLoadedCondition(CompatRegistry.PATCHOULI);
//        ItemStack patchouliBook = new ItemStack(PatchouliItems.BOOK);
//        patchouliBook.set(PatchouliDataComponents.BOOK, InitItems.MEMORIZABLE_GENSOKYO_LOCATION);
//        this.shapeless(RecipeCategory.MISC, patchouliBook)
//                .requires(Tags.Items.DYES_WHITE)
//                .requires(Tags.Items.DYES_RED)
//                .requires(Items.BOOK)
//                .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK))
//                .save(recipeOutput.withConditions(modLoadedCondition), InitItems.MEMORIZABLE_GENSOKYO_LOCATION);

        this.shaped(RecipeCategory.MISC, InitItems.CHAIR)
                .pattern("   ")
                .pattern("WWW")
                .pattern("IPI")
                .define('W', ItemTags.WOOL)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .save(recipeOutput);

        this.shaped(RecipeCategory.MISC, InitItems.CHAIR_SHOW)
                .pattern(" R ")
                .pattern("WWW")
                .pattern("IPI")
                .define('W', ItemTags.WOOL)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('P', ItemTags.PLANKS)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy(getHasName(Items.REDSTONE), has(Tags.Items.DUSTS_REDSTONE))
                .save(recipeOutput);

        this.shapeless(RecipeCategory.MISC, InitItems.ENTITY_ID_COPY)
                .requires(Tags.Items.LEATHERS)
                .requires(Items.PAPER)
                .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK))
                .save(recipeOutput);

        // 女仆床染色配方
        this.shapeless(RecipeCategory.MISC, InitItems.PINK_MAID_BED)
                .requires(TagItem.MAID_BED)
                .requires(Tags.Items.DYES_PINK)
                .unlockedBy("has_maid_bed", has(TagItem.MAID_BED))
                .save(recipeOutput, "pink_maid_bed_from_dye");

        this.shapeless(RecipeCategory.MISC, InitItems.WHITE_MAID_BED)
                .requires(TagItem.MAID_BED)
                .requires(Tags.Items.DYES_WHITE)
                .unlockedBy("has_maid_bed", has(TagItem.MAID_BED))
                .save(recipeOutput, "white_maid_bed_from_dye");

        this.shapeless(RecipeCategory.MISC, InitItems.BLACK_MAID_BED)
                .requires(TagItem.MAID_BED)
                .requires(Tags.Items.DYES_BLACK)
                .unlockedBy("has_maid_bed", has(TagItem.MAID_BED))
                .save(recipeOutput, "black_maid_bed_from_dye");

        this.shapeless(RecipeCategory.MISC, InitItems.YELLOW_MAID_BED)
                .requires(TagItem.MAID_BED)
                .requires(Tags.Items.DYES_YELLOW)
                .unlockedBy("has_maid_bed", has(TagItem.MAID_BED))
                .save(recipeOutput, "yellow_maid_bed_from_dye");

        this.shapeless(RecipeCategory.MISC, InitItems.BLUE_MAID_BED)
                .requires(TagItem.MAID_BED)
                .requires(Tags.Items.DYES_BLUE)
                .unlockedBy("has_maid_bed", has(TagItem.MAID_BED))
                .save(recipeOutput, "blue_maid_bed_from_dye");

        this.shapeless(RecipeCategory.MISC, InitItems.GREEN_MAID_BED)
                .requires(TagItem.MAID_BED)
                .requires(Tags.Items.DYES_GREEN)
                .unlockedBy("has_maid_bed", has(TagItem.MAID_BED))
                .save(recipeOutput, "green_maid_bed_from_dye");

        this.shapeless(RecipeCategory.MISC, InitItems.PURPLE_MAID_BED)
                .requires(TagItem.MAID_BED)
                .requires(Tags.Items.DYES_PURPLE)
                .unlockedBy("has_maid_bed", has(TagItem.MAID_BED))
                .save(recipeOutput, "purple_maid_bed_from_dye");
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeGenerator(registries, output);
        }

        @Override
        public String getName() {
            return "Touhou Little Maid Recipes";
        }
    }
}
