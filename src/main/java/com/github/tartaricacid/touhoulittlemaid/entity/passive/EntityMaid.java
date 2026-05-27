package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.ai.manager.entity.MaidAIChatManager;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.IBackpackData;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.api.event.*;
import com.github.tartaricacid.touhoulittlemaid.api.task.IAttackTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IRangedAttackTask;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.compat.curios.CuriosCompat;
import com.github.tartaricacid.touhoulittlemaid.config.ServerConfig;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.data.MaidNumAttachment;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagEntity;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagItem;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.control.MaidMoveControl;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.navigation.MaidPathNavigation;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.EmptyBackpack;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleDataCollection;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleManager;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.FavorabilityManager;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.info.ServerCustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityTombstone;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.MaidFishingHook;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.init.InitAttribute;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.backpack.BaubleContainer;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.config.MaidConfigContainer;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.MaidBackpackHandler;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.ItemBreakPackage;
import com.github.tartaricacid.touhoulittlemaid.network.message.PlayMaidSoundPackage;
import com.github.tartaricacid.touhoulittlemaid.network.message.SendEffectPackage;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import com.github.tartaricacid.touhoulittlemaid.util.TeleportHelper;
import com.github.tartaricacid.touhoulittlemaid.world.backups.MaidBackupsManager;
import com.github.tartaricacid.touhoulittlemaid.world.data.MaidWorldData;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;

import static com.github.tartaricacid.touhoulittlemaid.config.ServerConfig.MAID_AI_TIME_DEBUG;
import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.MAID_NUM;
import static net.neoforged.neoforge.common.CommonHooks.onLivingDamagePost;
import static net.neoforged.neoforge.common.CommonHooks.onLivingDamagePre;

