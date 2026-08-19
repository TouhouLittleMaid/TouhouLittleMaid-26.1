package com.github.tartaricacid.touhoulittlemaid.entity.projectile;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.HitResult;

public class EntityThrowPowerPoint extends ThrowableItemProjectile {
    public static final Identifier ENTITY_ID = IdentifierUtil.modLoc("throw_power_point");
    public static final ResourceKey<EntityType<?>> ENTITY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ENTITY_ID);
    public static final EntityType<EntityThrowPowerPoint> TYPE = EntityType.Builder
            .<EntityThrowPowerPoint>of(EntityThrowPowerPoint::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build(ENTITY_KEY);

    public EntityThrowPowerPoint(EntityType<EntityThrowPowerPoint> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityThrowPowerPoint(Level world, LivingEntity thrower) {
        super(TYPE, thrower, world, InitItems.POWER_POINT.get().getDefaultInstance());
    }

    public EntityThrowPowerPoint(Level world, double x, double y, double z) {
        super(TYPE, x, y, z, world, InitItems.POWER_POINT.get().getDefaultInstance());
    }

    @Override
    protected Item getDefaultItem() {
        return InitItems.POWER_POINT.get();
    }

    @Override
    public double getGravity() {
        return 0.07F;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level.isClientSide()) {
            return;
        }
        int color = new PotionContents(Potions.HEALING).getColor();
        this.level.levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, this.blockPosition(), color);
        int count = 30 + this.getRandom().nextInt(30) + this.getRandom().nextInt(30);
        while (count > 0) {
            int value = EntityPowerPoint.getPowerValue(count);
            count -= value;
            this.level.addFreshEntity(new EntityPowerPoint(this.level, this.getX(), this.getY(), this.getZ(), value));
        }
        this.discard();
    }
}
