package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class InitPainting {
    public static final DeferredRegister<PaintingVariant> PAINTING_VARIANTS = DeferredRegister.create(Registries.PAINTING_VARIANT, TouhouLittleMaid.MOD_ID);

    public static final DeferredHolder<PaintingVariant, PaintingVariant> WINE_FOX = PAINTING_VARIANTS.register("wine_fox", () -> new PaintingVariant(32, 48,
            ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "wine_fox")));
}
