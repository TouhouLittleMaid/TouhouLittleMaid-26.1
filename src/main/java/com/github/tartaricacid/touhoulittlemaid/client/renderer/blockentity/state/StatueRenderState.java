package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityStatue;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.Nullable;

public class StatueRenderState extends BlockEntityRenderState {
    public boolean isCoreBlock;
    public Direction facing = Direction.NORTH;
    public float size;
    public BlockEntityStatue.Size statueSize;
    public @Nullable CompoundTag extraMaidData;
    public @Nullable EntityRenderState entityRenderState;
}
