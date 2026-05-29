package com.github.tartaricacid.touhoulittlemaid.api.backpack;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityTombstone;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.item.BackpackLevel;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;


public abstract class IMaidBackpack {
    public abstract Identifier getId();

    public abstract Item getItem();

    public abstract void onPutOn(ItemStack stack, Player player, EntityMaid maid);

    public ItemStack getTakeOffItemStack(ItemStack stack, @Nullable Player player, EntityMaid maid) {
        return this.getItem().getDefaultInstance();
    }

    public abstract void onTakeOff(ItemStack stack, Player player, EntityMaid maid);

    public abstract void onSpawnTombstone(EntityMaid maid, EntityTombstone tombstone);

    public abstract MenuProvider getGuiProvider(int entityId);

    public abstract int getAvailableMaxContainerIndex();

    public abstract void offsetBackpackItem(PoseStack poseStack);

    @Nullable
    public abstract EntityModel<EntityMaidRenderState> getBackpackModel(EntityModelSet modelSet);

    @Nullable
    public abstract Identifier getBackpackTexture();

    protected final void dropAllItems(EntityMaid maid) {
        ItemsUtil.dropEntityItems(maid, maid.components.item.getMaidInv(), BackpackLevel.EMPTY_CAPACITY, null);
    }

    protected final void dropRelativeItems(ItemStack stack, EntityMaid maid) {
        BackpackManager.findBackpack(stack).ifPresentOrElse(backpack -> {
            int startIndex = backpack.getAvailableMaxContainerIndex();
            ItemsUtil.dropEntityItems(maid, maid.components.item.getMaidInv(), startIndex, null);
        }, () -> this.dropAllItems(maid));
    }
}
