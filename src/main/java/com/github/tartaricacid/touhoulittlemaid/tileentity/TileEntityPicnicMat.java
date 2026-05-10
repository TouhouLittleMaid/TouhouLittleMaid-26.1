package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityPicnicMat extends BlockEntity {
    public static final BlockEntityType<TileEntityPicnicMat> TYPE = new BlockEntityType<>(TileEntityPicnicMat::new, InitBlocks.PICNIC_MAT.get());
    private static final String CENTER_POS_NAME = "CenterPos";
    private static final String STORAGE_ITEM = "StorageItem";
    private static final String SIT_IDS = "SitIds";
    private final ItemStacksResourceHandler handler = new ItemStacksResourceHandler(9) {
        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (!resource.toStack().has(DataComponents.FOOD))
                return 0;
            return super.insert(index, resource, amount, transaction);
        }
    };
    private final UUID[] sitIds = new UUID[]{Util.NIL_UUID, Util.NIL_UUID, Util.NIL_UUID, Util.NIL_UUID};
    private BlockPos centerPos = BlockPos.ZERO;

    public TileEntityPicnicMat(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
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
        return handler.getResource(slotId).toStack(handler.getAmountAsInt(slotId));
    }

    public boolean isEmpty(int slotId) {
        return handler.getResource(slotId).isEmpty();
    }

    public void setHandler(ItemStacksResourceHandler stackHandler) {
        for (int i = 0; i < stackHandler.getSlots(); i++) {
            ItemStack stack = stackHandler.getStackInSlot(i);
            if (i >= this.handler.getSlots()) {
                return;
            }
            this.handler.setStackInSlot(i, stack);
        }
        this.refresh();
    }

    public ItemStacksResourceHandler getHandler() {
        return handler;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        getPersistentData().put(CENTER_POS_NAME, NbtUtils.writeBlockPos(centerPos));
        getPersistentData().put(STORAGE_ITEM, handler.serializeNBT(pRegistries));
        ListTag listTag = new ListTag();
        for (UUID uuid : sitIds) {
            listTag.add(NbtUtils.createUUID(uuid));
        }
        getPersistentData().put(SIT_IDS, listTag);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(pTag, pRegistries);
        NbtUtils.readBlockPos(getPersistentData(), CENTER_POS_NAME).ifPresent(pos -> centerPos = pos);
        this.handler.deserializeNBT(pRegistries, getPersistentData().getCompound(STORAGE_ITEM));
        ListTag sitIdsTag = getPersistentData().getList(SIT_IDS, Tag.TAG_INT_ARRAY);
        int i = 0;
        for (Tag tag : sitIdsTag) {
            this.sitIds[i] = NbtUtils.loadUUID(tag);
            i = i + 1;
            if (i >= 4) {
                break;
            }
        }
    }

    public void refresh() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
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

    public BlockPos getWorldPosition() {
        return this.worldPosition;
    }
}
