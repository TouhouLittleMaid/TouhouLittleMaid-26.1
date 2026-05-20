package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IChestType;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.inventory.chest.ChestManager;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.other.WirelessIOContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.*;

public class ItemWirelessIO extends Item implements MenuProvider {
    private static final int FILTER_LIST_SIZE = 9;
    private static final String TOOLTIPS_PREFIX = "§a▍ §7";

    public ItemWirelessIO(Identifier id) {
        super((new Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    public static void setMode(ItemStack stack, boolean maidToChest) {
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            stack.set(IO_MODE, maidToChest);
        }
    }

    public static boolean isMaidToChest(ItemStack stack) {
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            return Objects.requireNonNullElse(stack.get(IO_MODE), false);
        }
        return false;
    }

    public static void setFilterMode(ItemStack stack, boolean isBlacklist) {
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            stack.set(FILTER_MODE, isBlacklist);
        }
    }

    public static boolean isBlacklist(ItemStack stack) {
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            return Objects.requireNonNullElse(stack.get(FILTER_MODE), false);
        }
        return false;
    }

    public static ItemStacksResourceHandler getFilterList(HolderLookup.Provider provider, ItemStack stack) {
        WirelessIOHandler handler = new WirelessIOHandler(FILTER_LIST_SIZE);
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            List<ItemStack> itemStackList = stack.get(FILTER_LIST_TAG);
            if (itemStackList != null) {
                for (int i = 0; i < itemStackList.size() && i < FILTER_LIST_SIZE; i++)
                    handler.set(i, ItemResource.of(itemStackList.get(i)), 1);
            }
        }
        return handler;
    }

    public static void setFilterList(HolderLookup.Provider provider, ItemStack stack, ItemStacksResourceHandler itemStackHandler) {
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            List<ItemStack> itemStackList = new ArrayList<>();
            for (int i = 0; i < itemStackHandler.size(); i++) {
                ItemStack itemStack = itemStackHandler.getResource(i).toStack();
                itemStackList.add(itemStack);
            }
            stack.set(FILTER_LIST_TAG, itemStackList);
        }
    }

    @Nullable
    public static BlockPos getBindingPos(ItemStack stack) {
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            return stack.get(BINDING_POS);
        }
        return null;
    }

    public static void setBindingPos(ItemStack stack, BlockPos pos) {
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            stack.set(BINDING_POS, pos);
        }
    }

    public static void setSlotConfig(ItemStack stack, List<Boolean> config) {
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            stack.set(SLOT_CONFIG_TAG, config);
        }
    }

    @Nullable
    public static List<Boolean> getSlotConfig(ItemStack stack) {
        if (stack.getItem() == InitItems.WIRELESS_IO.get()) {
            return stack.get(SLOT_CONFIG_TAG);
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (hand != InteractionHand.MAIN_HAND) {
            return super.useOn(context);
        }
        if (player == null) {
            return super.useOn(context);
        }

        for (IChestType type : ChestManager.getAllChestTypes()) {
            if (!type.isChest(te)) {
                continue;
            }
            if (type.canOpenByPlayer(te, player)) {
                ItemStack stack = player.getMainHandItem();
                setBindingPos(stack, pos);
                return worldIn.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (handIn == InteractionHand.MAIN_HAND && playerIn instanceof ServerPlayer) {
            playerIn.openMenu(this, buffer -> ItemStack.STREAM_CODEC.encode(buffer, playerIn.getMainHandItem()));
            return worldIn.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        boolean maidToChest = isMaidToChest(stack);
        boolean isBlacklist = isBlacklist(stack);
        BlockPos pos = getBindingPos(stack);

        String ioModeText = maidToChest ?
                I18n.get("tooltips.touhou_little_maid.wireless_io.io_mode.input") :
                I18n.get("tooltips.touhou_little_maid.wireless_io.io_mode.output");
        String filterModeText = isBlacklist ?
                I18n.get("tooltips.touhou_little_maid.wireless_io.filter_mode.blacklist") :
                I18n.get("tooltips.touhou_little_maid.wireless_io.filter_mode.whitelist");
        String hasPos = (pos != null) ?
                I18n.get("tooltips.touhou_little_maid.wireless_io.binding_pos.has",
                        pos.getX(), pos.getY(), pos.getZ()) :
                I18n.get("tooltips.touhou_little_maid.wireless_io.binding_pos.none");

        builder.accept(Component.literal(TOOLTIPS_PREFIX + ioModeText));
        builder.accept(Component.literal(TOOLTIPS_PREFIX + filterModeText));
        builder.accept(Component.literal(TOOLTIPS_PREFIX + hasPos));
        builder.accept(Component.literal(" "));
        builder.accept(Component.translatable("tooltips.touhou_little_maid.wireless_io.usage.1").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("tooltips.touhou_little_maid.wireless_io.usage.2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("item.touhou_little_maid.wireless_io");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WirelessIOContainer(id, inventory, player.getMainHandItem());
    }

    private static class WirelessIOHandler extends ItemStacksResourceHandler {
        private WirelessIOHandler(int size) {
            super(size);
        }

        @Override
        protected int getCapacity(int index, ItemResource resource) {
            return 1;
        }
    }
}
