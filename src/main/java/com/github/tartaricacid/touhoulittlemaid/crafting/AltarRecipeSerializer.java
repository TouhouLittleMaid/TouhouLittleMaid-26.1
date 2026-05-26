package com.github.tartaricacid.touhoulittlemaid.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AltarRecipeSerializer {
    public static final MapCodec<AltarRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", StringUtils.EMPTY).forGetter(AltarRecipe::group),
                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(AltarRecipe::category),
                    Ingredient.CODEC.listOf().fieldOf("ingredients").flatXmap(AltarRecipeSerializer::checkIngredients, DataResult::success).forGetter(AltarRecipe::getIngredients),
                    Codec.FLOAT.fieldOf("power").forGetter(AltarRecipe::getPower),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(AltarRecipe::getResult),
                    Identifier.CODEC.fieldOf("entity").forGetter(AltarRecipe::getEntityType),
                    Codec.STRING.optionalFieldOf("lang", StringUtils.EMPTY).forGetter(AltarRecipe::getLangKey)
            ).apply(instance, AltarRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> STREAM_CODEC = StreamCodec.of(AltarRecipeSerializer::toNetwork, AltarRecipeSerializer::fromNetwork);

    public static final RecipeSerializer<AltarRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    @NotNull
    private static DataResult<NonNullList<Ingredient>> checkIngredients(List<Ingredient> ingredientList) {
        int n = ingredientList.size();
        if (n == 0) {
            return DataResult.error(() -> "No ingredients for shapeless recipe");
        }
        if (n > 6) {
            return DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: 6");
        }
        return DataResult.success(NonNullList.copyOf(ingredientList));
    }

    private static AltarRecipe fromNetwork(RegistryFriendlyByteBuf byteBuf) {
        String group = byteBuf.readUtf();
        CraftingBookCategory category = byteBuf.readEnum(CraftingBookCategory.class);
        int size = byteBuf.readVarInt();
        List<Ingredient> decoded = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            decoded.add(Ingredient.CONTENTS_STREAM_CODEC.decode(byteBuf));
        }
        NonNullList<Ingredient> ingredients = NonNullList.copyOf(decoded);
        float power = byteBuf.readFloat();
        ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(byteBuf);
        Identifier entityType = Identifier.STREAM_CODEC.decode(byteBuf);
        String langKey = byteBuf.readUtf();
        return new AltarRecipe(group, category, ingredients, power, result, entityType, langKey);
    }

    private static void toNetwork(RegistryFriendlyByteBuf friendlyByteBuf, AltarRecipe altarRecipe) {
        friendlyByteBuf.writeUtf(altarRecipe.group());
        friendlyByteBuf.writeEnum(altarRecipe.category());
        friendlyByteBuf.writeVarInt(altarRecipe.getIngredients().size());
        for (Ingredient ingredient : altarRecipe.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, ingredient);
        }
        friendlyByteBuf.writeFloat(altarRecipe.getPower());
        ItemStackTemplate.STREAM_CODEC.encode(friendlyByteBuf, altarRecipe.getResult());
        Identifier.STREAM_CODEC.encode(friendlyByteBuf, altarRecipe.getEntityType());
        friendlyByteBuf.writeUtf(altarRecipe.getLangKey());
    }
}
