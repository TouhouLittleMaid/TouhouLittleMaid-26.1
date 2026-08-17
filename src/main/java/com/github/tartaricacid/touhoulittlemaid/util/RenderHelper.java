package com.github.tartaricacid.touhoulittlemaid.util;

import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoProperties;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class RenderHelper {
    public static AABB getAABB(BlockPos pStart, BlockPos pEnd) {
        return new AABB(pStart.getX(), pStart.getY(), pStart.getZ(), pEnd.getX(), pEnd.getY(), pEnd.getZ());
    }

    public static GizmoProperties billboardText(String text, Vec3 pos, TextGizmo.Style style, Font.DisplayMode displayMode) {
        return Gizmos.addGizmo(new DisplayModeTextGizmo(pos, text, style, displayMode));
    }
}
