package com.github.tartaricacid.touhoulittlemaid.entity.item;


import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EntityExtinguishingAgent extends Entity {
    public static final Identifier ENTITY_ID = IdentifierUtil.modLoc("extinguishing_agent");
    public static final ResourceKey<EntityType<?>> ENTITY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ENTITY_ID);
    public static final EntityType<EntityExtinguishingAgent> TYPE = EntityType
            .Builder.<EntityExtinguishingAgent>of(EntityExtinguishingAgent::new, MobCategory.MISC)
            .noLootTable()
            .noSave()
            .noSummon()
            .sized(0.2f, 0.2f)
            .clientTrackingRange(10)
            .build(ENTITY_KEY);

    private static final int MAX_AGE = 3 * 20;
    private static final int REMOVE_FIRE_AGE = 5;

    private List<Monster> cacheFireImmuneMonster = Lists.newArrayList();

    public EntityExtinguishingAgent(EntityType<?> entityTypeIn, Level level) {
        super(entityTypeIn, level);
    }

    public EntityExtinguishingAgent(Level level, Vec3 position) {
        this(TYPE, level);
        this.setPos(position.x, position.y, position.z);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (tickCount > MAX_AGE) {
            this.discard();
            return;
        }
        if (tickCount == REMOVE_FIRE_AGE) {
            this.removeBlockFire();
            this.removeEntityFire();
        }
        this.damageFireImmuneMonster();
        if (level.isClientSide()) {
            this.spawnCloudParticle();
        }
        float pitch = 2.0f - (1.8f / MAX_AGE) * tickCount;
        this.playSound(SoundEvents.WOOL_PLACE, pitch, 0.1f);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damage) {
        return false;
    }

    private void spawnCloudParticle() {
        int spawnNumber = 4;
        for (int i = 0; i < spawnNumber; i++) {
            double offsetX = 2 * random.nextDouble() - 1;
            double offsetY = random.nextDouble() / 2;
            double offsetZ = 2 * random.nextDouble() - 1;
            level.addParticle(
                    ParticleTypes.CLOUD, false, false,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0, 0.1, 0
            );
        }
    }

    @SuppressWarnings("deprecation")
    private void damageFireImmuneMonster() {
        if (tickCount % 5 != 0 || this.cacheFireImmuneMonster.isEmpty()) {
            return;
        }
        this.cacheFireImmuneMonster.forEach(monster -> {
            if (monster.isAlive()) {
                monster.hurt(level.damageSources().magic(), 2);
            }
        });
    }

    private void removeEntityFire() {
        AABB aabb = this.getBoundingBox().inflate(2, 1, 2);
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb);
        this.cacheFireImmuneMonster = level.getEntitiesOfClass(Monster.class, aabb, Entity::fireImmune);
        for (LivingEntity entity : list) {
            entity.clearFire();
        }
    }

    private void removeBlockFire() {
        int hRange = 2;
        int vRange = 1;

        BlockPos center = this.blockPosition();
        BlockPos minPos = center.offset(-hRange, -vRange, -hRange);
        BlockPos maxPos = center.offset(hRange, vRange, hRange);

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            if (level.getBlockState(pos).is(Blocks.FIRE)) {
                level.removeBlock(pos, false);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }
}
