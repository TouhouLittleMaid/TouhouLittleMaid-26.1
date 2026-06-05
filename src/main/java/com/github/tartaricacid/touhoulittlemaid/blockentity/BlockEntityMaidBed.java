package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public class BlockEntityMaidBed extends BlockEntity {
    private static final String COLOR_TAG = "BedColor";
    private DyeColor color = DyeColor.PINK;

    public BlockEntityMaidBed(BlockPos blockPos, BlockState blockState) {
        super(InitBlocks.MAID_BED_BE.get(), blockPos, blockState);
    }

    public void setColor(DyeColor color) {
        this.color = color;
        this.refresh();
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        output.putInt(COLOR_TAG, color.getId());
        super.saveAdditional(output);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        color = DyeColor.byId(input.getIntOr(COLOR_TAG, DyeColor.PINK.getId()));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
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

    public DyeColor getColor() {
        return color;
    }
}
