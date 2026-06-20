package com.github.tartaricacid.touhoulittlemaid.entity.backpack;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.MaidBackpackRenderData;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.AbstractMaidContainer;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.backpack.EmptyBackpackContainer;
import com.github.tartaricacid.touhoulittlemaid.item.BackpackLevel;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class EmptyBackpack extends IMaidBackpack {
    public static final Identifier ID = IdentifierUtil.modLoc("empty");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Item getItem() {
        return Items.AIR;
    }

    @Override
    public int getAvailableMaxContainerIndex() {
        return BackpackLevel.EMPTY_CAPACITY;
    }

    @Override
    public MaidBackpackRenderData getRenderData() {
        return MaidBackpackRenderData.EMPTY;
    }

    @Override
    public MenuProvider getGuiProvider(int entityId) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("Maid Empty Container");
            }

            @Override
            public AbstractMaidContainer createMenu(int index, Inventory playerInventory, Player player) {
                return new EmptyBackpackContainer(index, playerInventory, entityId);
            }

            @Override
            public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                return false;
            }
        };
    }
}
