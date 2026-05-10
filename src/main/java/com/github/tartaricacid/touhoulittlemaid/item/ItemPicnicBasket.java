package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.PicnicBasketRender;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.other.PicnicBasketContainer;
import com.github.tartaricacid.touhoulittlemaid.inventory.tooltip.ItemContainerTooltip;
import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ItemPicnicBasket extends BlockItem implements MenuProvider {
    public static final IClientItemExtensions ITEM_EXTENSIONS = FMLEnvironment.dist == Dist.CLIENT ? new IClientItemExtensions() {
        private static final Supplier<PicnicBasketRender> MEMOIZE = Suppliers.memoize(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            return new PicnicBasketRender(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        });

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return MEMOIZE.get();
        }
    } : null;
    private static final int PICNIC_BASKET_SIZE = 9;

    public ItemPicnicBasket(Block block) {
        super(block, (new Properties()).stacksTo(1));
    }

    public static ItemStacksResourceHandler getContainer(ItemStack stack) {
        var handler = new ItemStacksResourceHandler(PICNIC_BASKET_SIZE);
        if (stack.getItem() == InitItems.PICNIC_BASKET.get()) {
            ItemContainerContents container = stack.get(DataComponents.CONTAINER);
            if (container != null) {
                assert container.getSlots() <= PICNIC_BASKET_SIZE;
                for (int i = 0; i < container.getSlots(); i++) {
                    ItemStack itemStack = container.getStackInSlot(i);
                    handler.set(i, ItemResource.of(itemStack), itemStack.getCount());
                }
            }
        }
        return handler;
    }

    public static void setContainer(ItemStack stack, ItemStacksResourceHandler itemStackHandler) {
        if (stack.getItem() == InitItems.PICNIC_BASKET.get()) {
            NonNullList<ItemStack> items = NonNullList.withSize(PICNIC_BASKET_SIZE, ItemStack.EMPTY);
            for (int i = 0; i < itemStackHandler.size(); i++) {
                items.set(i, ItemUtil.getStack(itemStackHandler, i));
            }
            ItemContainerContents container = ItemContainerContents.fromItems(items);
            stack.set(DataComponents.CONTAINER, container);
        }
    }

    @Override
    public InteractionResult<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (handIn == InteractionHand.MAIN_HAND && playerIn instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(this, data -> ItemStack.STREAM_CODEC.encode(data, serverPlayer.getMainHandItem()));
            return InteractionResult.success(playerIn.getMainHandItem());
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        var container = getContainer(stack);
        return Optional.of(new ItemContainerTooltip(container));
    }

    @Override
    public String getDescriptionId() {
        return "item.touhou_little_maid.picnic_basket";
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PicnicBasketContainer(containerId, playerInventory, player.getMainHandItem());
    }
}
