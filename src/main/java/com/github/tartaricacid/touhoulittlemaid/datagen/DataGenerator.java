package com.github.tartaricacid.touhoulittlemaid.datagen;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.*;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID)
public class DataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var pack = generator.getPackOutput();

        // Advancements
        generator.addProvider(true, new AdvancementDataGen(pack, registries));

        // Loot Tables
        generator.addProvider(true, new LootTableProvider(pack, Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(LootTableGenerator.ChestLootTables::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(LootTableGenerator.AdvancementLootTables::new, LootContextParamSets.ADVANCEMENT_REWARD),
                        new LootTableProvider.SubProviderEntry(LootTableGenerator.EntityLootTables::new, LootContextParamSets.ENTITY),
                        new LootTableProvider.SubProviderEntry(LootTableGenerator.BlockLootTables::new, LootContextParamSets.BLOCK)
                ),
                registries));

        // Global Loot Modifier
        generator.addProvider(true, new GlobalLootModifier(pack, registries, TouhouLittleMaid.MOD_ID));

        // Recipe
        event.createProvider(RecipeGenerator.Runner::new);

        // Tags
        event.createBlockAndItemTags(
                (output, lookup) -> new TagBlock(output, lookup, TouhouLittleMaid.MOD_ID),
                (output, lookup, blockTags) -> new TagItem(output, lookup, TouhouLittleMaid.MOD_ID)
        );
        generator.addProvider(true, new TagDamage(pack, registries));
        generator.addProvider(true, new TagEntity(pack, registries));
        generator.addProvider(true, new TagPaintingVariant(pack, registries));
        generator.addProvider(true, new TagEnchantment(pack, registries));

        // Registry Based Stuff
        DatapackBuiltinEntriesProvider datapackProvider = new RegistryDataGenerator(pack, registries);
        generator.addProvider(true, datapackProvider);

        generator.addProvider(true, new DataMapGenerator(pack, registries));
    }
}
