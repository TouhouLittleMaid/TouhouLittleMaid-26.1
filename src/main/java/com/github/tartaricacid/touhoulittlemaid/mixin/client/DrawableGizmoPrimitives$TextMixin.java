package com.github.tartaricacid.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.api.mixin.IDrawableGizmoPrimitives$TextMixin;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DrawableGizmoPrimitives.Text.class)
public class DrawableGizmoPrimitives$TextMixin implements IDrawableGizmoPrimitives$TextMixin {
    @Unique
    private Font.DisplayMode tlm$displayMode;

    @Override
    public void tlm$setDisplayMode(Font.DisplayMode displayMode) {
        this.tlm$displayMode = displayMode;
    }

    @Override
    public Font.DisplayMode tlm$getDisplayMode() {
        return this.tlm$displayMode;
    }
}