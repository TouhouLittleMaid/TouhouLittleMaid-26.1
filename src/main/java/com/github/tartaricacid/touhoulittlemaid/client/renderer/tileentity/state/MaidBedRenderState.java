package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.item.DyeColor;

public class MaidBedRenderState extends BlockEntityRenderState {
    public DyeColor dyeColor = DyeColor.PINK;
    public int rotation;
}
