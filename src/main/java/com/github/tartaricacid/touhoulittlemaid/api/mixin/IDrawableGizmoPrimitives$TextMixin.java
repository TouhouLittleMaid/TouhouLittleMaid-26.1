package com.github.tartaricacid.touhoulittlemaid.api.mixin;

import net.minecraft.client.gui.Font;

import javax.annotation.Nullable;

public interface IDrawableGizmoPrimitives$TextMixin {
    void tlm$setDisplayMode(Font.DisplayMode displayMode);

    @Nullable
    Font.DisplayMode tlm$getDisplayMode();
}
