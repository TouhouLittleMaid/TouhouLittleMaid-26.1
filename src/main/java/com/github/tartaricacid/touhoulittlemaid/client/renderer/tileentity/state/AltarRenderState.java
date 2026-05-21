package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class AltarRenderState extends BlockEntityRenderState {
    public boolean renderModel;
    public Direction direction = Direction.NORTH;
    public boolean canPlaceItem;
    public boolean hasItem;
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
}