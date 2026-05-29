package com.github.tartaricacid.touhoulittlemaid.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AltarRecipeSerializer {
    public static final int MAX_INGREDIENTS = 6;

    public static final MapCodec<AltarRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC.sizeLimitedListOf(MAX_INGREDIENTS).fieldOf("ingredients").forGetter(AltarRecipe::getIngredients),
                    Codec.FLOAT.fieldOf("power").forGetter(AltarRecipe::getPower),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(AltarRecipe::getResult),
                    Identifier.CODEC.fieldOf("entity").forGetter(AltarRecipe::getEntityType),
                    Codec.STRING.optionalFieldOf("lang", StringUtils.EMPTY).forGetter(AltarRecipe::getLangKey)
            ).apply(instance, AltarRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AltarRecipe decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<Ingredient> ingredients = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }
            float power = buf.readFloat();
            ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(buf);
            Identifier entityType = Identifier.STREAM_CODEC.decode(buf);
            String langKey = buf.readUtf();
            return new AltarRecipe(ingredients, power, result, entityType, langKey);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, AltarRecipe recipe) {
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }
            buf.writeFloat(recipe.getPower());
            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.getResult());
            Identifier.STREAM_CODEC.encode(buf, recipe.getEntityType());
            buf.writeUtf(recipe.getLangKey());
        }
    };

    public static final RecipeSerializer<AltarRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