public class EntityMaid extends TamableAnimal implements
        CrossbowAttackMob,
        MaidAnimationManager.View,
        MaidConfigManager.View,
        MaidItemManager.View,
        MaidParticleManager.View,
        MaidProfileManager.View,
        MaidStatsManager.View,
        MaidTaskManager.View,
        MaidWorldInteractionManager.View,
        MaidTeleportManager.View,
        MaidGameManager.View,
        MaidSwimManager.View {

    public static final EntityType<EntityMaid> TYPE = EntityType.Builder.<EntityMaid>of(EntityMaid::new, MobCategory.CREATURE)
            .sized(0.6f, 1.5f).clientTrackingRange(10)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "maid")));

    // 女仆默认属性
    public static final String MAID_BACKPACK_TYPE = "MaidBackpackType";
    public static final String EXPERIENCE_TAG = "MaidExperience";
    public static final String MAID_INVENTORY_TAG = "MaidInventory";
    public static final String MAID_BAUBLE_INVENTORY_TAG = "MaidBaubleInventory";
    public static final String MAID_HIDE_INVENTORY_TAG = "MaidHideInventory";
    public static final String MAID_TASK_INVENTORY_TAG = "MaidTaskInventory";

    // AI 超时检测
    private static final long WARNING_TIME_NANOS = Duration.ofMillis(50L).toNanos();

    // Brain
    private static final Supplier<Brain.Provider<EntityMaid>> BRAIN_PROVIDER = Suppliers.memoize(() -> Brain.provider(
            MaidBrain.getMemoryTypes(),
            MaidBrain.getSensorTypes(),
            //TODO 是否可能简化Brain注册?
            _ -> new ArrayList<>()
    ));

    // 女仆默认同步数据
    private static final EntityDataAccessor<Boolean> DATA_INVULNERABLE = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<MaidSchedule> SCHEDULE_MODE = SynchedEntityData.defineId(EntityMaid.class, MaidSchedule.DATA);
    private static final EntityDataAccessor<BlockPos> RESTRICT_CENTER = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> RESTRICT_RADIUS = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ChatBubbleDataCollection> CHAT_BUBBLE = SynchedEntityData.defineId(EntityMaid.class, ChatBubbleRegister.INSTANCE);
    private static final EntityDataAccessor<String> BACKPACK_TYPE = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<ItemStack> BACKPACK_ITEM_SHOW = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<String> BACKPACK_FLUID = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.STRING);

    private static final String INVULNERABLE_TAG = "Invulnerable";
    private static final String SCHEDULE_MODE_TAG = "MaidScheduleMode";
    private static final String BACKPACK_DATA_TAG = "MaidBackpackData";
    private static final String STRUCTURE_SPAWN_TAG = "StructureSpawn";

    public static boolean canInsertItem(ItemStack stack) {
        Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (key != null && MaidConfig.MAID_BACKPACK_BLACKLIST.get().contains(key.toString())) {
            return false;
        }
        return stack.getItem().canFitInsideContainerItems();
    }

    private final MaidProfileManager profileManager = new MaidProfileManager(this);
    private final MaidTaskManager taskManager = new MaidTaskManager(this);
    private final MaidStatsManager statsManager = new MaidStatsManager(this);
    private final MaidItemManager itemManager = new MaidItemManager(this);
    private final MaidParticleManager particleManager = new MaidParticleManager(this);
    private final MaidWorldInteractionManager worldInteractionManager = new MaidWorldInteractionManager(this);
    private final MaidTeleportManager teleportManager = new MaidTeleportManager(this);
    private final MaidAnimationManager animationManager = new MaidAnimationManager(this);
    private final MaidConfigManager configManager = new MaidConfigManager(this);
    private final MaidGameManager gameManager = new MaidGameManager(this);

    public final ItemStack[] handItemsForAnimation = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};


    private final MaidKillRecordManager killRecordManager = new MaidKillRecordManager();
    private final ChatBubbleManager chatBubbleManager = new ChatBubbleManager(this);
    private final FavorabilityManager favorabilityManager = new FavorabilityManager(this);
    private final MaidSwimManager swimManager = new MaidSwimManager(this);
    // 控制不同的 navigation 切换的条件以及切换后变更女仆相关的 AI 控制参数
    private final MaidNavigationManager navigationManager;
    private final MaidAIChatManager aiChatManager;
    private final SchedulePos schedulePos;
    private final ItemCooldowns cooldowns;

    private int pickupSoundCount = 5;

    // 是否为预览用实体
    public final boolean previewEntity;

    public boolean guiOpening = false;
    public MaidFishingHook fishing = null;

    public MaidRenderState renderState = MaidRenderState.ENTITY;

    /**
     * 用于方便特殊动画播放的变量，目前仅支持捡雪球
     */
    public int animationId = 0;
    public long animationRecordTime = -1L;
    public boolean shouldReset = false;

    private List<SendEffectPackage.EffectData> effects = Lists.newArrayList();
    IMaidTask task = TaskManager.getIdleTask();
    private IMaidBackpack backpack = BackpackManager.getEmptyBackpack();
    private int playerHurtSoundCount = 120;
    private int backpackDelay = 0;
    private int passiveUseShieldTick = 0;
    private @Nullable IBackpackData backpackData = null;

    /**
     * 女仆现在可以在前哨站生成，那么会打上这个标签
     */
    private boolean structureSpawn = false;
    /**
     * 女仆主动爬行标志位，用于管控女仆当前时刻需不需要攀爬
     */
    private boolean canClimb = false;
    /**
     * 一个记录女仆已经生成墓碑的变量，避免死亡重复生成墓碑
     */
    private boolean alreadyDropped = false;

    /**
     * 爬梯的计时器，用于在爬梯后的一段时间内禁用摔落伤害
     */
    private int climbFallDelayTicks = 0;

    protected EntityMaid(EntityType<EntityMaid> type, Level world) {
        super(type, world);
        this.aiChatManager = new MaidAIChatManager(this);

        // 尝试修复 https://github.com/TartaricAcid/TouhouLittleMaid/issues/631
        ResourceKey<Level> dimension = Objects.requireNonNullElse(world.dimension(), Level.OVERWORLD);
        this.schedulePos = new SchedulePos(BlockPos.ZERO, dimension.identifier());

        this.moveControl = new MaidMoveControl(this);
        this.navigationManager = new MaidNavigationManager(this);

        this.cooldowns = new ItemCooldowns();

        this.previewEntity = EntityCacheUtil.creatingPreviewEntity();

        // 启用实体持久化，也许能解决难以复现的女仆实体丢失问题
        this.setPersistenceRequired();
    }

    public EntityMaid(Level worldIn) {
        this(TYPE, worldIn);
    }

    @Override
    public MaidConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public MaidAnimationManager getAnimationManager() {
        return animationManager;
    }

    @Override
    public MaidProfileManager getProfileManager() {
        return profileManager;
    }

    @Override
    public MaidParticleManager getParticleManager() {
        return particleManager;
    }

    @Override
    public MaidItemManager getItemManager() {
        return itemManager;
    }

    @Override
    public MaidWorldInteractionManager getWorldInteractionManager() {
        return worldInteractionManager;
    }

    @Override
    public MaidTeleportManager getTeleportManager() {
        return teleportManager;
    }

    @Override
    public MaidTaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public MaidStatsManager getStatsManager() {
        return statsManager;
    }

    @Override
    public MaidGameManager getGameManager() {
        return gameManager;
    }

    @Override
    public MaidSwimManager getSwimManager() {
        return swimManager;
    }

    /**
     * 如果其他模组想要给女仆添加额外属性
     * <p>
     * 可通过 forge 的 EntityAttributeModificationEvent 添加
     */
    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                // 目前仅用于寻路，女仆最大可寻路 64 格
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.SWEEPING_DAMAGE_RATIO)
                // 目前仅用于寻路，女仆最大可寻路 64 格
                .add(Attributes.LUCK)
                // 女仆攻击速度，这个数字表示每秒可施展的攻击次数，会受武器本身的攻击速度影响
                .add(Attributes.ATTACK_SPEED)
                // 用于女仆近战的范围判断
                .add(Attributes.ENTITY_INTERACTION_RANGE, 2)
                // 部分本模组新增属性
                .add(InitAttribute.MAID_USE_ITEM_SPEED)
                .add(InitAttribute.MAID_CROSSBOW_ATTACK_SPEED)
                .add(InitAttribute.MAID_GUN_ATTACK_SPEED)
                .add(InitAttribute.MAID_SHOOT_COOLDOWN)
                .add(InitAttribute.MAID_TRIDENT_COOLDOWN)
                .add(InitAttribute.MAID_PICKUP_RANGE)
                .add(InitAttribute.MAID_PASSIVE_USE_SHIELD_TICK)
                .add(InitAttribute.MAID_HUNGER);
    }

    public static EntityDataAccessor<ChatBubbleDataCollection> getChatBubbleKey() {
        return CHAT_BUBBLE;
    }

    @SuppressWarnings("ConstantValue")
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(DATA_INVULNERABLE, false);
        builder.define(SCHEDULE_MODE, MaidSchedule.DAY);
        builder.define(RESTRICT_CENTER, BlockPos.ZERO);
        builder.define(RESTRICT_RADIUS, MaidConfig.MAID_NON_HOME_RANGE.get());
        builder.define(CHAT_BUBBLE, ChatBubbleDataCollection.getEmptyCollection());
        builder.define(BACKPACK_TYPE, EmptyBackpack.ID.toString());
        builder.define(BACKPACK_ITEM_SHOW, ItemStack.EMPTY);
        builder.define(BACKPACK_FLUID, StringUtils.EMPTY);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
    }

    @Override
    protected PathNavigation createNavigation(Level levelIn) {
        return new MaidPathNavigation(this, levelIn);
    }

    @Override
    @SuppressWarnings("all")
    public Brain<EntityMaid> getBrain() {
        return (Brain<EntityMaid>) super.getBrain();
    }

    @Override
    protected Brain<? extends LivingEntity> makeBrain(Brain.Packed packedBrain) {
        Brain<EntityMaid> brain = BRAIN_PROVIDER.get().makeBrain(this, packedBrain);
        MaidBrain.registerBrainGoals(brain, this);
        return brain;
    }

    public void refreshBrain(ServerLevel serverWorldIn) {
        Brain<EntityMaid> oldBrain = this.getBrain();
        Brain.Packed packed = oldBrain.pack();
        oldBrain.stopAll(serverWorldIn, this);
        //FIXME 此处没复制SensorType。是否影响？
        this.brain = makeBrain(packed);
        MaidBrain.registerBrainGoals(this.getBrain(), this);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        long timeRecord = Util.getNanos();
        Profiler.get().push("maidBrain");
        if (!guiOpening) {
            this.getBrain().tick((ServerLevel) level, this);
        }
        Profiler.get().pop();
        timeRecord = Util.getNanos() - timeRecord;
        if (MAID_AI_TIME_DEBUG.get() && timeRecord > WARNING_TIME_NANOS) {
            double timeMs = timeRecord / 1000000.0;
            BlockPos blockPos = this.blockPosition();
            String taskId = this.getTask().getUid().toString();
            int searchRange = Math.round(this.getHomeRadius());

            TouhouLittleMaid.LOGGER.error("Maid's AI taking too long! Time: {} ms, Pos: ({},{},{}), Task ID: {}, Search Range: {}",
                    timeMs, blockPos.getX(), blockPos.getY(), blockPos.getZ(), taskId, searchRange);
        }
        super.customServerAiStep(level);
    }

    @Override
    public void tick() {
        if (!NeoForge.EVENT_BUS.post(new MaidTickEvent(this)).isCanceled()) {
            super.tick();
            getMaidBauble().fireEvent((b, s) -> {
                b.onTick(this, s);
                return false;
            });
        }

        // 自 1.4.2 版本起强制开启女仆备份机制
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
        if (backpackDelay > 0) {
            backpackDelay--;
        }
        if (playerHurtSoundCount > 0) {
            playerHurtSoundCount--;
        }
        if (climbFallDelayTicks > 0) {
            climbFallDelayTicks--;
            this.fallDistance = 0;
        }
        this.particleManager.spawnPortalParticle();
        this.randomRestoreHealth();
        this.onMaidSleep();
        this.gameManager.tick();
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

    private void onMaidSleep() {
        if (isSleeping()) {
            getSleepingPos().ifPresent(pos -> setPos(pos.getX() + 0.5, pos.getY() + 0.5625, pos.getZ() + 0.5));
            setDeltaMovement(Vec3.ZERO);
            if (!isSilent()) {
                this.setSilent(true);
            }
        } else {
            if (isSilent()) {
                this.setSilent(false);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
        this.navigationManager.tick();
        if (!level.isClientSide()) {
            this.chatBubbleManager.tick();
            if (this.backpackData != null) {
                Profiler.get().push("maidBackpackData");
                this.backpackData.serverTick(this);
                Profiler.get().pop();
            }

            Profiler.get().push("maidFavorability");
            this.favorabilityManager.tick();
            Profiler.get().pop();

            Profiler.get().push("maidSchedulePos");
            this.schedulePos.tick(this);
            Profiler.get().pop();

            Profiler.get().push("maidCooldowns");
            this.cooldowns.tick();
            if (this.passiveUseShieldTick > 0) {
                // 如果没有拿着盾牌，直接取消计时，避免疯狂挥手
                ItemStack offHandItem = this.getItemInHand(InteractionHand.OFF_HAND);
                if (offHandItem.has(DataComponents.BLOCKS_ATTACKS)) {
                    this.passiveUseShieldTick--;
                } else {
                    this.passiveUseShieldTick = 1;
                }
                // 最后 1 tick 取消盾牌
                if (this.passiveUseShieldTick == 1 && this.isUsingItem() && this.getUsedItemHand() == InteractionHand.OFF_HAND) {
                    this.stopUsingItem();
                }
            }
            Profiler.get().pop();
        }
    }

    @Override
    public InteractionResult mobInteract(Player playerIn, InteractionHand hand) {
        // 禁止 fake player 交互女仆
        if (playerIn instanceof FakePlayer) {
            return InteractionResult.PASS;
        }
        if (hand == InteractionHand.MAIN_HAND && isOwnedBy(playerIn)) {
            ItemStack stack = playerIn.getMainHandItem();
            InteractMaidEvent event = new InteractMaidEvent(playerIn, this, stack);
            // 利用短路原理，逐个触发对应的交互事件
            if (NeoForge.EVENT_BUS.post(event).isCanceled()
                || stack.interactLivingEntity(playerIn, this, hand).consumesAction()
                || openMaidGui(playerIn)) {
                return InteractionResult.SUCCESS;
            }
        } else {
            return tameMaid(playerIn.getItemInHand(hand), playerIn);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult tameMaid(ItemStack stack, Player player) {
        MaidNumAttachment cap = player.getData(MAID_NUM);
        if (cap.canAdd() || player.isCreative()) {
            boolean isNormal = !isTame() && getTamedItem().test(stack);
            boolean isNtr = getNtrItem().test(stack);
            if (isNormal || isNtr) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                    cap.add();
                }
                this.tame(player);
                // 清掉寻路，清掉敌对记忆
                this.navigation.stop();
                this.setTarget(null);
                this.brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
                this.level.broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
                this.playSound(InitSounds.MAID_TAMED.get(), 1, 1);
                if (player instanceof ServerPlayer serverPlayer) {
                    InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.TAMED_MAID);
                    if (this.isStructureSpawn()) {
                        InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.TAMED_MAID_FROM_STRUCTURE);
                    }
                }
                // 触发事件
                NeoForge.EVENT_BUS.post(new MaidTamedEvent(this, player, isNtr));
                return InteractionResult.SUCCESS;
            }
        } else {
            if (player instanceof ServerPlayer) {
                MutableComponent msg = Component.translatable("message.touhou_little_maid.owner_maid_num.can_not_add",
                        cap.get(), cap.getMaxNum());
                player.sendSystemMessage(msg);
            }
        }
        return InteractionResult.PASS;
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
        int attackPlusDistance = this.favorabilityManager.getAttackDistancePlusByPoint(this.getFavorability());
        double attackDistance = this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + attackPlusDistance;
        return this.distanceTo(target) < attackDistance;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        MaidHurtTarget.Pre event = new MaidHurtTarget.Pre(this, target);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return true;
        }

        // 调用饰品的攻击
        getMaidBauble().fireEvent((b, s) -> {
            b.onMeleeAttack(this, s, target);
            return false;
        });

        boolean result = super.doHurtTarget(level, target);
        if (result) {
            // 尝试使用横扫之刃
            this.doSweepHurt(target);
            // 调用 hurtEnemy 来实现耐久消耗和部分其他功能
            ItemStack mainHandItem = this.getMainHandItem();
            Item item = mainHandItem.getItem();
            if (target instanceof LivingEntity livingEntity) {
                item.hurtEnemy(mainHandItem, livingEntity, this);
                item.postHurtEnemy(mainHandItem, livingEntity, this);
            }
        }

        MaidHurtTarget.Post postEvent = new MaidHurtTarget.Post(this, target, result);
        NeoForge.EVENT_BUS.post(postEvent);

        // 部分 task 有额外伤害
        if (this.getTask() instanceof IAttackTask attackTask && attackTask.hasExtraAttack(this, target)) {
            boolean extraResult = attackTask.doExtraAttack(this, target);
            return result && extraResult;
        }
        return result;
    }

    private void doSweepHurt(Entity target) {
        ItemStack mainhandItem = this.getItemInHand(InteractionHand.MAIN_HAND);
        boolean canSweep = mainhandItem.canPerformAction(ItemAbilities.SWORD_SWEEP);
        float sweepingDamageRatio = (float) this.getAttributes().getValue(Attributes.SWEEPING_DAMAGE_RATIO);
        if (canSweep && sweepingDamageRatio > 0) {
            float baseDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float sweepDamage = 1.0f + sweepingDamageRatio * baseDamage;
            AABB sweepRange = this.getFavorabilityManager().getSweepRange(target, this.getFavorability());
            List<LivingEntity> hurtEntities = this.level.getEntitiesOfClass(LivingEntity.class, sweepRange);
            for (LivingEntity entity : hurtEntities) {
                if (entity != this && entity != target && !this.isAlliedTo(entity) && canAttack(entity) && wantsToAttack(entity, getOwner())) {
                    float posX = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
                    float posY = -Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
                    entity.knockback(0.4, posX, posY);
                    entity.hurt(this.damageSources().mobAttack(this), sweepDamage);
                }
            }
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1, 1);
            this.particleManager.spawnSweepAttackParticle();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (NeoForge.EVENT_BUS.post(new MaidAttackEvent(this, source, amount)).isCanceled()) {
            return false;
        }
        if (source.getEntity() instanceof Player player && this.isAlliedTo(player)) {
            // 主人和同 Team 玩家对自己女仆的伤害数值为 1/5，最大为 2
            amount = Mth.clamp(amount / 5, 0, 2);
            return super.hurtServer(level, source, amount);
        }
        // 使用盾牌
        if (source.is(DamageTypeTags.IS_PROJECTILE) && this.canUseShield()) {
            boolean isUsingShield = this.isUsingItem() && this.getUsedItemHand() == InteractionHand.OFF_HAND;
            if (!isUsingShield) {
                this.startUsingItem(InteractionHand.OFF_HAND);
                // 使用五秒的盾牌
                AttributeInstance attribute = this.getAttribute(InitAttribute.MAID_PASSIVE_USE_SHIELD_TICK);
                if (attribute != null) {
                    this.passiveUseShieldTick = (int) attribute.getValue();
                } else {
                    this.passiveUseShieldTick = 100;
                }
            }
        }
        return super.hurtServer(level, source, amount);
    }

    /**
     * 重新复写父类方法，添加上自己的 Event
     */
    @Override
    @SuppressWarnings("UnstableApiUsage")
    protected void actuallyHurt(ServerLevel level, DamageSource damageSrc, float damageAmount) {
        if (!this.isInvulnerableTo(level, damageSrc) && this.damageContainers != null) {
            DamageContainer peek = this.damageContainers.peek();

            // 获取盔甲减伤后的数值
            float armorAbsorb = this.getDamageAfterArmorAbsorb(damageSrc, peek.getNewDamage());
            peek.setReduction(DamageContainer.Reduction.ARMOR, peek.getNewDamage() - armorAbsorb);

            // 获取抗性提升减伤后的数值
            this.getDamageAfterMagicAbsorb(damageSrc, peek.getNewDamage());

            // 获取事件减伤效果
            MaidHurtEvent maidHurtEvent = new MaidHurtEvent(this, damageSrc, peek.getNewDamage());
            damageAmount = NeoForge.EVENT_BUS.post(maidHurtEvent).isCanceled() ? 0 : maidHurtEvent.getAmount();
            peek.setReduction(DamageContainer.Reduction.ABSORPTION, peek.getNewDamage() - damageAmount);

            // NeoForge 事件也来一套
            float damage = onLivingDamagePre(this, peek);
            peek.setReduction(DamageContainer.Reduction.ABSORPTION, Math.min(this.getAbsorptionAmount(), damage));

            // 总减伤效果，用于玩家信息统计
            float damageDealtAbsorbed = Math.min(damage, peek.getReduction(DamageContainer.Reduction.ABSORPTION));
            this.setAbsorptionAmount(Math.max(0, this.getAbsorptionAmount() - damageDealtAbsorbed));
            if (0 < damageDealtAbsorbed && damageDealtAbsorbed < 3.5 && damageSrc.getEntity() instanceof ServerPlayer player) {
                player.awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(damageDealtAbsorbed * 10));
            }

            // 饰品
            MutableFloat newDamage = new MutableFloat(peek.getNewDamage());
            boolean baubleCancel = getMaidBauble().fireEvent((b, s) -> b.onInjured(this, s, damageSrc, newDamage));
            float damageAfterAbsorption = newDamage.getValue();
            // 如果饰品取消了事件，那么也不触发后续内容了
            if (baubleCancel || damageAfterAbsorption <= 0) {
                return;
            }

            // 再来一次事件
            MaidDamageEvent maidDamageEvent = new MaidDamageEvent(this, damageSrc, damageAfterAbsorption);
            damageAfterAbsorption = NeoForge.EVENT_BUS.post(maidDamageEvent).isCanceled() ? 0 : maidDamageEvent.getAmount();

            // 最终运用实际伤害
            if (damageAfterAbsorption != 0) {
                this.getCombatTracker().recordDamage(damageSrc, damageAfterAbsorption);
                this.setHealth(this.getHealth() - damageAfterAbsorption);
                this.gameEvent(GameEvent.ENTITY_DAMAGE);
                this.onDamageTaken(peek);
            }

            // NeoForge 事件也来一套
            onLivingDamagePost(this, peek);
        }
    }

    @Override
    protected void handlePortal() {
        if (this.level instanceof ServerLevel && !this.isRemoved() && this.portalProcess != null) {
            if (TeleportHelper.teleport(this)) {
                this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 1, true, false));
                this.setPortalCooldown();
            }
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (this.getOwnerUUID() != null) {
            MaidWorldData data = MaidWorldData.get(this.level);
            if (data != null) {
                data.removeInfo(this);
            }
        }
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        if (!this.level.isClientSide() && this.isAlive() && this.getOwnerUUID() != null) {
            MaidWorldData data = MaidWorldData.get(this.level);
            if (data != null) {
                data.addInfo(this);
            }
        }
    }

    @Override
    public void die(DamageSource cause) {
        boolean baubleCancel = getMaidBauble().fireEvent((b, s) -> b.onDeath(this, s, cause));
        if (!baubleCancel && !NeoForge.EVENT_BUS.post(new MaidDeathEvent(this, cause)).isCanceled()) {
            // 清除死亡时需要清除的内容
            this.clearFire();
            this.setTicksFrozen(0);
            this.setSharedFlagOnFire(false);
            this.removeAllEffects();
            // 最后父类方法
            super.die(cause);
            // 额外发送女仆所处坐标
            this.sendMaidPos();
        }
    }

    private void sendMaidPos() {
        if (this.dead && this.level instanceof ServerLevel level
            && level.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES)
            && this.getOwner() instanceof ServerPlayer serverPlayer) {
            // 支持旅行地图格式
            // [name:"name", x:-136, y:36, z:48, dim:minecraft:the_nether]
            BlockPos blockPos = this.blockPosition();
            String name = Identifier.parse(this.getModelId()).getPath();
            String msg = """
                    [name:"%s", x:%d, y:%d, z:%d, dim:%s]""".formatted(
                    name,
                    blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                    this.level.dimension().identifier().toString()
            );
            OutgoingChatMessage message = OutgoingChatMessage.create(PlayerChatMessage.system(msg));
            serverPlayer.sendChatMessage(message, false, ChatType.bind(ChatType.CHAT, serverPlayer));
        }
    }

    public boolean canPickup(Entity pickupEntity) {
        return canPickup(pickupEntity, false);
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

    // 弩在装载时的 tryLoadProjectiles 方法会从这里拿到需要装填的物品
    @Override
    public ItemStack getProjectile(ItemStack weaponStack) {
        // 烟花只检查副手：优先检查副手有没有烟花
        if (this.getOffhandItem().getItem() instanceof FireworkRocketItem) {
            return this.getOffhandItem();
        }
        if (!(this.getMainHandItem().getItem() instanceof ProjectileWeaponItem weaponItem)) {
            return ItemStack.EMPTY;
        }
        var handler = this.getAvailableInv(true);
        int slot = ItemsUtil.findStackSlot(handler, weaponItem.getAllSupportedProjectiles());
        if (slot < 0) {
            // 不存在时，返回空
            return ItemStack.EMPTY;
        } else {
            // 拿到弹药物品
            return ItemUtil.getStack(handler, slot);
        }
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt lightning) {
        super.thunderHit(world, lightning);
        if (!isStruckByLightning()) {
            double beforeMaxHealth = this.getAttributeBaseValue(Attributes.MAX_HEALTH);
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(beforeMaxHealth + 20);
            setStruckByLightning(true);
            if (this.getOwner() instanceof ServerPlayer serverPlayer) {
                InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.LIGHTNING_BOLT);
                if (this.getMaxHealth() >= 100) {
                    InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.MAID_100_HEALTHY);
                }
            }
        }
    }

    @Override
    protected void hurtArmor(DamageSource damageSource, float damage) {
        this.doHurtEquipment(damageSource, damage, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        IMaidTask maidTask = this.getTask();
        if (maidTask instanceof IRangedAttackTask rangedAttackTask) {
            // 调用饰品的攻击
            getMaidBauble().fireEvent((b, s) -> {
                b.onRangedAttack(this, s, rangedAttackTask);
                return false;
            });
            rangedAttackTask.performRangedAttack(this, target, distanceFactor);
        }
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        return target.getType() != EntityType.ARMOR_STAND;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.getTask() instanceof IAttackTask attackTask) {
            return attackTask.canAttack(this, target);
        }
        return super.canAttack(target);
    }

    /**
     * 女仆在危险情况（比如附近有苦力怕）下是否应该停止骑乘或待命状态
     */
    public boolean shouldLeaveMountOrSitForDanger() {
        // 如果女仆和玩家骑乘同一个扫帚
        Entity vehicle = this.getVehicle();
        if (vehicle != null && vehicle.getControllingPassenger() instanceof Player) {
            return false;
        }
        return true;
    }

    /**
     * 用于物品的耐久损失
     */
    public void hurtAndBreak(ItemStack stack, int amount) {
        if (this.level instanceof ServerLevel serverLevel) {
            stack.hurtAndBreak(amount, serverLevel, this, stackIn -> NetworkHandler.sendToNearby(this, new ItemBreakPackage(this.getId(), stackIn.getDefaultInstance())));
        }
    }

    private void randomRestoreHealth() {
        if (this.getHealth() < this.getMaxHealth() && random.nextFloat() < 0.0025) {
            this.heal(1);
            this.spawnRestoreHealthParticle(random.nextInt(3) + 7);
        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        itemManager.addAdditionalSaveData(output);
        output.store(INVULNERABLE_TAG, Codec.BOOL, getIsInvulnerable());
        output.store(SCHEDULE_MODE_TAG, Codec.STRING, getSchedule().name());
        output.store(MAID_BACKPACK_TYPE, Codec.STRING, getMaidBackpackType().getId().toString());
        output.store(STRUCTURE_SPAWN_TAG, Codec.BOOL, this.structureSpawn);
        this.favorabilityManager.addAdditionalSaveData(output);
        this.schedulePos.save(output);
        if (this.backpackData != null) {
            this.backpackData.save(output.child(BACKPACK_DATA_TAG), this);
        }
        this.killRecordManager.addAdditionalSaveData(output);
        this.aiChatManager.saveValue(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        input.read(SCHEDULE_MODE_TAG, Codec.STRING).ifPresent(s -> setSchedule(MaidSchedule.valueOf(s)));
        itemManager.readAdditionalSaveData(input);

        input.read(INVULNERABLE_TAG, Codec.BOOL).ifPresent(this::setEntityInvulnerable);
        input.read(STRUCTURE_SPAWN_TAG, Codec.BOOL).ifPresent(v -> this.structureSpawn = v);
        //FIXME NbtUtils.readBlockPos migration for RESTRICT_CENTER_TAG archive migration

        input.read(MAID_BACKPACK_TYPE, Codec.STRING).ifPresent(idStr -> {
            Identifier id = Identifier.parse(idStr);
            IMaidBackpack backpack = BackpackManager.findBackpack(id).orElse(BackpackManager.getEmptyBackpack());
            setMaidBackpackType(backpack);
            if (this.backpackData != null) {
                this.backpackData.load(input.childOrEmpty(BACKPACK_DATA_TAG), this);
            }
        });

        this.favorabilityManager.readAdditionalSaveData(input);
        this.schedulePos.load(input, this);
        this.setBackpackShowItem(ItemUtil.getStack(itemManager.getMaidInv(), MaidBackpackHandler.BACKPACK_ITEM_SLOT));
        this.killRecordManager.readAdditionalSaveData(input);
        this.aiChatManager.loadValue(input);
    }

    public boolean openMaidGui(Player player) {
        return openMaidGui(player, TabIndex.MAIN);
    }

    public boolean openMaidGui(Player player, int tabIndex) {
        if (player instanceof ServerPlayer serverPlayer && !this.isSleeping()) {
            this.navigation.stop();
            final int id = getId();
            MenuProvider guiProvider = getGuiProvider(tabIndex);
            serverPlayer.openMenu(guiProvider, buffer -> buffer.writeInt(id));
        }
        return true;
    }

    private MenuProvider getGuiProvider(int tabIndex) {
        return switch (tabIndex) {
            case TabIndex.TASK_CONFIG -> task.getTaskConfigGuiProvider(this);
            case TabIndex.MAID_CONFIG -> MaidConfigContainer.create(getId());
            case TabIndex.BAUBLE -> BaubleContainer.create(this);
            case TabIndex.CURIOS -> CuriosCompat.create(this);
            default -> this.getMaidBackpackType().getGuiProvider(getId());
        };
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
        if (this.getOwnerUUID() != null /* && !PetBedDrop.hasPetBedPos(this) */) {
            // 掉出世界的判断
            Vec3 position = Vec3.atBottomCenterOf(blockPosition());
            // 防止卡在基岩里？
            if (this.getY() < this.level.getMaxY() + 5) {
                position = new Vec3(position.x, this.level.getMinY() + 5, position.z);
            }
            if (this.getY() > this.level.getMaxY()) {
                position = new Vec3(position.x, this.level.getMaxY(), position.z);
            }
            EntityTombstone tombstone = new EntityTombstone(level, this.getOwnerUUID(), position);
            tombstone.setMaidName(this.getDisplayName());

            itemManager.addItemsToTomb(tombstone);

            // 事件触发，既可以阻断墓碑生成，也可以修改墓碑内容
            MaidTombstoneEvent tombstoneEvent = new MaidTombstoneEvent(this, tombstone);
            if (NeoForge.EVENT_BUS.post(tombstoneEvent).isCanceled()) {
                // 如果事件被取消了，那么就不生成墓碑了
                return;
            }

            // 全局记录
            MaidWorldData maidWorldData = MaidWorldData.get(level);
            if (maidWorldData != null) {
                maidWorldData.addTombstones(this, tombstone);
            }

            // 记录墓碑已经生成，避免重复生成
            alreadyDropped = true;
            level.addFreshEntity(tombstone);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        // TODO: 尝试修复可能存在的目标生成丢失问题，可能会有问题
        if (reason == RemovalReason.KILLED && !alreadyDropped) {
            // 女仆被指令杀后也正常生成墓碑
            if (this.level instanceof ServerLevel level) {
                this.dropEquipment(level);
            }
        }
        super.remove(reason);
    }

    @Override
    protected void completeUsingItem() {
        this.getSwimManager().resetEatBreatheItem();
        super.completeUsingItem();
        this.itemManager.backCurrentHandItemStack(this);
    }

    //FIXME eat方法已经被Consumer取代。事件需要寻找替代品或者对应移除
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
        // 优先事件系统
        MaidTypeNameEvent typeNameEvent = new MaidTypeNameEvent(this);
        NeoForge.EVENT_BUS.post(typeNameEvent);
        if (typeNameEvent.getTypeName() != null) {
            return typeNameEvent.getTypeName();
        }
        // 然后才是默认模型名
        Optional<MaidModelInfo> info = ServerCustomPackLoader.SERVER_MAID_MODELS.getInfo(getModelId());
        return info.map(maidModelInfo -> ParseI18n.parse(maidModelInfo.getName())).orElseGet(() -> Component.literal(getType().getDescriptionId()));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn) {
        // 为结构生成的女仆添加特殊标签
        if (reason == EntitySpawnReason.STRUCTURE) {
            this.structureSpawn = true;
        }
        int modelSize = ServerCustomPackLoader.SERVER_MAID_MODELS.getModelSize();
        // 这里居然可能为 0
        if (modelSize > 0) {
            int skipRandom = random.nextInt(modelSize);
            Optional<String> modelId = ServerCustomPackLoader.SERVER_MAID_MODELS.getModelIdSet().stream().skip(skipRandom).findFirst();
            return modelId.map(id -> {
                this.setModelId(id);
                return spawnDataIn;
            }).orElse(spawnDataIn);
        }
        return spawnDataIn;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);
        if (!this.level.isClientSide()) {
            NeoForge.EVENT_BUS.post(new MaidEquipEvent(this, slot, stack));
        }
    }

    @Override
    public void onEquipItem(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem) {
        super.onEquipItem(slot, oldItem, newItem);
        if (newItem.isEmpty() || this.firstTick || !slot.isArmor()) {
            return;
        }

        // 触发成就
        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.ANY_EQUIPMENT);
        }

        // 如果是下界合金
        if (isNetheriteArmor(newItem)) {
            // 检查全身装备
            for (EquipmentSlot slotIn : EquipmentSlot.values()) {
                if (!slotIn.isArmor() || slotIn == slot || slotIn == EquipmentSlot.BODY) {
                    continue;
                }
                ItemStack itemBySlot = getItemBySlot(slotIn);
                if (!isNetheriteArmor(itemBySlot)) {
                    return;
                }
            }
            // 触发事件
            if (this.getOwner() instanceof ServerPlayer serverPlayer) {
                InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.ALL_NETHERITE_EQUIPMENT);
            }
        }
    }

    private boolean isNetheriteArmor(ItemStack stack) {
        //FIXME 判断合理?
        if (stack.has(DataComponents.EQUIPPABLE) && stack.has(DataComponents.REPAIRABLE)) {
            return stack.get(DataComponents.REPAIRABLE).isValidRepairItem(Items.NETHERITE_INGOT.getDefaultInstance());
        }
        return false;
    }

    @Override
    public void playSound(SoundEvent soundEvent, float volume, float pitch) {
        if (soundEvent.location().getPath().startsWith("maid") && !level.isClientSide()) {
            NetworkHandler.sendToNearby(this, new PlayMaidSoundPackage(soundEvent.location(), this.getSoundPackId(), this.getId()), 16);
        } else {
            super.playSound(soundEvent, volume, pitch);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (!mayPlaySound()) {
            return null;
        }
        return task.getAmbientSound(this);
    }

    public boolean mayPlaySound() {
        return !NeoForge.EVENT_BUS.post(new MaidPlaySoundEvent(this)).isCanceled();
    }

    public void tryPlayMaidPickupSound() {
        if (mayPlaySound()) {
            pickupSoundCount--;
            if (pickupSoundCount == 0) {
                this.playSound(InitSounds.MAID_ITEM_GET.get(), 1, 1);
                pickupSoundCount = 5;
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (!mayPlaySound()) {
            return null;
        }
        if (damageSourceIn.is(DamageTypeTags.IS_FIRE)) {
            return InitSounds.MAID_HURT_FIRE.get();
        } else if (damageSourceIn.getEntity() instanceof Player) {
            if (playerHurtSoundCount == 0) {
                playerHurtSoundCount = 120;
                return InitSounds.MAID_PLAYER.get();
            } else {
                return null;
            }
        } else {
            return InitSounds.MAID_HURT.get();
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (!mayPlaySound()) {
            return null;
        }
        return InitSounds.MAID_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return 1 + random.nextFloat() * 0.1F;
    }

    @Override
    public float getEyeHeight(Pose pPose) {
        return this.getDimensions(pPose).height() * (isMaidInSittingPose() ? 0.65F : 0.85F);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
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

    /**
     * 给 MaidMeleeAttack 使用，用于判断当前任务是否能够近战
     * <p>
     * 如果返回 true，则表示当前是远程攻击，不是近战攻击
     * FIXME 方法从原版消失了
     */
    public boolean canFireProjectileWeapon(ProjectileWeaponItem shootableItem) {
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
        Vec3 pose = getLegacyLeashOffset(modelId);
        if (pose != null) {
            return pose;
        }
        return super.getLeashOffset();
    }

    @Nullable
    private Vec3 getLegacyLeashOffset(String modelId) {
        var modelOptional = CustomPackLoader.MAID_MODELS.getModel(modelId);
        Optional<MaidModelInfo> infoOptional = CustomPackLoader.MAID_MODELS.getInfo(modelId);
        if (modelOptional.isPresent() && infoOptional.isPresent()) {
            var model = modelOptional.get();
            float renderEntityScale = infoOptional.get().getRenderEntityScale();

            BedrockPart arm = null;
            HumanoidArm armSide = HumanoidArm.RIGHT;
            if (model.hasRightArm()) {
                arm = model.getRightArm();
            } else if (model.hasLeftArm()) {
                arm = model.getLeftArm();
                armSide = HumanoidArm.LEFT;
            }

            if (arm != null) {
                BedrockPart positioningModel = model.getArmPositioningModel(armSide);
                Vector3f positionVec;
                // TODO: getTranslateAndRotateVector3f() 在 BedrockPart 中已被移除
                // 需要使用新的变换获取方式（可能是 x/y/z 字段 + rotationDegreesX/Y/Z 组合）
                if (positioningModel != null) {
                    // positionVec = positioningModel.getTranslateAndRotateVector3f();
                    positionVec = new Vector3f(0, 0.5f, 0);  // 临时默认值
                } else {
                    positionVec = new Vector3f(0, 0.5f, 0);
                }
                // Vector3f armVec = arm.getTranslateAndRotateVector3f();
                Vector3f armVec = new Vector3f(0, 10, 0);  // 临时默认值
                Vector3f pose = armVec.add(positionVec);
                return new Vec3(pose.x() * renderEntityScale, (1.5 - pose.y) * renderEntityScale, pose.z() * renderEntityScale);
            }

            if (model.hasHead()) {
                BedrockPart head = model.getHead();
                return new Vec3(head.x * renderEntityScale, (1.5 - head.y / 16) * renderEntityScale, head.z * renderEntityScale);
            }
        }
        return null;
    }

    @Override
    protected void updateUsingItem(ItemStack usingItem) {
        // 处理问题 https://github.com/TartaricAcid/TouhouLittleMaid/issues/1003
        // 检测女仆是否处于异常的进食状态：正在使用物品但手中物品不是可正常使用状态下的物品
        if (this.isUsingItem()) {
            ItemStack currentItem = this.getUseItem();
            // 如果正在使用物品但该物品无法继续使用（例如食物已被移除），则强制停止使用
            if (currentItem.isEmpty() || currentItem.getUseDuration(this) <= 0) {
                this.stopUsingItem();
                return;
            }
        }

        if (!usingItem.isEmpty()) {
            AttributeInstance attribute = this.getAttribute(InitAttribute.MAID_USE_ITEM_SPEED);
            if (attribute != null) {
                // MAID_USE_ITEM_SPEED 默认是 1
                // 故这里减去属性值再加 1，保证属性值为 1 时行为和原版一致
                this.useItemRemaining = this.useItemRemaining - (int) attribute.getValue() + 1;
            }
        }
        super.updateUsingItem(usingItem);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        // 修正睡觉时渲染问题，默认 64 格内渲染
        double range = 64.0 * getViewScale();
        return distance < range * range;
    }

    @Override
    public void startSleeping(BlockPos pPos) {
        super.startSleeping(pPos);
        this.setHealth(this.getMaxHealth());
        this.favorabilityManager.apply(Type.SLEEP);
        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.MAID_SLEEP);
        }
    }

    public void setBackpackDelay() {
        backpackDelay = 20;
    }

    public boolean backpackHasDelay() {
        return backpackDelay > 0;
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
        this.entityData.set(RESTRICT_CENTER, pos);
        this.entityData.set(RESTRICT_RADIUS, distance);
    }

    @Override
    public BlockPos getHomePosition() {
        return this.entityData.get(RESTRICT_CENTER);
    }

    @Override
    public int getHomeRadius() {
        return this.entityData.get(RESTRICT_RADIUS);
    }

    @Override
    public void clearHome() {
        this.schedulePos.clear(this);
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

    public boolean canBrainMoving() {
        return !this.isMaidInSittingPose() && !this.isPassenger() && !this.isSleeping() && !this.isLeashed();
    }

    public String getBackpackFluid() {
        return this.entityData.get(BACKPACK_FLUID);
    }

    public void setBackpackFluid(String fluidName) {
        this.entityData.set(BACKPACK_FLUID, fluidName);
    }

    public MaidSchedule getSchedule() {
        return this.entityData.get(SCHEDULE_MODE);
    }

    public void setSchedule(MaidSchedule schedule) {
        this.entityData.set(SCHEDULE_MODE, schedule);
        if (this.level instanceof ServerLevel) {
            this.refreshBrain((ServerLevel) this.level);
        }
    }

    public Activity getScheduleDetail() {
        //TODO 检查是否正确
        return level.environmentAttributes().getValue(this.getSchedule().getEnvironmentAttribute(), blockPosition());
    }

    public SchedulePos getSchedulePos() {
        return schedulePos;
    }

    public ItemStack getBackpackShowItem() {
        return this.entityData.get(BACKPACK_ITEM_SHOW);
    }

    public void setBackpackShowItem(ItemStack stack) {
        this.entityData.set(BACKPACK_ITEM_SHOW, stack);
    }

    public IMaidBackpack getMaidBackpackType() {
        Identifier id = Identifier.parse(entityData.get(BACKPACK_TYPE));
        return BackpackManager.findBackpack(id).orElse(BackpackManager.getEmptyBackpack());
    }

    public boolean hasBackpack() {
        return !(this.getMaidBackpackType() instanceof EmptyBackpack);
    }

    public void setMaidBackpackType(IMaidBackpack backpack) {
        if (backpack == this.backpack) {
            return;
        }
        this.backpack = backpack;
        if (this.backpack.hasBackpackData()) {
            this.backpackData = this.backpack.getBackpackData(this);
        } else {
            this.backpackData = null;
        }
        this.entityData.set(BACKPACK_TYPE, backpack.getId().toString());
    }

    public IBackpackData getBackpackData() {
        return backpackData;
    }

    public boolean getIsInvulnerable() {
        return this.entityData.get(DATA_INVULNERABLE);
    }

    public void setEntityInvulnerable(boolean isInvulnerable) {
        super.setInvulnerable(isInvulnerable);
        this.entityData.set(DATA_INVULNERABLE, isInvulnerable);
    }

    @Override
    public void setInSittingPose(boolean inSittingPose) {
        super.setInSittingPose(inSittingPose);
        setOrderedToSit(inSittingPose);
    }

    @Override
    public float getLuck() {
        return (float) this.getAttributeValue(Attributes.LUCK);
    }

    public MaidKillRecordManager getKillRecordManager() {
        return killRecordManager;
    }

    public boolean hasFishingHook() {
        return this.fishing != null;
    }

    public boolean isStructureSpawn() {
        return structureSpawn;
    }

    public List<SendEffectPackage.EffectData> getEffects() {
        return effects;
    }

    public void setEffects(List<SendEffectPackage.EffectData> effects) {
        this.effects = effects;
    }

    public FavorabilityManager getFavorabilityManager() {
        return favorabilityManager;
    }

    @SuppressWarnings("all")
    public Ingredient getTamedItem() {
        // 可以被配置文件和 tag 同时修改
        Ingredient configIngredient = getConfigIngredient(MaidConfig.MAID_TAMED_ITEM.get(), Items.CAKE);
        Ingredient tagIngredient = Ingredient.of(registryAccess().get(TagItem.MAID_TAMED_ITEM).get());
        return merge(Lists.newArrayList(configIngredient, tagIngredient));
    }

    private Ingredient merge(Collection<Ingredient> parts) {
        return Ingredient.of(parts.stream().flatMap(i -> i.getValues().stream()).map(Holder::value));
    }

    @SuppressWarnings("all")
    public Ingredient getTemptationItem() {
        return getConfigIngredient(MaidConfig.MAID_TEMPTATION_ITEM.get(), Items.CAKE);
    }

    @SuppressWarnings("all")
    public static Ingredient getNtrItem() {
        return Ingredient.of(InitItems.OWNER_CONVERSION_TOOL.get());
    }

    private static Ingredient getConfigIngredient(String config, Item defaultItem) {
        if (config.startsWith(MaidConfig.TAG_PREFIX)) {
            Identifier key = Identifier.parse(config.substring(1));
            TagKey<Item> tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), key);
            return Ingredient.of(BuiltInRegistries.ITEM.get(tagKey).get());
        } else {
            Identifier key = Identifier.parse(config);
            if (BuiltInRegistries.ITEM.containsKey(key)) {
                return Ingredient.of(BuiltInRegistries.ITEM.get(key).get().value());
            }
        }
        return Ingredient.of(defaultItem);
    }

    public ItemStack[] getHandItemsForAnimation() {
        return handItemsForAnimation;
    }

    @Override
    public Vec3 handleOnClimbable(Vec3 deltaMovement) {
        Vec3 oriDelta = super.handleOnClimbable(deltaMovement);
        // 主动爬行过程中严禁水平方向偏移，防止摔伤，y轴保持原样
        if (this.isCanClimb()) {
            Vec3 vec3 = this.position();
            if (vec3.x() % 1 != 0.5D || vec3.z() % 1 != 0.5) {
                BlockPos currentPosition = this.blockPosition().mutable();
                Vec3 centerPos = Vec3.atBottomCenterOf(currentPosition);
                this.moveOrInterpolateTo(new Vec3(centerPos.x, vec3.y(), centerPos.z));
            }
            oriDelta = new Vec3(0, oriDelta.y, 0);
        }
        return oriDelta;
    }

    /**
     * 爬梯子状态加上路径判断
     */
    @Override
    public boolean onClimbable() {
        boolean result = false;
        Path path = this.navigation.getPath();
        if (path != null && !path.isDone()) {
            // 女仆是要爬梯子而不是路过梯子，那么也就意味着当前节点的前后必有一个节点是同坐标的
            for (int i = Math.max(0, path.getNextNodeIndex() - 3); i < Math.min(path.getNodeCount(), path.getNextNodeIndex() + 3) - 1; i++) {
                BlockPos pos1 = path.getNodePos(i);
                BlockPos pos2 = path.getNodePos(i + 1);
                if (pos1.getX() == pos2.getX() && pos1.getZ() == pos2.getZ()) {
                    result = true;
                    break;
                }
            }
        }
        if (result) {
            result = super.onClimbable();
            // 用作脚手架和卡在梯子顶部的特判，避免女仆卡在脚手架顶上
            if (!result && !this.isSpectator()) {
                Optional<BlockPos> ladderPos = CommonHooks.isLivingOnLadder(
                        level.getBlockState(blockPosition().below()),
                        level(), blockPosition().below(), this);
                if (ladderPos.isPresent()) {
                    result = true;
                }
            }
        }
        if (result) {
            // 爬梯后一段时间禁用摔落伤害
            this.climbFallDelayTicks = 30;
            // 爬梯时，禁止旋转
            this.getLastClimbablePos().ifPresent(climbablePos -> {
                BlockState blockState = this.level.getBlockState(climbablePos);
                blockState.getOptionalValue(HorizontalDirectionalBlock.FACING).ifPresent(direction -> {
                    int yRot = direction.getOpposite().get2DDataValue() * 90;
                    this.setYRot(yRot);
                    this.setYHeadRot(yRot);
                });
            });
        }
        return result;
    }

    /**
     * 略微修改原版的方法，禁用了向上的动力源
     */
    @Override
    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 deltaMovement, float friction) {
        this.moveRelative(this.getFrictionInfluencedSpeed(friction), deltaMovement);
        this.setDeltaMovement(this.handleOnClimbable(this.getDeltaMovement()));
        this.move(MoverType.SELF, this.getDeltaMovement());
        return this.getDeltaMovement();
    }

    public boolean isCanClimb() {
        return canClimb;
    }

    public void setCanClimb(boolean canClimb) {
        this.canClimb = canClimb;
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
        if (isInWater()) {
            if (this.getSwimManager().wantToSwim()) {
                this.moveRelative(0.01F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            } else if (this.getSwimManager().isReadyToLand() || isUnderWater()) {
                super.travel(travelVector.scale(1.2).add(0, 0.5, 0));
            } else {
                super.travel(travelVector.scale(1.2).add(0, 0.05, 0));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return pose == Pose.SWIMMING ? this.getSwimManager().getSwimmingDimensions() : super.getDefaultDimensions(pose);
    }

    @Override
    public void updateSwimming() {
        this.getSwimManager().updateSwimming();
    }

    @Override
    public boolean isVisuallySwimming() {
        return this.isSwimming();
    }

    public boolean canUseShield() {
        ItemStack offhandItem = this.getOffhandItem();
        return offhandItem.has(DataComponents.BLOCKS_ATTACKS) && !this.getCooldowns().isOnCooldown(offhandItem.getItem().getDefaultInstance());
    }

    @Override
    public @Nullable ItemStack getItemBlockingWith() {
        if (!this.useItem.isEmpty()) {
            BlocksAttacks blocksAttacks = this.useItem.get(DataComponents.BLOCKS_ATTACKS);
            if (blocksAttacks != null) {
                return this.useItem;
            }
        }
        return null;
    }

    @Override
    public float applyItemBlocking(ServerLevel level, DamageSource source, float damage) {
        boolean shouldPredicateBlockItemBreaking = isBlocking();
        InteractionHand interactionhand = this.getUsedItemHand();
        float v = super.applyItemBlocking(level, source, damage);
        if (shouldPredicateBlockItemBreaking && this.useItem.isEmpty()) {
            if (interactionhand == InteractionHand.MAIN_HAND) {
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else {
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
            this.useItem = ItemStack.EMPTY;
        }
        return v;
    }

    public ItemCooldowns getCooldowns() {
        return cooldowns;
    }

    public MaidAIChatManager getAiChatManager() {
        return aiChatManager;
    }

    public MaidNavigationManager getNavigationManager() {
        return navigationManager;
    }

    public @Nullable UUID getOwnerUUID() {
        EntityReference<LivingEntity> ownerReference = getOwnerReference();
        return ownerReference == null ? null : ownerReference.getUUID();
    }

    /**
     * 参考自 <a href="https://github.com/Snownee/Companion/blob/1.20-forge/src/main/java/snownee/companion/Hooks.java#L313-L322">Snownee's Companion</a>
     * <p>
     * 更加高效的 owner 寻找方式
     */
    @Nullable
    @Override
    public LivingEntity getOwner() {
        UUID uuid = this.getOwnerUUID();
        if (uuid == null) {
            return null;
        }
        MinecraftServer server = this.level().getServer();
        if (server == null) {
            return this.level().getPlayerByUUID(uuid);
        }
        return server.getPlayerList().getPlayer(uuid);
    }

    public ChatBubbleManager getChatBubbleManager() {
        return chatBubbleManager;
    }


    @Override
    public void spawnItemParticles(ItemStack stack, int amount) {
        particleManager.spawnItemParticles(stack, amount);
    }

    /**
     * 因为部分 idea 插件会检查 Map 类里，这些对象做 key 时，是否重写了 equals 和 hashCode 方法，
     * 故这里必须重写这两个方法，但实际上并不需要修改默认父类的实现
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * 因为部分 idea 插件会检查 Map 类里，这些对象做 key 时，是否重写了 equals 和 hashCode 方法，
     * 故这里必须重写这两个方法，但实际上并不需要修改默认父类的实现
     */
    @Override
    public boolean equals(Object pObject) {
        return super.equals(pObject);
    }
}
