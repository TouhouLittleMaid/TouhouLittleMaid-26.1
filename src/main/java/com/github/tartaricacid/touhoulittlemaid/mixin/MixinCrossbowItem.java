package com.github.tartaricacid.touhoulittlemaid.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public class MixinCrossbowItem {
    @Inject(method = "shootProjectile(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/projectile/Projectile;IFFFLnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true)
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, LivingEntity target, CallbackInfo ci) {
        if (shooter instanceof EntityMaid maid) {
            // 弩箭伤害也和好感度挂钩
            // 但是烟花火箭的伤害是很特殊的，就不应用了
            if (projectile instanceof AbstractArrow arrow) {
                AttributeInstance attackDamage = maid.getAttribute(Attributes.ATTACK_DAMAGE);
                double attackValue = 2.0;
                if (attackDamage != null) {
                    attackValue = attackDamage.getBaseValue();
                }
                float multiplier = (float) (attackValue / 2.0f);
                arrow.setBaseDamage(arrow.getBaseDamage() * multiplier);
            }
            this.shootCrossbowProjectile(shooter, target, projectile, 1.6F);
            ci.cancel();
        }
    }

    /**
     * 修改默认方法，让女仆能实现超远距离打击
     */
    @Unique
    private void shootCrossbowProjectile(LivingEntity shooter, LivingEntity target, Projectile projectile, float velocityIn) {
        double x = target.getX() - shooter.getX();
        double y = target.getEyeY() - shooter.getEyeY();
        double z = target.getZ() - shooter.getZ();
        // 依据距离调整箭速和不准确度
        float distance = shooter.distanceTo(target);
        float velocity = Mth.clamp(distance / 10f, velocityIn, 3.2f);
        float inaccuracy = 1 - Mth.clamp(distance / 100f, 0, 0.9f);
        // 射出的箭忽略重力，从而能让女仆百发百中
        projectile.setNoGravity(true);
        projectile.shoot(x, y, z, velocity, inaccuracy);
        shooter.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (shooter.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
