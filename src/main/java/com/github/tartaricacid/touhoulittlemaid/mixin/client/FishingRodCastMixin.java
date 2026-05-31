package com.github.tartaricacid.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(FishingRodCast.class)
public class FishingRodCastMixin {
    @Inject(
            method = "get(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/LivingEntity;ILnet/minecraft/world/item/ItemDisplayContext;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void tlm$get(
            ItemStack itemStack, ClientLevel level, LivingEntity owner, int seed,
            ItemDisplayContext displayContext, CallbackInfoReturnable<Boolean> cir
    ) {
        if (owner instanceof EntityMaid maid) {
            if (maid.fishing != null) {
                cir.setReturnValue(owner.getItemHeldByArm(HumanoidArm.RIGHT) == itemStack);
            } else {
                cir.setReturnValue(false);
            }
        }
    }
}
