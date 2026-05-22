package com.github.tartaricacid.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderContextManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(at = @At("HEAD"), method = "renderEntityInInventoryFollowsAngle(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V", remap = false)
    private static void beforeRenderEntityInInventoryFollowsAngle(GuiGraphicsExtractor p_282802_, int p_275688_, int p_275245_, int p_275535_, int p_294406_, int p_294663_, float p_275604_, float angleXComponent, float angleYComponent, LivingEntity p_275689_, CallbackInfo ci) {
        RenderContextManager.setRenderingInInventory(true);
    }

    @Inject(at = @At("RETURN"), method = "renderEntityInInventoryFollowsAngle(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V", remap = false)
    private static void afterRenderEntityInInventoryFollowsAngle(GuiGraphicsExtractor p_282802_, int p_275688_, int p_275245_, int p_275535_, int p_294406_, int p_294663_, float p_275604_, float angleXComponent, float angleYComponent, LivingEntity p_275689_, CallbackInfo ci) {
        RenderContextManager.setRenderingInInventory(false);
    }
}
