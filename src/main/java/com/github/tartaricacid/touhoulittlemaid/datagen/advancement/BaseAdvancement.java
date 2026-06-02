package com.github.tartaricacid.touhoulittlemaid.datagen.advancement;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.MaidEventTrigger;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.datagen.LootTableGenerator;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.RecipeCraftedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;


public class BaseAdvancement {
    public static void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
        AdvancementHolder root = make(InitItems.HAKUREI_GOHEI.get(), "craft_gohei")
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion("craft_hakurei_gohei", RecipeCraftedTrigger.TriggerInstance.craftedItem(recipeKey("hakurei_gohei")))
                .addCriterion("craft_sanae_gohei", RecipeCraftedTrigger.TriggerInstance.craftedItem(recipeKey("sanae_gohei")))
                .rewards(AdvancementRewards.Builder.experience(50))
                .save(saver, id("base/craft_gohei").toString());

        generateAltar(registries, saver, root);

        generateMaid(saver, root);

        generateChair(saver, root);
    }

    private static void generateChair(Consumer<AdvancementHolder> saver, AdvancementHolder root) {
        AdvancementHolder chair = make(InitItems.CHAIR.get(), "craft_chair").parent(root)
                .addCriterion("craft_chair", RecipeCraftedTrigger.TriggerInstance.craftedItem(recipeKey("chair")))
                .save(saver, id("base/craft_chair").toString());

        make(InitItems.CHANGE_CHAIR_MODEL.get(), "change_chair_model").parent(chair)
                .addCriterion("maid_event", MaidEventTrigger.create(TriggerType.CHANGE_CHAIR_MODEL))
                .save(saver, id("base/change_chair_model").toString());
    }

    private static void generateMaid(Consumer<AdvancementHolder> saver, AdvancementHolder root) {
//        ItemStack stack = ItemEntityPlaceholder.setRecipeId(new ItemStack(InitItems.ENTITY_PLACEHOLDER.get()), "spawn_box");
//        AdvancementHolder spawnMaid = make(stack, "spawn_maid").parent(root)
//                .addCriterion("altar_craft", AltarCraftTrigger.Instance.recipe(id("altar_recipe/spawn_box")))
//                .rewards(AdvancementRewards.Builder.loot(LootTableGenerator.CAKE))
//                .save(saver, id("base/spawn_maid").toString());
//
//        makeGoal(Items.CAKE, "tamed_maid").parent(spawnMaid)
//                .addCriterion("maid_event", MaidEventTrigger.create(TriggerType.TAMED_MAID))
//                .save(saver, id("base/tamed_maid").toString());
//
//        make(InitItems.CHANGE_MAID_MODEL.get(), "change_maid_model").parent(spawnMaid)
//                .addCriterion("maid_event", MaidEventTrigger.create(TriggerType.CHANGE_MAID_MODEL))
//                .save(saver, id("base/change_maid_model").toString());
//
//        make(Items.JUKEBOX, "change_maid_sound").parent(spawnMaid)
//                .addCriterion("maid_event", MaidEventTrigger.create(TriggerType.CHANGE_MAID_SOUND))
//                .save(saver, id("base/change_maid_sound").toString());
    }

    private static void generateAltar(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, AdvancementHolder root) {
        HolderGetter<EntityType<?>> entityTypes = registries.lookupOrThrow(Registries.ENTITY_TYPE);

        AdvancementHolder altar = make(Items.RED_WOOL, "build_altar").parent(root)
                .addCriterion("maid_event", MaidEventTrigger.create(TriggerType.BUILD_ALTAR))
                .rewards(AdvancementRewards.Builder.loot(LootTableGenerator.ADVANCEMENT_POWER_POINT))
                .save(saver, id("base/build_altar").toString());

        EntityPredicate.Builder predicate = EntityPredicate.Builder.entity().of(entityTypes, InitEntities.FAIRY.get());
        make(InitItems.FAIRY_SPAWN_EGG.get(), "kill_maid_fairy").parent(altar)
                .addCriterion("killed_entity", KilledTrigger.TriggerInstance.playerKilledEntity(predicate))
                .save(saver, id("base/kill_maid_fairy").toString());

        make(InitItems.POWER_POINT.get(), "pickup_power_point").parent(altar)
                .addCriterion("maid_event", MaidEventTrigger.create(TriggerType.PICKUP_POWER_POINT))
                .save(saver, id("base/pickup_power_point").toString());
    }

    private static Advancement.Builder make(ItemLike item, String key) {
        MutableComponent title = Component.translatable(String.format("advancements.touhou_little_maid.base.%s.title", key));
        MutableComponent desc = Component.translatable(String.format("advancements.touhou_little_maid.base.%s.description", key));

        return Advancement.Builder.advancement().display(item, title, desc,
                IdentifierUtil.modLoc("textures/advancements/backgrounds/stone.png"),
                AdvancementType.TASK, true, true, false);
    }

    private static Advancement.Builder make(ItemStack item, String key) {
        MutableComponent title = Component.translatable(String.format("advancements.touhou_little_maid.base.%s.title", key));
        MutableComponent desc = Component.translatable(String.format("advancements.touhou_little_maid.base.%s.description", key));

        return Advancement.Builder.advancement().display(ItemStackTemplate.fromNonEmptyStack(item), title, desc,
                IdentifierUtil.modLoc("textures/advancements/backgrounds/stone.png"),
                AdvancementType.TASK, true, true, false);
    }

    private static Advancement.Builder makeGoal(ItemLike item, String key) {
        MutableComponent title = Component.translatable(String.format("advancements.touhou_little_maid.base.%s.title", key));
        MutableComponent desc = Component.translatable(String.format("advancements.touhou_little_maid.base.%s.description", key));

        return Advancement.Builder.advancement().display(item, title, desc,
                IdentifierUtil.modLoc("textures/advancements/backgrounds/stone.png"),
                AdvancementType.GOAL, true, true, false);
    }

    private static Identifier id(String id) {
        return IdentifierUtil.modLoc(id);
    }

    private static ResourceKey<Recipe<?>> recipeKey(String id) {
        return ResourceKey.create(Registries.RECIPE, id(id));
    }
}
