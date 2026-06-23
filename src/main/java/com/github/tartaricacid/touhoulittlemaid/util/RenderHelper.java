package com.github.tartaricacid.touhoulittlemaid.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public final class RenderHelper {
    public static AABB getAABB(BlockPos pStart, BlockPos pEnd) {
        return new AABB(pStart.getX(), pStart.getY(), pStart.getZ(), pEnd.getX(), pEnd.getY(), pEnd.getZ());
    }
}
