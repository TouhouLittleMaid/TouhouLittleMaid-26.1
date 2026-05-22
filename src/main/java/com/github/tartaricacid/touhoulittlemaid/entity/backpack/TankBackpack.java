package com.github.tartaricacid.touhoulittlemaid.entity.backpack;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.IBackpackData;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.data.TankBackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityTombstone;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.AbstractMaidContainer;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.backpack.TankBackpackContainer;
import com.github.tartaricacid.touhoulittlemaid.item.BackpackLevel;
import com.github.tartaricacid.touhoulittlemaid.item.ItemTankBackpack;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

import javax.annotation.Nullable;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.BedrockModelLoader.TANK_BACKPACK;

public class TankBackpack extends IMaidBackpack {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "tank");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Item getItem() {
        return InitItems.TANK_BACKPACK.get();
    }

    @Override
    public void onPutOn(ItemStack stack, Player player, EntityMaid maid) {
        IBackpackData backpackData = maid.getBackpackData();
        if (backpackData instanceof TankBackpackData tankBackpackData) {
            ItemTankBackpack.setTankBackpack(maid, tankBackpackData, stack);
        }
    }

    @Override
    public void onTakeOff(ItemStack stack, Player player, EntityMaid maid) {
        IBackpackData backpackData = maid.getBackpackData();
        if (backpackData instanceof TankBackpackData tankBackpackData) {
            ResourceHandler<ItemResource> inv = VanillaContainerWrapper.of(tankBackpackData);
            ItemsUtil.dropEntityItems(maid, inv, null);
        }
        dropRelativeItems(stack, maid);
    }

    @Override
    public ItemStack getTakeOffItemStack(ItemStack stack, @Nullable Player player, EntityMaid maid) {
        IBackpackData backpackData = maid.getBackpackData();
        if (backpackData instanceof TankBackpackData tankBackpackData) {
            return ItemTankBackpack.getTankBackpack(maid.registryAccess(), tankBackpackData);
        }
        return super.getTakeOffItemStack(stack, player, maid);
    }

    @Override
    public void onSpawnTombstone(EntityMaid maid, EntityTombstone tombstone) {
        IBackpackData backpackData = maid.getBackpackData();
        if (backpackData instanceof TankBackpackData tankBackpackData) {
            for (int i = 0; i < tankBackpackData.getContainerSize(); i++) {
                tombstone.insertItem(tankBackpackData.getItem(i).copy());
            }
        }
    }

    @Override
    public boolean hasBackpackData() {
        return true;
    }

    @Nullable
    @Override
    public IBackpackData getBackpackData(EntityMaid maid) {
        return new TankBackpackData(maid);
    }

    @Override
    public MenuProvider getGuiProvider(int entityId) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("Maid Tank Container");
            }

            @Override
            public AbstractMaidContainer createMenu(int index, Inventory playerInventory, Player player) {
                return new TankBackpackContainer(index, playerInventory, entityId);
            }

            @Override
            public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                return false;
            }
        };
    }

    @Override
    public int getAvailableMaxContainerIndex() {
        return BackpackLevel.TANK_CAPACITY;
    }

    @Nullable
    @Override
    //FIXME 等待EntityMaidRenderState的实现
    public EntityModel<EntityMaidRenderState> getBackpackModel(EntityModelSet modelSet) {
        return BedrockModelLoader.getEntityModel(TANK_BACKPACK);
    }

    @Nullable
    @Override
    public Identifier getBackpackTexture() {
        return Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/entity/backpack/tank_backpack.png");
    }

    @Override
    public void offsetBackpackItem(PoseStack poseStack) {
        poseStack.mulPose(Axis.XP.rotationDegrees(-7.5F));
        poseStack.translate(0, 0.625, -0.25);
    }
}
