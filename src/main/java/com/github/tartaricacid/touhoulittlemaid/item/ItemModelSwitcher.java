package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityModelSwitcher;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.storage.TagValueInput;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.STORAGE_DATA_TAG;

public class ItemModelSwitcher extends BlockItem {
    public ItemModelSwitcher(Identifier id) {
        super(InitBlocks.MODEL_SWITCHER.get(), (new Item.Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    public static ItemStack tileEntityToItemStack(HolderLookup.Provider provider, TileEntityModelSwitcher switcher) {
        ItemStack itemStack = Objects.requireNonNull(InitItems.MODEL_SWITCHER.get().getDefaultInstance());
        itemStack.set(STORAGE_DATA_TAG, switcher.saveWithoutMetadata(provider));
        return itemStack;
    }

    public static void itemStackToTileEntity(HolderLookup.Provider provider, ItemStack stack, TileEntityModelSwitcher switcher) {
        CompoundTag tag = stack.get(STORAGE_DATA_TAG);
        if (tag != null && !tag.isEmpty()) {
            switcher.loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, provider, tag));
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof EntityMaid maid) {
            CompoundTag tag = stack.getOrDefault(STORAGE_DATA_TAG, new CompoundTag());
            tag.store(TileEntityModelSwitcher.ENTITY_UUID, UUIDUtil.CODEC, maid.getUUID());
            stack.set(STORAGE_DATA_TAG, tag);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, pPlayer, pInteractionTarget, pUsedHand);
    }

    private boolean hasMaidInfo(ItemStack stack) {
        CompoundTag tag = stack.get(STORAGE_DATA_TAG);
        if (tag != null && !tag.isEmpty()) {
            return tag.read(TileEntityModelSwitcher.ENTITY_UUID, UUIDUtil.CODEC).isPresent();
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Item.TooltipContext pLevel, TooltipDisplay display, Consumer<Component> pTooltip, TooltipFlag pFlag) {
        if (hasMaidInfo(pStack)) {
            pTooltip.accept(Component.translatable("tooltips.touhou_little_maid.model_switcher.bounded").withStyle(ChatFormatting.GRAY));
        } else {
            pTooltip.accept(Component.translatable("gui.touhou_little_maid.model_switcher.uuid.empty").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
