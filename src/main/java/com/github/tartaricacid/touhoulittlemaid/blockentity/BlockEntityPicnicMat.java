package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.PicnicMatItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import java.util.Arrays;
import java.util.UUID;

public class BlockEntityPicnicMat extends BlockEntityBase {
    private static final String CENTER_POS = "CenterPos";
    private static final String STORAGE_ITEM = "StorageItem";
    private static final String SIT_IDS = "SitIds";

    private final ItemStacksResourceHandler handler = new PicnicMatItemHandler();
    private final UUID[] sitIds = new UUID[]{
            Util.NIL_UUID, Util.NIL_UUID,
            Util.NIL_UUID, Util.NIL_UUID
    };
    private BlockPos centerPos = BlockPos.ZERO;

    public BlockEntityPicnicMat(BlockPos pos, BlockState blockState) {
        super(InitBlocks.PICNIC_MAT_BE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(CENTER_POS, BlockPos.CODEC, centerPos);
        output.putChild(STORAGE_ITEM, handler);
        output.store(SIT_IDS, UUIDUtil.CODEC.listOf(), Arrays.asList(sitIds));
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        centerPos = input.read(CENTER_POS, BlockPos.CODEC).orElse(BlockPos.ZERO);
        input.readChild(STORAGE_ITEM, this.handler);

        Arrays.fill(this.sitIds, Util.NIL_UUID);
        input.read(SIT_IDS, UUIDUtil.CODEC.listOf()).ifPresent(ids -> {
            for (int i = 0; i < ids.size() && i < this.sitIds.length; i++) {
                this.sitIds[i] = ids.get(i);
            }
        });
    }

    public void setCenterPos(BlockPos centerPos) {
        this.centerPos = centerPos;
        this.refresh();
    }

    public void setSitId(int index, UUID uuid) {
        if (index < 0 || index >= 4) {
            return;
        }
        this.sitIds[index] = uuid;
        this.refresh();
    }

    public UUID[] getSitIds() {
        return sitIds;
    }

    public BlockPos getCenterPos() {
        return centerPos;
    }

    public ItemStack getStorageItem(int slotId) {
        return ItemUtil.getStack(handler, slotId);
    }

    public boolean isEmpty(int slotId) {
        return handler.getResource(slotId).isEmpty();
    }

    public void setHandler(ItemStacksResourceHandler stackHandler) {
        ResourceHandlerUtil.move(
                stackHandler, this.handler, _ -> true,
                Integer.MAX_VALUE, null
        );
        this.refresh();
    }

    public ItemStacksResourceHandler getHandler() {
        return handler;
    }
}