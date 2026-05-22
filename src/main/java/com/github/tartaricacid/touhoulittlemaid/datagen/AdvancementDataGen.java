package com.github.tartaricacid.touhoulittlemaid.datagen;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.advancements.rewards.GiveSmartSlabConfigTrigger;
import com.github.tartaricacid.touhoulittlemaid.datagen.advancement.BaseAdvancement;
import com.github.tartaricacid.touhoulittlemaid.datagen.advancement.ChallengeAdvancement;
import com.github.tartaricacid.touhoulittlemaid.datagen.advancement.FavorabilityAdvancement;
import com.github.tartaricacid.touhoulittlemaid.datagen.advancement.MaidBaseAdvancement;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementDataGen extends AdvancementProvider {
    public AdvancementDataGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider, List.of(
                new MainAdvancement(),
                new GiveSmartSlab()
        ));
    }

    private static final class GiveSmartSlab implements AdvancementSubProvider {
        @Override
        public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> saver) {
            Advancement.Builder.advancement()
                    .addCriterion("tick", GiveSmartSlabConfigTrigger.Instance.instance())
                    .rewards(AdvancementRewards.Builder.loot(LootTableGenerator.GIVE_SMART_SLAB))
                    .save(saver, Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "give_smart_slab"));
        }
    }

    private static final class MainAdvancement implements AdvancementSubProvider {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
            BaseAdvancement.generate(registries, saver);
            MaidBaseAdvancement.generate(registries, saver);
            FavorabilityAdvancement.generate(saver);
            ChallengeAdvancement.generate(saver);
        }
    }
}
