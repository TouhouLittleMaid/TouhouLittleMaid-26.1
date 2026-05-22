package com.github.tartaricacid.touhoulittlemaid.datagen.tag;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;

import java.util.concurrent.CompletableFuture;

public class TagPaintingVariant extends PaintingVariantTagsProvider {
    private static final ResourceKey<PaintingVariant> WINE_FOX = ResourceKey.create(Registries.PAINTING_VARIANT, ResourceLocationUtil.getResourceLocation("wine_fox"));

    public TagPaintingVariant(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, TouhouLittleMaid.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(PaintingVariantTags.PLACEABLE).add(WINE_FOX);
    }
}
