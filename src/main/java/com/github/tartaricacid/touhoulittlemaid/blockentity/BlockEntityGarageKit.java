package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

import static net.minecraft.world.item.component.CustomData.COMPOUND_TAG_CODEC;

public class BlockEntityGarageKit extends BlockEntity {
    private static final String FACING_TAG = "GarageKitFacing";
    private static final String EXTRA_DATA = "ExtraData";
    private Direction facing = Direction.NORTH;
    private CompoundTag extraData = new CompoundTag();

    public BlockEntityGarageKit(BlockPos blockPos, BlockState blockState) {
        super(InitBlocks.GARAGE_KIT_BE.get(), blockPos, blockState);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        output.store(FACING_TAG, Direction.CODEC, facing);
        output.store(EXTRA_DATA, COMPOUND_TAG_CODEC, extraData);
        super.saveAdditional(output);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        facing = input.read(FACING_TAG, Direction.CODEC).orElse(Direction.NORTH);
        extraData = input.read(EXTRA_DATA, COMPOUND_TAG_CODEC).orElseGet(CompoundTag::new);
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

    public Direction getFacing() {
        return facing;
    }

    public CompoundTag getExtraData() {
        return extraData;
    }

    public void setData(Direction facing, CompoundTag extraData) {
        this.facing = facing;
        this.extraData = extraData;
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }
}
