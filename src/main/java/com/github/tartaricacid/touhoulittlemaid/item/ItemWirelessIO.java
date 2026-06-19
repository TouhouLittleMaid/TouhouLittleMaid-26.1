package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.other.WirelessIOContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.*;

@SuppressWarnings("deprecation")
public class ItemWirelessIO extends Item implements MenuProvider {
    private static final String TOOLTIPS_PREFIX = "§a▍ §7";

    public ItemWirelessIO(Identifier id) {
        super(new Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    public static void setMode(ItemStack stack, boolean maidToChest) {
        if (stack.is(InitItems.WIRELESS_IO)) {
            stack.set(IO_MODE, maidToChest);
        }
    }

    public static boolean isMaidToChest(ItemStack stack) {
        if (stack.is(InitItems.WIRELESS_IO)) {
            return stack.getOrDefault(IO_MODE, false);
        }
        return false;
    }

    public static void setFilterMode(ItemStack stack, boolean isBlacklist) {
        if (stack.is(InitItems.WIRELESS_IO)) {
            stack.set(FILTER_MODE, isBlacklist);
        }
    }

    public static boolean isBlacklist(ItemStack stack) {
        if (stack.is(InitItems.WIRELESS_IO)) {
            return stack.getOrDefault(FILTER_MODE, false);
        }
        return false;
    }

    @Nullable
    public static BlockPos getBindingPos(ItemStack stack) {
        if (stack.is(InitItems.WIRELESS_IO)) {
            return stack.get(BINDING_POS);
        }
        return null;
    }

    public static void setBindingPos(ItemStack stack, BlockPos pos) {
        if (stack.is(InitItems.WIRELESS_IO)) {
            stack.set(BINDING_POS, pos);
        }
    }

    public static void setSlotConfig(ItemStack stack, List<Boolean> config) {
        if (stack.is(InitItems.WIRELESS_IO)) {
            stack.set(SLOT_CONFIG_TAG, config);
        }
    }

    @Nullable
    public static List<Boolean> getSlotConfig(ItemStack stack) {
        if (stack.is(InitItems.WIRELESS_IO)) {
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

        // 如果是 BaseContainerBlockEntity 需要检查权限
        if (te instanceof BaseContainerBlockEntity baseContainer && !baseContainer.canOpen(player)) {
            return super.useOn(context);
        }

        // 依据输入输出，选择不同的朝向
        ItemStack stack = player.getMainHandItem();
        boolean isMaidToChest = ItemWirelessIO.isMaidToChest(stack);
        Direction side = isMaidToChest ? Direction.UP : Direction.DOWN;

        // 最后检查有无 cap
        var capability = worldIn.getCapability(Capabilities.Item.BLOCK, pos, side);
        if (capability != null) {
            setBindingPos(stack, pos);
            return worldIn.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (handIn == InteractionHand.MAIN_HAND && playerIn instanceof ServerPlayer) {
            playerIn.openMenu(this);
            return worldIn.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag tooltipFlag) {
        boolean maidToChest = isMaidToChest(stack);
        boolean isBlacklist = isBlacklist(stack);
        BlockPos pos = getBindingPos(stack);

        MutableComponent ioModeText = maidToChest ?
                Component.translatable("tooltips.touhou_little_maid.wireless_io.io_mode.input") :
                Component.translatable("tooltips.touhou_little_maid.wireless_io.io_mode.output");

        MutableComponent filterModeText = isBlacklist ?
                Component.translatable("tooltips.touhou_little_maid.wireless_io.filter_mode.blacklist") :
                Component.translatable("tooltips.touhou_little_maid.wireless_io.filter_mode.whitelist");

        MutableComponent hasPos = (pos != null) ?
                Component.translatable("tooltips.touhou_little_maid.wireless_io.binding_pos.has", pos.getX(), pos.getY(), pos.getZ()) :
                Component.translatable("tooltips.touhou_little_maid.wireless_io.binding_pos.none");

        builder.accept(Component.literal(TOOLTIPS_PREFIX).append(ioModeText));
        builder.accept(Component.literal(TOOLTIPS_PREFIX).append(filterModeText));
        builder.accept(Component.literal(TOOLTIPS_PREFIX).append(hasPos));
        builder.accept(CommonComponents.space());
        builder.accept(Component.translatable("tooltips.touhou_little_maid.wireless_io.usage.1").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("tooltips.touhou_little_maid.wireless_io.usage.2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WirelessIOContainer(id, inventory, null);
    }
}
