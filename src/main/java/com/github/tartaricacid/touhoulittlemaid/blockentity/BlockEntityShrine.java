package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.ShrineItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class BlockEntityShrine extends BlockEntityBase {
    private static final String STORAGE_ITEM = "StorageItem";
    private final ItemStacksResourceHandler handler = new ShrineItemHandler();

    public BlockEntityShrine(BlockPos pos, BlockState blockState) {
        super(InitBlocks.SHRINE_BE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(STORAGE_ITEM, handler);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.readChild(STORAGE_ITEM, handler);
    }

    public ItemStack getStorageItem() {
        return ItemUtil.getStack(handler, 0);
    }

    public void insertStorageItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        try (Transaction tx = Transaction.openRoot()) {
            int insert = handler.insert(ItemResource.of(stack), stack.count(), tx);
            if (insert > 0) {
                tx.commit();
                this.refresh();
            }
        }
    }

    public ItemStack extractStorageItem() {
        try (Transaction tx = Transaction.openRoot()) {
            ItemResource resource = handler.getResource(0);
            int extract = handler.extract(0, resource, 1, tx);
            if (extract > 0) {
                tx.commit();
                this.refresh();
                return resource.toStack(extract);
            } else {
                return ItemStack.EMPTY;
            }
        }
    }

    public boolean isEmpty() {
        return handler.getResource(0).isEmpty();
    }

    public boolean canInsert(ItemStack stack) {
        return handler.isValid(0, ItemResource.of(stack));
    }
}
