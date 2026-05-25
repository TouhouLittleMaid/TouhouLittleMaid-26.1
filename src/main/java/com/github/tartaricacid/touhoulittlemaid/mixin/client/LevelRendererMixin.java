package com.github.tartaricacid.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.GeckoUpdateManager;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderContextManager;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(at = @At(value = "HEAD"), method = "extractLevel(Lnet/minecraft/client/DeltaTracker;Lnet/minecraft/client/Camera;F)V")
    private void beforeExtractLevel(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
        RenderContextManager.setRenderingLevel(true);
    }

    @Inject(at = @At(value = "RETURN"), method = "extractLevel(Lnet/minecraft/client/DeltaTracker;Lnet/minecraft/client/Camera;F)V")
    private void afterExtractLevel(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
        GeckoUpdateManager.updateRemaining(deltaTracker.getGameTimeDeltaPartialTick(true));
        RenderContextManager.setRenderingLevel(false);
    }

    @Inject(at = @At(value = "RETURN"), method = "renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V")
    private void afterRenderLevel(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci) {
        GeckoUpdateManager.finalizeFrame();
    }
}
