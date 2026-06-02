package com.github.tartaricacid.touhoulittlemaid.entity.monster;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.goal.FairyAttackGoal;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.goal.FairyNearestAttackableTargetGoal;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.DanmakuShoot;
import com.github.tartaricacid.touhoulittlemaid.init.InitPoi;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE;

public class EntityFairy extends Monster implements RangedAttackMob, FlyingAnimal, IHasPowerPoint {
    public static final EntityType<EntityFairy> TYPE = EntityType.Builder.<EntityFairy>of(EntityFairy::new, MobCategory.MONSTER)
            .sized(0.6f, 1.5f).clientTrackingRange(10).build(ResourceKey.create(Registries.ENTITY_TYPE, IdentifierUtil.modLoc("fairy")));

    private static final Identifier SPEED_MODIFIER_BABY_ID = IdentifierUtil.modLoc("baby");
    private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_ID, 0.2, ADD_MULTIPLIED_BASE);
    private static final EntityDimensions BABY_DIMENSIONS = TYPE.getDimensions().scale(0.75F).withEyeHeight(1);

    public static final String RICK = "rick";
    private static final String FAIRY_TYPE_TAG_NAME = "FairyType";
    private static final String BABY_TAG_NAME = "IsBaby";

    private static final EntityDataAccessor<Integer> DATA_FAIRY_TYPE = SynchedEntityData.defineId(EntityFairy.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(EntityFairy.class, EntityDataSerializers.BOOLEAN);

    private static final double AIMED_SHOT_PROBABILITY = 0.9;

    protected EntityFairy(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new FlyingMoveControl(this, 15, true);
    }

    public EntityFairy(Level worldIn) {
        this(TYPE, worldIn);
    }

    public static boolean checkFairySpawnRules(EntityType<EntityFairy> entityType, ServerLevelAccessor levelAccessor, EntitySpawnReason spawnType, BlockPos pos, RandomSource randomSource) {
        if (Monster.checkMonsterSpawnRules(entityType, levelAccessor, spawnType, pos, randomSource) && levelAccessor instanceof ServerLevel level) {
            int scarecrowRange = MiscConfig.SCARECROW_RANGE.get();
            long findCount = level.getPoiManager().getInSquare(type -> type.value().equals(InitPoi.SCARECROW.get()), pos, scarecrowRange, PoiManager.Occupancy.ANY).count();
            return findCount <= 0;
        }
        return false;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new FairyAttackGoal(this, 6.0, 1.0));
        goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 1.0));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, EntityMaid.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new FairyNearestAttackableTargetGoal<>(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FAIRY_TYPE, FairyType.BLACK.ordinal());
        builder.define(DATA_BABY_ID, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_BABY_ID.equals(pKey)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public int getPowerPoint() {
        double reward = MiscConfig.MAID_FAIRY_POWER_POINT.get() * 100;
        if (this.isBaby()) {
            return (int) (reward * 2);
        }
        return (int) reward;
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        dropPowerPoint(this);
    }

    @Override
    public boolean causeFallDamage(double distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        float damageBase = 1;
        Difficulty difficulty = target.level.getDifficulty();
        switch (difficulty) {
            case NORMAL -> damageBase = 1.5f;
            case HARD -> damageBase = 2f;
        }
        if (this.random.nextFloat() <= AIMED_SHOT_PROBABILITY) {
            DanmakuShoot.create().setWorld(level).setThrower(this)
                    .setTarget(target).setRandomColor().setRandomType()
                    .setDamage(distanceFactor + damageBase).setGravity(0)
                    .setVelocity(0.2f * (distanceFactor + 1))
                    .setInaccuracy(0.2f).aimedShot();
        } else {
            DanmakuShoot.create().setWorld(level).setThrower(this)
                    .setTarget(target).setRandomColor().setRandomType()
                    .setDamage(distanceFactor + damageBase + 0.5f).setGravity(0)
                    .setVelocity(0.2f * (distanceFactor + 1))
                    .setInaccuracy(0.2f).setFanNum(3).setYawTotal(Math.PI / 6)
                    .fanShapedShot();
        }
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, worldIn);
        navigator.setCanOpenDoors(false);
        navigator.setCanFloat(true);
        return navigator;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, EntitySpawnReason reason,
                                        @Nullable SpawnGroupData spawnDataIn) {
        this.setFairyTypeOrdinal(random.nextInt(FairyType.values().length));
        // 有 5% 概率生成 Rick-rolling 彩蛋
        if (random.nextInt(20) == 0) {
            this.setCustomName(Component.literal(RICK));
            this.setCustomNameVisible(true);
        }
        // 有 10% 概率生成小女仆妖精
        if (random.nextInt(10) == 0) {
            this.setBaby(true);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store(FAIRY_TYPE_TAG_NAME, Codec.INT, getFairyTypeOrdinal());
        output.store(BABY_TAG_NAME, Codec.BOOL, this.isBaby());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read(FAIRY_TYPE_TAG_NAME, Codec.INT).ifPresent(this::setFairyTypeOrdinal);
        input.read(BABY_TAG_NAME, Codec.BOOL).ifPresent(this::setBaby);
    }

    public int getFairyTypeOrdinal() {
        return this.entityData.get(DATA_FAIRY_TYPE);
    }

    public void setFairyTypeOrdinal(int ordinal) {
        this.entityData.set(DATA_FAIRY_TYPE, ordinal);
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public boolean isBaby() {
        return this.getEntityData().get(DATA_BABY_ID);
    }

    @Override
    public void setBaby(boolean isBaby) {
        this.getEntityData().set(DATA_BABY_ID, isBaby);

        if (!this.level.isClientSide()) {
            AttributeInstance attribute = this.getAttribute(Attributes.FLYING_SPEED);
            if (attribute == null) {
                return;
            }
            attribute.removeModifier(SPEED_MODIFIER_BABY_ID);
            if (isBaby) {
                attribute.addTransientModifier(SPEED_MODIFIER_BABY);
            }
        }
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return InitSounds.FAIRY_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return InitSounds.FAIRY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return InitSounds.FAIRY_DEATH.get();
    }
}
