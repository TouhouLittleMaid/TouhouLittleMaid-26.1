package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class ShrineRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean hasItem;
    public float itemRotation;
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
}
