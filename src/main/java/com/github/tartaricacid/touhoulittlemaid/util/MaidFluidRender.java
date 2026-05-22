package com.github.tartaricacid.touhoulittlemaid.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;


/**
 * From JEI: <a href="https://github.com/mezz/JustEnoughItems/blob/26.1/Library/src/main/java/mezz/jei/library/render/FluidTankRenderer.java">...</a> and
 * <a href="https://github.com/mezz/JustEnoughItems/blob/26.1/NeoForge/src/main/java/mezz/jei/neoforge/platform/FluidHelper.java">...</a>
 */
public final class MaidFluidRender {
    private static final int TEXTURE_SIZE = 16;

    public static Component getFluidName(String fluidId, int amount) {
        Fluid fluid = BuiltInRegistries.FLUID.get(Identifier.parse(fluidId)).map(Holder.Reference::value).orElse(null);
        if (amount <= 0 || fluid == null || fluid.isSame(Fluids.EMPTY)) {
            return Component.translatable("tooltips.touhou_little_maid.tank_backpack.empty_fluid");
        }
        return fluid.getFluidType().getDescription();
    }

    public static void drawFluid(GuiGraphicsExtractor graphics, int x, int y, int width, int height, String fluidId, int amount, int capacity) {
        Fluid fluid = BuiltInRegistries.FLUID.get(Identifier.parse(fluidId)).map(Holder.Reference::value).orElse(null);
        if (amount <= 0 || fluid == null || fluid.isSame(Fluids.EMPTY)) {
            return;
        }
        FluidStack fluidStack = new FluidStack(fluid, amount);
        getStillFluidSprite(fluidStack).ifPresent(fluidStillSprite -> {
            int fluidColor = getColorTint(fluidStack);
            int scaledAmount = (amount * height) / capacity;
            if (scaledAmount < 1) {
                // 至少渲染一行像素，让人知道里面有东西
                scaledAmount = 1;
            }
            if (scaledAmount > height) {
                scaledAmount = height;
            }
            drawTiledSprite(graphics, width, height, fluidColor, scaledAmount, fluidStillSprite, x, y);
        });
    }


    public static int getColorTint(FluidStack ingredient) {
        Fluid fluid = ingredient.getFluid();
        Minecraft minecraft = Minecraft.getInstance();
        ModelManager modelManager = minecraft.getModelManager();
        FluidStateModelSet fluidStateModelSet = modelManager.getFluidStateModelSet();
        FluidModel fluidModel = fluidStateModelSet.get(fluid.defaultFluidState());
        FluidTintSource tintSource = fluidModel.fluidTintSource();
        if (tintSource == null) {
            return 0xFFFFFFFF;
        }
        return tintSource.colorAsStack(ingredient);
    }


    public static Optional<TextureAtlasSprite> getStillFluidSprite(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        Minecraft minecraft = Minecraft.getInstance();
        ModelManager modelManager = minecraft.getModelManager();
        FluidStateModelSet fluidStateModelSet = modelManager.getFluidStateModelSet();
        FluidModel fluidModel = fluidStateModelSet.get(fluid.defaultFluidState());
        Material.Baked stillMaterial = fluidModel.stillMaterial();
        TextureAtlasSprite sprite = stillMaterial.sprite();
        // noinspection OptionalOfNullableMisuse
        return Optional.ofNullable(sprite)
                .filter(s -> s.atlasLocation() != MissingTextureAtlasSprite.getLocation());
    }


    private static void drawTiledSprite(GuiGraphicsExtractor guiGraphics, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite, int posX, int posY) {
        SpriteContents spriteContents = sprite.contents();
        GuiSpriteScaling.Tile tileScaling = new GuiSpriteScaling.Tile(spriteContents.width(), spriteContents.height());

        posY = posY + tiledHeight - scaledAmount;

        guiGraphics.enableScissor(posX, posY, posX + tiledWidth, posY + scaledAmount);
        {
            blitTiledSprite(
                    guiGraphics,
                    RenderPipelines.GUI_TEXTURED,
                    sprite,
                    tileScaling,
                    posX,
                    posY,
                    tiledWidth,
                    scaledAmount,
                    color
            );
        }
        guiGraphics.disableScissor();
    }

    public static void blitTiledSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, TextureAtlasSprite sprite, GuiSpriteScaling.Tile scaling, int xOffset, int yOffset, int width, int height, int color) {
        guiGraphics.blitTiledSprite(
                renderPipeline,
                sprite,
                xOffset,
                yOffset,
                width,
                height,
                0,
                0,
                scaling.width(),
                scaling.height(),
                scaling.width(),
                scaling.height(),
                color
        );
    }
}
