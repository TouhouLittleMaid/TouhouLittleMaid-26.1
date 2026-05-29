package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidEquipEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTickEvent;
import com.github.tartaricacid.touhoulittlemaid.api.task.IAttackTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IRangedAttackTask;
import com.github.tartaricacid.touhoulittlemaid.config.ServerConfig;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagEntity;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.control.MaidMoveControl;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.navigation.MaidPathNavigation;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleDataCollection;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponents;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.MaidFishingHook;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.network.message.SendEffectPackage;
import com.github.tartaricacid.touhoulittlemaid.world.backups.MaidBackupsManager;
import com.github.tartaricacid.touhoulittlemaid.world.data.MaidWorldData;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.Stack;

import static com.github.tartaricacid.touhoulittlemaid.config.ServerConfig.MAID_AI_TIME_DEBUG;
import static com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidBrain.BRAIN_PROVIDER;
import static com.github.tartaricacid.touhoulittlemaid.inventory.handler.MaidBackpackHandler.BACKPACK_ITEM_SLOT;

public class EntityMaid extends TamableAnimal implements CrossbowAttackMob {

    public static final Identifier ENTITY_ID = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "maid");
    public static final ResourceKey<EntityType<?>> ENTITY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ENTITY_ID);
    public static final EntityType<EntityMaid> TYPE = EntityType.
            Builder.<EntityMaid>of(EntityMaid::new, MobCategory.CREATURE)
            .sized(0.6f, 1.5f)
            .clientTrackingRange(10)
            .build(ENTITY_KEY);

    private static final long WARNING_TIME_NANOS = Duration.ofMillis(50L).toNanos();

    private static final EntityDataAccessor<Boolean> DATA_SYNC_INVULNERABLE = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> BACKPACK_ITEM_SHOW = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ChatBubbleDataCollection> CHAT_BUBBLE = SynchedEntityData.defineId(EntityMaid.class, ChatBubbleRegister.INSTANCE);

    /**
     * 在 {@code super(...)} 返回后由字段初始化器赋值；Brain 构建发生在 super 链路中，此期间为 null。
     */
    public final MaidComponents components = MaidComponents.create(this);

    public boolean guiOpening = false;
    public @Nullable MaidFishingHook fishing = null;
    public MaidRenderState renderState = MaidRenderState.ENTITY;
    private List<SendEffectPackage.EffectData> effects = Lists.newArrayList();

    protected EntityMaid(EntityType<EntityMaid> type, Level world) {
        super(type, world);
        this.moveControl = new MaidMoveControl(this);
        // LivingEntity 构造会调用 createNavigation，覆盖组件构造时的 setNavigation
        this.components.navigation.rebindNavigation();
        this.setPersistenceRequired();
    }

    public EntityMaid(Level worldIn) {
        this(TYPE, worldIn);
    }

    public static EntityDataAccessor<ChatBubbleDataCollection> getChatBubbleKey() {
        return CHAT_BUBBLE;
    }

    public float damageAfterArmorAbsorb(DamageSource source, float damage) {
        return getDamageAfterArmorAbsorb(source, damage);
    }

    public float damageAfterMagicAbsorb(DamageSource source, float damage) {
        return getDamageAfterMagicAbsorb(source, damage);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SYNC_INVULNERABLE, this.isInvulnerable());
        builder.define(BACKPACK_ITEM_SHOW, ItemStack.EMPTY);
        builder.define(CHAT_BUBBLE, ChatBubbleDataCollection.getEmptyCollection());
    }

    @Override
    protected PathNavigation createNavigation(Level levelIn) {
        return new MaidPathNavigation(this, levelIn);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<EntityMaid> getBrain() {
        return (Brain<EntityMaid>) super.getBrain();
    }

    @Override
    protected Brain<? extends LivingEntity> makeBrain(Brain.Packed packedBrain) {
        Brain<EntityMaid> brain = BRAIN_PROVIDER.makeBrain(this, packedBrain);
        MaidBrain.registerBrainGoals(brain, this);
        return brain;
    }

    public void refreshBrain(ServerLevel serverWorldIn) {
        Brain<EntityMaid> oldBrain = this.getBrain();
        oldBrain.stopAll(serverWorldIn, this);
        this.brain = makeBrain(oldBrain.pack());
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        long timeRecord = Util.getNanos();

        if (!guiOpening) {
            Profiler.get().push("maidBrain");
            this.getBrain().tick(level, this);
            Profiler.get().pop();
        }

        timeRecord = Util.getNanos() - timeRecord;
        if (MAID_AI_TIME_DEBUG.get() && timeRecord > WARNING_TIME_NANOS) {
            double timeMs = timeRecord / 1000000.0;
            BlockPos blockPos = this.blockPosition();
            String taskId = components.task.getTask().getUid().toString();
            int searchRange = this.getHomeRadius();
            TouhouLittleMaid.LOGGER.error(
                    "Maid's AI taking too long! Time: {} ms, Pos: ({},{},{}), Task ID: {}, Search Range: {}",
                    timeMs, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                    taskId, searchRange
            );
        }

        super.customServerAiStep(level);
    }

    @Override
    public void tick() {
        if (!NeoForge.EVENT_BUS.post(new MaidTickEvent(this)).isCanceled()) {
            super.tick();
            components.item.getMaidBauble().fireEvent((b, s) -> {
                b.onTick(this, s);
                return false;
            });
        }

        int saveIntervalTick = ServerConfig.MAID_BACKUP_INTERVAL_SECONDS.get() * 20;
        int checkTick = Math.abs(this.getUUID().hashCode()) % saveIntervalTick;
        if (this.level.getGameTime() % saveIntervalTick == checkTick && this.level instanceof ServerLevel serverLevel) {
            MaidBackupsManager.save(serverLevel.getServer(), this);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        components.baseTick();
    }

    @Override
    public void rideTick() {
        super.rideTick();

        Entity vehicle = this.getVehicle();
        if (vehicle != null && !vehicle.is(TagEntity.MAID_VEHICLE_ROTATE_BLOCKLIST)) {
            this.setYHeadRot(vehicle.getYRot());
            this.setYBodyRot(vehicle.getYRot());
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        this.updateSwingTime();

        if (!level.isClientSide()) {
            components.aiStep();
        } else {
            components.navigation.aiStep();
        }
    }

    @Override
    public InteractionResult mobInteract(Player playerIn, InteractionHand hand) {
        return components.misc.mobInteract(playerIn, hand);
    }

    @Override
    protected void pushEntities() {
        super.pushEntities();

        if (components.config.isPickup() && this.isTame()) {
            components.item.pickupEntities();
        }
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) {
        return components.combat.isWithinMeleeAttackRange(target);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        return components.combat.doHurtTarget(level, target, super::doHurtTarget);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return components.combat.hurtServer(level, source, amount, super::hurtServer);
    }

    @Nullable
    public Stack<DamageContainer> getDamageContainers() {
        return this.damageContainers;
    }

    @Override
    protected void actuallyHurt(ServerLevel level, DamageSource damageSrc, float damageAmount) {
        components.combat.actuallyHurt(level, damageSrc, damageAmount);
    }

    @Override
    protected void handlePortal() {
        components.teleport.handlePortal();
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();

        if (this.getOwnerReference() != null) {
            MaidWorldData data = MaidWorldData.get(this.level);
            if (data != null) {
                data.removeInfo(this);
            }
        }
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();

        if (!this.level.isClientSide() && this.isAlive() && this.getOwnerReference() != null) {
            MaidWorldData data = MaidWorldData.get(this.level);
            if (data != null) {
                data.addInfo(this);
            }
        }
    }

    @Override
    public void die(DamageSource cause) {
        components.death.die(cause, super::die);
    }

    public boolean isDead() {
        return this.dead;
    }

    @Override
    public void setChargingCrossbow(boolean isCharging) {
        components.animation.setChargingCrossbow(isCharging);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        return this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    @Override
    public ItemStack getProjectile(ItemStack weaponStack) {
        return components.combat.getProjectile(weaponStack);
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt lightning) {
        super.thunderHit(world, lightning);
        components.misc.thunderHit(world, lightning);
    }

    @Override
    protected void hurtArmor(DamageSource damageSource, float damage) {
        this.doHurtEquipment(
                damageSource, damage,
                EquipmentSlot.FEET, EquipmentSlot.LEGS,
                EquipmentSlot.CHEST, EquipmentSlot.HEAD
        );
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        components.combat.performRangedAttack(target, distanceFactor);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, @Nullable LivingEntity owner) {
        return target.getType() != EntityType.ARMOR_STAND;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (components.task.getTask() instanceof IAttackTask attackTask) {
            return attackTask.canAttack(this, target);
        }
        return super.canAttack(target);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        components.save(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        components.load(input);

        this.setSyncInvulnerable(this.isInvulnerable());

        ItemStack backpackItem = ItemUtil.getStack(components.item.getMaidInv(), BACKPACK_ITEM_SLOT);
        this.setBackpackShowItem(backpackItem);
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
        components.death.dropEquipment(level);
    }

    @Override
    public void remove(RemovalReason reason) {
        components.death.remove(reason);
        super.remove(reason);
    }

    @Override
    protected void completeUsingItem() {
        components.swim.resetEatBreatheItem();
        super.completeUsingItem();
        components.item.backCurrentHandItemStack(this);
    }

    @Override
    public void handleExtraItemsCreatedOnUse(ItemStack convertedStack) {
        components.item.handleExtraItemsCreatedOnUse(convertedStack);
    }

    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @Override
    public int getBaseExperienceReward(ServerLevel level) {
        return components.stats.getExperience();
    }

    @Override
    protected Component getTypeName() {
        return components.misc.getTypeName();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
                                        EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn) {
        return components.misc.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);
        if (!this.level.isClientSide()) {
            NeoForge.EVENT_BUS.post(new MaidEquipEvent(this, slot, stack));
        }
    }

    public boolean firstTick() {
        return this.firstTick;
    }

    @Override
    public void onEquipItem(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem) {
        super.onEquipItem(slot, oldItem, newItem);
        components.item.onEquipItem(slot, oldItem, newItem);
    }

    @Override
    public void playSound(SoundEvent soundEvent, float volume, float pitch) {
        if (components.sound.playSound(soundEvent, volume, pitch)) {
            return;
        }
        super.playSound(soundEvent, volume, pitch);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return components.sound.getAmbientSound();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return components.sound.getHurtSound(damageSourceIn);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return components.sound.getDeathSound();
    }

    @Override
    public float getVoicePitch() {
        return 1 + random.nextFloat() * 0.1F;
    }

    @Override
    public float getEyeHeight(Pose pose) {
        boolean sittingPose = isMaidInSittingPose();
        float multiply = sittingPose ? 0.65F : 0.85F;
        return this.getDimensions(pose).height() * multiply;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    public boolean canPathReach(BlockPos pos) {
        Path path = this.getNavigation().createPath(pos, 0);
        return path != null && path.canReach();
    }

    public boolean canPathReach(Entity entity) {
        Path path = this.getNavigation().createPath(entity, 0);
        return path != null && path.canReach();
    }

    @Override
    public boolean canUseNonMeleeWeapon(ItemStack itemStack) {
        return components.task.getTask() instanceof IRangedAttackTask;
    }

    public boolean canSee(LivingEntity target) {
        if (components.task.getTask() instanceof IRangedAttackTask rangedTask) {
            return rangedTask.canSee(this, target);
        }
        return BehaviorUtils.canSee(this, target);
    }

    public AABB searchDimension() {
        if (components.task.getScheduleDetail() == Activity.WORK) {
            return components.task.getTask().searchDimension(this);
        }
        return TaskManager.getIdleTask().searchDimension(this);
    }

    public float searchRadius() {
        return components.task.getTask().searchRadius(this);
    }

    @Override
    public Vec3 getLeashOffset() {
        String modelId = components.profile.getModelId();
        Vec3 pose = components.misc.getLegacyLeashOffset(modelId);
        if (pose != null) {
            return pose;
        }
        return super.getLeashOffset();
    }

    @Override
    protected void updateUsingItem(ItemStack usingItem) {
        components.item.updateUsingItem(usingItem);
        super.updateUsingItem(usingItem);
    }

    public void setUseItemRemainingTicks(int ticks) {
        this.useItemRemaining = ticks;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double range = 64.0 * getViewScale();
        return distance < range * range;
    }

    @Override
    public void startSleeping(BlockPos pos) {
        super.startSleeping(pos);

        this.setHealth(this.getMaxHealth());
        components.favorability.apply(Type.SLEEP);
        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.MAID_SLEEP);
        }
    }

    public boolean isMaidInSittingPose() {
        return super.isInSittingPose();
    }

    @Override
    public boolean isWithinHome() {
        return this.isWithinHome(this.blockPosition());
    }

    @Override
    public boolean isWithinHome(BlockPos pos) {
        if (hasHome()) {
            return this.getHomePosition().distSqr(pos) < (double) (this.getHomeRadius() * this.getHomeRadius());
        }
        return true;
    }

    @Override
    public void setHomeTo(BlockPos pos, int distance) {
        components.task.setHomeTo(pos, distance);
    }

    @Override
    public BlockPos getHomePosition() {
        return components.task.getHomePosition();
    }

    @Override
    public int getHomeRadius() {
        return components.task.getHomeRadius();
    }

    @Override
    public boolean hasHome() {
        return components.config.isHomeModeEnable();
    }

    public BlockPos getBrainSearchPos() {
        if (this.hasHome()) {
            return this.getHomePosition();
        } else {
            return this.blockPosition();
        }
    }

    public boolean canBrainMoving() {
        return !this.isMaidInSittingPose()
               && !this.isPassenger()
               && !this.isSleeping()
               && !this.isLeashed();
    }

    public ItemStack getBackpackShowItem() {
        return this.entityData.get(BACKPACK_ITEM_SHOW);
    }

    public void setBackpackShowItem(ItemStack stack) {
        this.entityData.set(BACKPACK_ITEM_SHOW, stack);
    }

    public boolean getSyncInvulnerable() {
        return this.entityData.get(DATA_SYNC_INVULNERABLE);
    }

    public void setSyncInvulnerable(boolean isInvulnerable) {
        super.setInvulnerable(isInvulnerable);
        this.entityData.set(DATA_SYNC_INVULNERABLE, isInvulnerable);
    }

    @Override
    public void setInSittingPose(boolean inSittingPose) {
        super.setInSittingPose(inSittingPose);
        this.setOrderedToSit(inSittingPose);
    }

    @Override
    public float getLuck() {
        return (float) this.getAttributeValue(Attributes.LUCK);
    }

    public boolean hasFishingHook() {
        return this.fishing != null;
    }

    public void setEffects(List<SendEffectPackage.EffectData> effects) {
        this.effects = effects;
    }

    public List<SendEffectPackage.EffectData> getEffects() {
        return effects;
    }

    @Override
    public Vec3 handleOnClimbable(Vec3 deltaMovement) {
        Vec3 oriDelta = super.handleOnClimbable(deltaMovement);
        return components.climb.handleOnClimbable(oriDelta);
    }

    public PathNavigation getRawNavigation() {
        return this.navigation;
    }

    @Override
    public boolean onClimbable() {
        return components.climb.onClimbable(super::onClimbable);
    }

    @Override
    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 deltaMovement, float friction) {
        return components.climb.handleRelativeFrictionAndCalculateMovement(deltaMovement, friction);
    }

    public void setNavigation(PathNavigation navigation) {
        this.navigation = navigation;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPushedByFluid() {
        return !components.swim.wantToSwim();
    }

    @Override
    public void travel(Vec3 travelVector) {
        components.climb.travel(travelVector, super::travel);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        if (pose == Pose.SWIMMING) {
            return components.swim.getSwimmingDimensions();
        }
        return super.getDefaultDimensions(pose);
    }

    @Override
    public void updateSwimming() {
        components.swim.updateSwimming();
    }

    @Override
    public boolean isVisuallySwimming() {
        return this.isSwimming();
    }

    public void setUseItem(ItemStack stack) {
        this.useItem = stack;
    }

    @Override
    @Nullable
    public ItemStack getItemBlockingWith() {
        return components.combat.getItemBlockingWith();
    }

    @Override
    public float applyItemBlocking(ServerLevel level, DamageSource source, float damage) {
        return components.combat.applyItemBlocking(
                level, source, damage, super::applyItemBlocking
        );
    }

    @Override
    public void spawnItemParticles(ItemStack stack, int amount) {
        components.particle.spawnItemParticles(stack, amount);
    }
}
