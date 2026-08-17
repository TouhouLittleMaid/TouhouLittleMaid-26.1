package com.github.tartaricacid.touhoulittlemaid.api.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.phys.Vec3;

public interface IDrawableGizmoPrimitivesMixin {
    void tlm$addTextWithDisplayMode(Vec3 pos, String text, TextGizmo.Style style, Font.DisplayMode displayMode);
}
