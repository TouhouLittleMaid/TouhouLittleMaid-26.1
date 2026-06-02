package com.github.tartaricacid.touhoulittlemaid.datagen;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RegistryDataGenerator extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.ENCHANTMENT, EnchantmentKeys::bootstrap)
            .add(Registries.DAMAGE_TYPE, DamageTypeProvider::bootstrap)
            .add(Registries.PAINTING_VARIANT, RegistryDataGenerator::genPainting)
            .add(Registries.TIMELINE, TimelinesProvider::bootstrap);

    private static void genPainting(BootstrapContext<PaintingVariant> ctx) {
        var id = IdentifierUtil.modLoc("wine_fox");
        ctx.register(ResourceKey.create(Registries.PAINTING_VARIANT, id),
                new PaintingVariant(2, 3, id, Optional.empty(), Optional.empty()));
    }

    public RegistryDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", TouhouLittleMaid.MOD_ID));
    }

}
