package com.github.tartaricacid.touhoulittlemaid.crafting;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class FallbackIngredient implements ICustomIngredient {
    private static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
            dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
            json -> new Dynamic<>(JsonOps.INSTANCE, json)
    );

    public static final MapCodec<FallbackIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FallbackEntry.CODEC.listOf().fieldOf("fallbacks").forGetter(FallbackIngredient::fallbacks)
    ).apply(instance, FallbackIngredient::new));

    private final List<FallbackEntry> fallbacks;
    private final Ingredient resolvedIngredient;

    public FallbackIngredient(List<FallbackEntry> fallbacks) {
        this.fallbacks = List.copyOf(fallbacks);
        this.resolvedIngredient = resolveIngredient(this.fallbacks);
    }

    public List<FallbackEntry> fallbacks() {
        return this.fallbacks;
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.resolvedIngredient.test(stack);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(this.resolvedIngredient.getItems());
    }

    @Override
    public boolean isSimple() {
        return this.resolvedIngredient.isSimple();
    }

    @Override
    public IngredientType<?> getType() {
        return InitRecipes.FALLBACK_INGREDIENT_TYPE.get();
    }

    private static Ingredient resolveIngredient(List<FallbackEntry> fallbacks) {
        for (FallbackEntry entry : fallbacks) {
            if (!ModList.get().isLoaded(entry.modid())) {
                continue;
            }
            var parsed = Ingredient.CODEC.parse(JsonOps.INSTANCE, entry.value())
                    .resultOrPartial(message -> TouhouLittleMaid.LOGGER.error("Failed to parse fallback ingredient for mod {}: {}", entry.modid(), message));
            if (parsed.isPresent()) {
                return parsed.get();
            }
        }
        return Ingredient.EMPTY;
    }

    public record FallbackEntry(String modid, JsonElement value) {
        public static final Codec<FallbackEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("modid").forGetter(FallbackEntry::modid),
                JSON_ELEMENT_CODEC.fieldOf("value").forGetter(FallbackEntry::value)
        ).apply(instance, FallbackEntry::new));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof FallbackIngredient that)) {
            return false;
        }
        return Objects.equals(this.fallbacks, that.fallbacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fallbacks);
    }
}
