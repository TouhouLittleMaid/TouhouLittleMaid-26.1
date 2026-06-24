package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidEquipEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTickEvent;
import com.github.tartaricacid.touhoulittlemaid.api.task.IAttackTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IRangedAttackTask;
import com.github.tartaricacid.touhoulittlemaid.config.ServerConfig;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagEntity;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.control.MaidMoveControl;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.navigation.MaidPathNavigation;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleDataCollection;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.MaidFishingHook;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.network.message.SendEffectPackage;
import com.github.tartaricacid.touhoulittlemaid.world.backups.MaidBackupsManager;
import com.github.tartaricacid.touhoulittlemaid.world.data.MaidWorldData;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.EntityTypeUtil;
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

public class EntityMaid extends MaidManagerHost implements CrossbowAttackMob {
    public static final Identifier ENTITY_ID = IdentifierUtil.modLoc("maid");
    public static final ResourceKey<EntityType<?>> ENTITY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ENTITY_ID);
    public static final EntityType<EntityMaid> TYPE = EntityType.
            Builder.<EntityMaid>of(EntityMaid::new, MobCategory.CREATURE)
            .sized(0.6f, 1.5f)
            .clientTrackingRange(10)
            .build(ENTITY_KEY);

    /**
     * AI 超时检测
     */
    private static final long WARNING_TIME_NANOS = Duration.ofMillis(50L).toNanos();

    /**
     * 女仆默认同步数据
     */
    private static final EntityDataAccessor<Boolean> DATA_SYNC_INVULNERABLE = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> BACKPACK_ITEM_SHOW = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ChatBubbleDataCollection> CHAT_BUBBLE = SynchedEntityData.defineId(EntityMaid.class, ChatBubbleRegister.INSTANCE);

    /**
     * 检查玩家是否正在打开女仆的 GUI 的标志位，打开 GUI 后女仆会暂停 Brain 的执行
     */
    public boolean guiOpening = false;
    /**
     * 女仆钓鱼实体的引用
     */
    public @Nullable MaidFishingHook fishing = null;
    /**
     * 女仆当前处于什么形态的渲染，是手办、雕像还是 GUI 内渲染等等
     */
    public MaidRenderState renderState = MaidRenderState.ENTITY;
    /**
     * 用于女仆 GUI 界面内的效果的渲染
     */
    private List<SendEffectPackage.EffectData> effects = Lists.newArrayList();

    protected EntityMaid(EntityType<EntityMaid> type, Level world) {
        super(type, world);
        super.initMaidManagers(this);

        this.moveControl = new MaidMoveControl(this);
        // 启用实体持久化，也许能解决难以复现的女仆实体丢失问题
        this.setPersistenceRequired();
    }

    public EntityMaid(Level worldIn) {
        this(TYPE, worldIn);
    }

    public static EntityDataAccessor<ChatBubbleDataCollection> getChatBubbleKey() {
        return CHAT_BUBBLE;
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

        // 当玩家打开女仆的 GUI 时，暂停女仆的 Brain 执行
        if (!guiOpening) {
            Profiler.get().push("maidBrain");
            this.getBrain().tick(level, this);
            Profiler.get().pop();
        }

        // 如果开启了 Debug 模式，此处会记录每次 AI 执行的时间
        // 超过 50ms 就会在控制台输出警告日志，方便开发者定位性能问题
        timeRecord = Util.getNanos() - timeRecord;
        if (MAID_AI_TIME_DEBUG.get() && timeRecord > WARNING_TIME_NANOS) {
            double timeMs = timeRecord / 1000000.0;
            BlockPos blockPos = this.blockPosition();
            String taskId = this.getTask().getUid().toString();
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
            this.getMaidBauble().fireEvent((b, s) -> {
                b.onTick(this, s);
                return false;
            });
        }

        // 强制开启女仆备份机制
        int saveIntervalTick = ServerConfig.MAID_BACKUP_INTERVAL_SECONDS.get() * 20;
        // 通过哈希计算出一个随机值，这样做可以避免所有实体都在同一 tick 进行保存
        int checkTick = Math.abs(this.getUUID().hashCode()) % saveIntervalTick;
        if (this.level.getGameTime() % saveIntervalTick == checkTick && this.level instanceof ServerLevel serverLevel) {
            MaidBackupsManager.save(serverLevel.getServer(), this);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();

        this.backpackManager.tick();
        this.soundManager.tick();
        this.climbManager.tick();
        this.particleManager.tick();
        this.miscManager.tick();
        this.gameManager.tick();
    }

    @Override
    public void rideTick() {
        super.rideTick();

        // 强制让女仆和骑乘的实体一个朝向，黑名单的除外
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
        this.getNavigationManager().tick();

        if (!level.isClientSide()) {
            this.chatBubbleManager.tick();
            this.favorabilityManager.tick();
            this.taskManager.tick();
            this.combatManager.tick();
            this.combatManager.aiStep();
        }
    }

    @Override
    public InteractionResult mobInteract(Player playerIn, InteractionHand hand) {
        return this.miscManager.mobInteract(playerIn, hand);
    }

    @Override
    protected void pushEntities() {
        super.pushEntities();

        // 只有拾物模式开启，驯服状态下才可以捡起物品
        if (this.isPickup() && this.isTame()) {
            itemManager.pickupEntities();
        }
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) {
        return combatManager.isWithinMeleeAttackRange(target);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        return combatManager.doHurtTarget(level, target, super::doHurtTarget);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return combatManager.hurtServer(level, source, amount, super::hurtServer);
    }

    @Nullable
    public Stack<DamageContainer> getDamageContainers() {
        return this.damageContainers;
    }

    @Override
    public float getDamageAfterArmorAbsorb(DamageSource damageSource, float damage) {
        return super.getDamageAfterArmorAbsorb(damageSource, damage);
    }

    @Override
    public float getDamageAfterMagicAbsorb(DamageSource damageSource, float damage) {
        return super.getDamageAfterMagicAbsorb(damageSource, damage);
    }

    @Override
    protected void actuallyHurt(ServerLevel level, DamageSource damageSrc, float damageAmount) {
        this.combatManager.actuallyHurt(level, damageSrc, damageAmount);
    }

    @Override
    protected void handlePortal() {
        this.teleportManager.handlePortal();
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();

        // 当女仆添加进世界后，从 MaidWorldData 上移除
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

        // 当女仆从区块卸载时，把信息存入 MaidWorldData，方便其他工具查询实体位置
        if (!this.level.isClientSide() && this.isAlive() && this.getOwnerReference() != null) {
            MaidWorldData data = MaidWorldData.get(this.level);
            if (data != null) {
                data.addInfo(this);
            }
        }
    }

    @Override
    public void die(DamageSource cause) {
        this.deathManager.die(cause, super::die);
    }

    public boolean isDead() {
        return this.dead;
    }

    @Override
    public void setChargingCrossbow(boolean isCharging) {
        this.animationManager.setChargingCrossbow(isCharging);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        // 实现 CrossbowAttackMob 接口中拿到目标实体的方法
        return this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    @Override
    public ItemStack getProjectile(ItemStack weaponStack) {
        // 弩在装载时的 tryLoadProjectiles 方法会从这里拿到需要装填的物品
        return this.combatManager.getProjectile(weaponStack);
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt lightning) {
        super.thunderHit(world, lightning);
        this.miscManager.thunderHit(world, lightning);
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
        this.combatManager.performRangedAttack(target, distanceFactor);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, @Nullable LivingEntity owner) {
        // 避免女仆攻击盔甲
        return target.getType() != EntityTypeUtil.armorStand();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.getTask() instanceof IAttackTask attackTask) {
            return attackTask.canAttack(this, target);
        }
        return super.canAttack(target);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        this.statsManager.save(output);
        this.itemManager.save(output);
        this.favorabilityManager.save(output);
        this.taskManager.save(output);
        this.killRecordManager.save(output);
        this.aiChatManager.save(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        this.statsManager.read(input);
        this.itemManager.read(input);
        this.favorabilityManager.read(input);
        this.taskManager.read(input);
        this.killRecordManager.read(input);
        this.aiChatManager.read(input);

        // 因为原版的无敌状态不会自动同步，故需要在这里手动设置同步
        this.setSyncInvulnerable(this.isInvulnerable());

        // 背包内的装饰栏有特殊渲染效果，需要手动同步到客户端
        ItemStack backpackItem = ItemUtil.getStack(itemManager.getMaidInv(), BACKPACK_ITEM_SLOT);
        this.setBackpackShowItem(backpackItem);
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
        this.deathManager.dropEquipment(level);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.deathManager.remove(reason);
        super.remove(reason);
    }

    @Override
    protected void completeUsingItem() {
        this.getSwimManager().resetEatBreatheItem();
        super.completeUsingItem();
        this.itemManager.backCurrentHandItemStack(this);
    }

    @Override
    public void handleExtraItemsCreatedOnUse(ItemStack convertedStack) {
        this.itemManager.handleExtraItemsCreatedOnUse(convertedStack);
    }

    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @Override
    public int getBaseExperienceReward(ServerLevel level) {
        return this.getExperience();
    }

    @Override
    protected Component getTypeName() {
        return this.miscManager.getTypeName();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
                                        EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn) {
        return this.miscManager.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
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
        this.itemManager.onEquipItem(slot, oldItem, newItem);
    }

    @Override
    public void playSound(SoundEvent soundEvent, float volume, float pitch) {
        if (this.soundManager.playSound(soundEvent, volume, pitch)) {
            return;
        }
        super.playSound(soundEvent, volume, pitch);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.soundManager.getAmbientSound();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.soundManager.getHurtSound(damageSourceIn);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return this.soundManager.getDeathSound();
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
        // 没有幼年形态的女仆
        return false;
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableMob) {
        // 没有幼年形态的女仆
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // 女仆不可繁殖
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

    /**
     * 给 MaidMeleeAttack 使用，用于判断当前任务是否能够近战
     * <p>
     * 如果返回 true，则表示当前是远程攻击，不是近战攻击
     */
    @Override
    public boolean canUseNonMeleeWeapon(ItemStack itemStack) {
        return getTask() instanceof IRangedAttackTask;
    }

    /**
     * 因为原版默认的攻击识别范围是固定死的 16 格，但是一些远程武器我们希望获得超视距打击
     * 通过修改此处来获得更远的攻击距离
     */
    public boolean canSee(LivingEntity target) {
        if (this.getTask() instanceof IRangedAttackTask rangedTask) {
            return rangedTask.canSee(this, target);
        }
        return BehaviorUtils.canSee(this, target);
    }

    /**
     * 实体搜索范围
     */
    public AABB searchDimension() {
        // 仅工作时，才搜索 task 的范围，避免性能压力
        if (this.getScheduleDetail() == Activity.WORK) {
            return this.getTask().searchDimension(this);
        }
        return TaskManager.getIdleTask().searchDimension(this);
    }

    /**
     * 实体搜索范围的水平范围值
     */
    public float searchRadius() {
        return this.getTask().searchRadius(this);
    }

    @Override
    public Vec3 getLeashOffset() {
        String modelId = this.getModelId();
        Vec3 pose = this.miscManager.getLegacyLeashOffset(modelId);
        if (pose != null) {
            return pose;
        }
        return super.getLeashOffset();
    }

    @Override
    protected void updateUsingItem(ItemStack usingItem) {
        this.itemManager.updateUsingItem(usingItem);
        super.updateUsingItem(usingItem);
    }

    public void setUseItemRemainingTicks(int ticks) {
        this.useItemRemaining = ticks;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        // 修正睡觉时渲染问题，默认 64 格内渲染
        double range = 64.0 * getViewScale();
        return distance < range * range;
    }

    @Override
    public void startSleeping(BlockPos pos) {
        super.startSleeping(pos);

        // 睡觉时自动满血，增加好感度，并触发睡觉成就
        this.setHealth(this.getMaxHealth());
        this.favorabilityManager.apply(Type.SLEEP);
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
        this.taskManager.setHomeTo(pos, distance);
    }

    @Override
    public BlockPos getHomePosition() {
        return this.taskManager.getHomePosition();
    }

    @Override
    public int getHomeRadius() {
        return this.taskManager.getHomeRadius();
    }

    @Override
    public boolean hasHome() {
        return this.isHomeModeEnable();
    }

    public BlockPos getBrainSearchPos() {
        if (this.hasHome()) {
            return this.getHomePosition();
        } else {
            return this.blockPosition();
        }
    }

    /**
     * 当前女仆是否能够执行移动相关的任务
     */
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
        return this.climbManager.handleOnClimbable(oriDelta);
    }

    public PathNavigation getRawNavigation() {
        return this.navigation;
    }

    @Override
    public boolean onClimbable() {
        return this.climbManager.onClimbable(super::onClimbable);
    }

    @Override
    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 deltaMovement, float friction) {
        return this.climbManager.handleRelativeFrictionAndCalculateMovement(deltaMovement, friction);
    }

    public void setNavigation(PathNavigation navigation) {
        this.navigation = navigation;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPushedByFluid() {
        return !this.getSwimManager().wantToSwim();
    }

    @Override
    public void travel(Vec3 travelVector) {
        this.climbManager.travel(travelVector, super::travel);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        if (pose == Pose.SWIMMING) {
            return this.getSwimManager().getSwimmingDimensions();
        }
        return super.getDefaultDimensions(pose);
    }

    @Override
    public void updateSwimming() {
        this.getSwimManager().updateSwimming();
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
        return this.combatManager.getItemBlockingWith();
    }

    @Override
    public float applyItemBlocking(ServerLevel level, DamageSource source, float damage) {
        return this.combatManager.applyItemBlocking(
                level, source, damage, super::applyItemBlocking
        );
    }

    @Override
    public void spawnItemParticles(ItemStack stack, int amount) {
        particleManager.spawnItemParticles(stack, amount);
    }
}
