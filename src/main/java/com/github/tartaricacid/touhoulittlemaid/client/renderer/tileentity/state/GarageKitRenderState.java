package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.Nullable;

public class GarageKitRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public @Nullable CompoundTag extraData;
    public @Nullable EntityRenderState entityRenderState;
}
