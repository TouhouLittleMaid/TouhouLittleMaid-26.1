package com.github.tartaricacid.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.api.mixin.IDrawableGizmoPrimitives$TextMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives$Group")
public abstract class DrawableGizmoPrimitives$GroupMixin  {
    @WrapOperation(
            method = "renderTexts",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLorg/joml/Matrix4fc;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"
            )
    )
    private void tlm$modifyDisplayMode(
            Font instance, String str, float x, float y, int color, boolean dropShadow, Matrix4fc pose, MultiBufferSource bufferSource,
            Font.DisplayMode oriMode, int backgroundColor, int packedLightCoords,
            Operation<Void> original,
            @Local DrawableGizmoPrimitives.Text text
    ) {
        Font.DisplayMode mode = ((IDrawableGizmoPrimitives$TextMixin) (Object) text).tlm$getDisplayMode();
        original.call(instance, str, x, y, color, dropShadow, pose, bufferSource, mode == null ? oriMode : mode, backgroundColor, packedLightCoords);
    }
}
