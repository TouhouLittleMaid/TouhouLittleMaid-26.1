package com.github.tartaricacid.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.api.mixin.IDrawableGizmoPrimitives$TextMixin;
import com.github.tartaricacid.touhoulittlemaid.api.mixin.IDrawableGizmoPrimitivesMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(DrawableGizmoPrimitives.class)
public abstract class DrawableGizmoPrimitivesMixin implements IDrawableGizmoPrimitivesMixin {
    @Shadow
    public abstract void addText(Vec3 pos, String text, TextGizmo.Style style);

    @Unique
    private final ThreadLocal<DrawableGizmoPrimitives.Text> tlm$cachedText = new ThreadLocal<>();

    @WrapOperation(method = "addText", at = @At(remap = false, value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private <E> boolean tlm$addText(List<E> instance, E text, Operation<Boolean> original) {
        boolean result = original.call(instance, text);
        tlm$cachedText.set((DrawableGizmoPrimitives.Text) text);
        return result;
    }

    @Override
    public void tlm$addTextWithDisplayMode(Vec3 pos, String text, TextGizmo.Style style, Font.DisplayMode displayMode) {
        this.addText(pos, text, style);
        DrawableGizmoPrimitives.Text t = tlm$cachedText.get();
        if (t != null) {
            ((IDrawableGizmoPrimitives$TextMixin) (Object) t).tlm$setDisplayMode(displayMode);
        }
    }
}
