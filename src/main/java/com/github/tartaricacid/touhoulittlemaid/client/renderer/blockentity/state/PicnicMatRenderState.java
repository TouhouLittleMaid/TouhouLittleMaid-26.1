package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class PicnicMatRenderState extends BlockEntityRenderState {
    public boolean isCenter;
    public Direction facing = Direction.NORTH;
    public ItemStack[] slotItems = new ItemStack[9];
}
