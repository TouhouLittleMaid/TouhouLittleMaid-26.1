package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.AltarItemHandler;
import com.github.tartaricacid.touhoulittlemaid.util.PosListData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import javax.annotation.Nullable;

public class TileEntityAltar extends BlockEntity {
    private static final String STORAGE_ITEM = "StorageItem";
    private static final String IS_RENDER = "IsRender";
    private static final String CAN_PLACE_ITEM = "CanPlaceItem";
    private static final String STORAGE_STATE_ID = "StorageBlockStateId";
    private static final String DIRECTION = "Direction";
    private static final String STORAGE_BLOCK_LIST = "StorageBlockList";
    private static final String CAN_PLACE_ITEM_POS_LIST = "CanPlaceItemPosList";

    public final ItemStacksResourceHandler handler = new AltarItemHandler();

    private boolean isRender = false;
    private boolean canPlaceItem = false;
    private BlockState storageState = Blocks.AIR.defaultBlockState();
    private PosListData blockPosList = new PosListData();
    private PosListData canPlaceItemPosList = new PosListData();
    private Direction direction = Direction.SOUTH;

    public TileEntityAltar(BlockPos blockPos, BlockState blockState) {
        super(InitBlocks.ALTAR_TE.get(), blockPos, blockState);
    }

    public void setData(BlockState storageState, boolean isRender, boolean canPlaceItem, Direction direction,
                        PosListData blockPosList, PosListData canPlaceItemPosList) {
        this.isRender = isRender;
        this.canPlaceItem = canPlaceItem;
        this.storageState = storageState;
        this.direction = direction;
        this.blockPosList = blockPosList;
        this.canPlaceItemPosList = canPlaceItemPosList;
        refresh();
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean(IS_RENDER, isRender);
        output.putBoolean(CAN_PLACE_ITEM, canPlaceItem);
        output.putInt(STORAGE_STATE_ID, Block.getId(storageState));
        output.putChild(STORAGE_ITEM, handler);
        output.store(DIRECTION, Direction.CODEC, direction);
        output.putChild(STORAGE_BLOCK_LIST, blockPosList);
        output.putChild(CAN_PLACE_ITEM_POS_LIST, canPlaceItemPosList);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        isRender = input.getBooleanOr(IS_RENDER, false);
        canPlaceItem = input.getBooleanOr(CAN_PLACE_ITEM, false);
        storageState = Block.stateById(input.getIntOr(STORAGE_STATE_ID, Block.getId(Blocks.AIR.defaultBlockState())));
        input.readChild(STORAGE_ITEM, handler);
        direction = input.read(DIRECTION, Direction.CODEC).orElse(Direction.SOUTH);
        input.readChild(STORAGE_BLOCK_LIST, blockPosList);
        input.readChild(CAN_PLACE_ITEM_POS_LIST, canPlaceItemPosList);
    }

    public BlockPos getWorldPosition() {
        return this.worldPosition;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return this.saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void refresh() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        ItemStack stack = ItemUtil.getStack(handler, 0);
        if (!stack.isEmpty() && this.level != null) {
            Block.popResource(this.level, pos.offset(0, 1, 0), stack);
        }
    }

    public boolean isRender() {
        return isRender;
    }

    public boolean isCanPlaceItem() {
        return canPlaceItem;
    }

    public BlockState getStorageState() {
        return storageState;
    }

    public PosListData getBlockPosList() {
        return blockPosList;
    }

    public PosListData getCanPlaceItemPosList() {
        return canPlaceItemPosList;
    }

    public ItemStack getStorageItem() {
        if (canPlaceItem) {
            return ItemUtil.getStack(handler, 0);
        }
        return ItemStack.EMPTY;
    }

    public Direction getDirection() {
        return direction;
    }
}
