package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityModelSwitcher;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
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
import net.minecraft.world.level.storage.ValueInput;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityModelSwitcher.ENTITY_UUID;
import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.STORAGE_DATA_TAG;

@SuppressWarnings("deprecation")
public class ItemModelSwitcher extends BlockItem {
    public ItemModelSwitcher(Identifier id) {
        super(InitBlocks.MODEL_SWITCHER.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1)
                .overrideDescription("block.touhou_little_maid.model_switcher")
        );
    }

    public static ItemStack blockEntityToItemStack(HolderLookup.Provider provider, BlockEntityModelSwitcher switcher) {
        ItemStack itemStack = InitItems.MODEL_SWITCHER.get().getDefaultInstance();
        itemStack.set(STORAGE_DATA_TAG, switcher.saveWithoutMetadata(provider));
        return itemStack;
    }

    public static void itemStackToBlockEntity(HolderLookup.Provider provider, ItemStack stack,
                                              BlockEntityModelSwitcher switcher) {
        CompoundTag tag = stack.get(STORAGE_DATA_TAG);
        if (tag != null && !tag.isEmpty()) {
            ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, provider, tag);
            switcher.loadAdditional(valueInput);
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
                                                  InteractionHand hand) {
        if (target instanceof EntityMaid maid) {
            CompoundTag tag = stack.getOrDefault(STORAGE_DATA_TAG, new CompoundTag());
            tag.store(ENTITY_UUID, UUIDUtil.CODEC, maid.getUUID());
            stack.set(STORAGE_DATA_TAG, tag);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    private boolean hasMaidInfo(ItemStack stack) {
        CompoundTag tag = stack.get(STORAGE_DATA_TAG);
        if (tag != null) {
            return tag.contains(ENTITY_UUID);
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag flag) {
        if (hasMaidInfo(stack)) {
            builder.accept(Component
                    .translatable("tooltips.touhou_little_maid.model_switcher.bounded")
                    .withStyle(ChatFormatting.GRAY)
            );
        } else {
            builder.accept(Component
                    .translatable("gui.touhou_little_maid.model_switcher.uuid.empty")
                    .withStyle(ChatFormatting.DARK_RED)
            );
        }
    }
}
