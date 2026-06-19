package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityMaidBeacon;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityMaidBeacon.STORAGE_POWER_TAG;
import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.STORAGE_DATA_TAG;
import static net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;

@SuppressWarnings("deprecation")
public class ItemMaidBeacon extends DoubleHighBlockItem {
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public ItemMaidBeacon(Identifier id) {
        super(InitBlocks.MAID_BEACON.get(), new Item.Properties()
                .stacksTo(1)
                .setId(ResourceKey.create(Registries.ITEM, id))
                .overrideDescription("block.touhou_little_maid.maid_beacon")
                .attributes(createAttributes()));
    }

    private static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 3, ADD_VALUE), MAINHAND)
                .add(ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.2F, ADD_VALUE), MAINHAND)
                .build();
    }

    public static ItemStack blockEntityToItemStack(HolderLookup.Provider provider, BlockEntityMaidBeacon beacon) {
        ItemStack stack = InitItems.MAID_BEACON.get().getDefaultInstance();
        stack.set(STORAGE_DATA_TAG, beacon.saveWithoutMetadata(provider));
        return stack;
    }

    public static void itemStackToBlockEntity(ItemStack stack, BlockEntityMaidBeacon beacon) {
        CompoundTag tag = stack.get(STORAGE_DATA_TAG);
        if (tag != null && !tag.isEmpty()) {
            beacon.loadData(tag);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext worldIn, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flagIn) {
        float numPower = 0f;
        CompoundTag tag = stack.get(STORAGE_DATA_TAG);
        if (tag != null) {
            numPower = tag.getFloatOr(STORAGE_POWER_TAG, 0f);
        }

        String format = DECIMAL_FORMAT.format(numPower);
        tooltip.accept(Component
                .translatable("tooltips.touhou_little_maid.maid_beacon.desc", format)
                .withStyle(ChatFormatting.GRAY));
    }
}
