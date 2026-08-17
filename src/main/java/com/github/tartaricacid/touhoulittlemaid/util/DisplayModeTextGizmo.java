package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.api.mixin.IDrawableGizmoPrimitivesMixin;
import net.minecraft.client.gui.Font;
import net.minecraft.gizmos.Gizmo;
import net.minecraft.gizmos.GizmoPrimitives;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

/**
 * 基于TextGizmo的扩展。
 * <p>
 * 能接收DisplayMode参数，用于解决{@link Gizmos#billboardText(String, Vec3, TextGizmo.Style)}
 * 在渲染文本时硬编码{@link Font.DisplayMode#NORMAL}可能出现的渲染问题。
 */
public record DisplayModeTextGizmo(Vec3 pos, String text, TextGizmo.Style style,
                                   Font.DisplayMode displayMode) implements Gizmo {
    @Override
    public void emit(final GizmoPrimitives primitives, final float alphaMultiplier) {
        TextGizmo.Style newStyle;
        if (alphaMultiplier < 1.0F) {
            newStyle = new TextGizmo.Style(ARGB.multiplyAlpha(style.color(), alphaMultiplier), style.scale(), style.adjustLeft());
        } else {
            newStyle = style;
        }

        if (primitives instanceof IDrawableGizmoPrimitivesMixin iDrawable) {
            iDrawable.tlm$addTextWithDisplayMode(pos, text, newStyle, displayMode);
        } else {
            primitives.addText(pos, text, newStyle);
        }
    }
}
