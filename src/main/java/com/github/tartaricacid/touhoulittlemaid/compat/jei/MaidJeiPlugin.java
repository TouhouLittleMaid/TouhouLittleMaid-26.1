package com.github.tartaricacid.touhoulittlemaid.compat.jei;

import com.github.tartaricacid.touhoulittlemaid.client.event.ClientRecipeEvent;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.item.WirelessIOContainerGui;
import com.github.tartaricacid.touhoulittlemaid.compat.jei.category.AltarRecipeCategory;
import com.github.tartaricacid.touhoulittlemaid.compat.jei.handler.MaidContainerHandler;
import com.github.tartaricacid.touhoulittlemaid.compat.jei.handler.WirelessIOGhostHandler;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class MaidJeiPlugin implements IModPlugin {
    private static final Identifier UID = IdentifierUtil.modLoc("jei");

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(
                InitItems.GARAGE_KIT.toStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.touhou_little_maid.garage_kit.info")
        );

        registration.addRecipes(AltarRecipeCategory.TYPE, ClientRecipeEvent.ALTAR_RECIPES);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(AltarRecipeCategory.TYPE, InitItems.HAKUREI_GOHEI.get());
        registration.addCraftingStation(AltarRecipeCategory.TYPE, InitItems.SANAE_GOHEI.get());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractMaidContainerGui.class, new MaidContainerHandler());
        registration.addGhostIngredientHandler(WirelessIOContainerGui.class, new WirelessIOGhostHandler());
    }

    @Override
    public Identifier getPluginUid() {
        return UID;
    }
}

