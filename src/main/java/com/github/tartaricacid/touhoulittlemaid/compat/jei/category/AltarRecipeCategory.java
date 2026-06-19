package com.github.tartaricacid.touhoulittlemaid.compat.jei.category;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AltarRecipeCategory implements IRecipeCategory<RecipeHolder<AltarRecipe>> {
    public static final IRecipeHolderType<AltarRecipe> TYPE = IRecipeType.create(InitRecipes.ALTAR_RECIPE.get());

    private static final Identifier ALTAR_ICON = IdentifierUtil.modLoc("textures/gui/altar_icon.png");
    private static final Identifier POWER_ICON = IdentifierUtil.modLoc("textures/entity/power_point.png");
    private static final MutableComponent TITLE = Component.translatable("jei.touhou_little_maid.altar_craft.title");

    private static final int WIDTH = 160;
    private static final int HEIGHT = 125;
    private static final int TEXT_COLOR = 0xFF555555;

    private final IDrawable iconDraw;
    private final IDrawable powerDraw;

    public AltarRecipeCategory(IGuiHelper guiHelper) {
        this.iconDraw = guiHelper.drawableBuilder(ALTAR_ICON, 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.powerDraw = guiHelper.drawableBuilder(POWER_ICON, 32, 0, 16, 16).setTextureSize(64, 64).build();
    }

    @Override
    public void draw(RecipeHolder<AltarRecipe> holder, IRecipeSlotsView slotsView,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        AltarRecipe recipe = holder.value();

        graphics.pose().pushMatrix();
        graphics.pose().scale(0.8f, 0.8f);
        powerDraw.draw(graphics, 90, 50);
        graphics.pose().popMatrix();

        String powerText = "×%.2f".formatted(recipe.getPower());
        Component resultText = Component.translatable(
                "jei.touhou_little_maid.altar_craft.result",
                Component.translatable(recipe.getLangKey())
        );
        int width = (WIDTH - font.width(resultText)) / 2;

        graphics.text(font, powerText, 65, 55, TEXT_COLOR, false);
        graphics.text(font, resultText.getVisualOrderText(), width, 85, TEXT_COLOR, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AltarRecipe> holder, IFocusGroup focuses) {
        List<Ingredient> inputs = holder.value().getIngredients();
        ItemStackTemplate output = holder.value().getResult();

        addInput(builder, 40, 35, inputs, 0);
        addInput(builder, 40, 55, inputs, 1);
        addInput(builder, 60, 15, inputs, 2);
        addInput(builder, 80, 15, inputs, 3);
        addInput(builder, 100, 35, inputs, 4);
        addInput(builder, 100, 55, inputs, 5);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 138, 6)
                .add(output)
                .setOutputSlotBackground();
    }

    private void addInput(IRecipeLayoutBuilder builder, int x, int y, List<Ingredient> inputs, int index) {
        if (index >= inputs.size()) {
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .add(ItemStack.EMPTY)
                    .setStandardSlotBackground();
        } else {
            Ingredient ingredient = inputs.get(index);
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .add(ingredient)
                    .setStandardSlotBackground();
        }
    }

    @Override
    public IRecipeType<RecipeHolder<AltarRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return iconDraw;
    }
}

