package com.github.tartaricacid.touhoulittlemaid.geckolib3.core;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.GeckoUpdateManager;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.MolangEventWrapper;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.PhysicsManager;
import com.github.tartaricacid.touhoulittlemaid.event.ClientTickEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.Animation;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.controller.AnimationControllerData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.GeckoAsyncTask;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.GeckoSyncTask;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.GeckoUpdateTask;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.manager.AnimationData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.DebugSource;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.MolangContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor.AnimationProcessor;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor.IBoneView;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.util.RateLimiter;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.RenderContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.RenderContextManager;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.GeoModelState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.GeoModelStateExtractor;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.model.provider.data.EntityModelData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoContainer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoLibCache;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.data.SoundFormat;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.stream.AudioStreamProvider;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.stream.VorbisAudioStream;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class AnimatableEntity<TEntity extends Entity> {
    private final AnimationData manager = new AnimationData();

    private GeoModelState mainModelState = new GeoModelState();
    private final ConcurrentHashMap<RenderContext, ReferenceArrayList<GeoModelState>> modelStateCache = new ConcurrentHashMap<>();

    protected final TEntity entity;
    private final AnimationProcessor<TEntity> animationProcessor;
    private final RateLimiter rateLimiter;
    private final EntityStateTracker<TEntity> stateTracker;
    private final PhysicsManager physicsManager;
    @Nullable
    private PhysicsManager alterPhysicsManager;

    private Identifier modelId;
    private GeckoContainer currentGeckoContainer;
    private AnimatedGeoModel currentModel;
    @Nullable
    private List<IValue> deferHandler;
    private List<IValue> syncHandler;
    private int lastCheckUpdateTime;

    // 这两个变量不跟随动画一起更新，所以不能放进 stateTracker
    private float lastFrameTime = -1;
    private boolean lastMutableRender;
    private boolean currentFrameTicked;
    private boolean currentFrameShouldTick;

    private boolean lastFrameRendered = true;
    private boolean currentFrameRendered = false;
    private boolean lastFrameUpdated;
    private float seekTime;

    @Nullable
    private GeckoUpdateTask<?> lastUpdateTask;

    /**
     * 存储 Coded 动画控制器的动画播放状态，用于一些 molang 判断
     */
    private final Map<String, AnimationState> codedAnimationStates = Maps.newHashMap();

    protected AnimatableEntity(TEntity entity, boolean keepUpdate) {
        this.entity = entity;
        this.animationProcessor = new AnimationProcessor<>(this);
        this.rateLimiter = new RateLimiter();
        this.stateTracker = createStateTracker(entity);
        this.physicsManager = new PhysicsManager();
        this.rateLimiter.setLimit(getFrameRateLimit());
        if (keepUpdate) {
            GeckoUpdateManager.add(this);
        }
    }

    protected EntityStateTracker<TEntity> createStateTracker(TEntity entity) {
        return new EntityStateTracker<>(entity);
    }

    public EntityStateTracker<TEntity> getStateTracker() {
        return stateTracker;
    }

    public float getSeekTime() {
        return seekTime;
    }

    @SuppressWarnings("rawtypes")
    public void addAnimationController(IAnimationController value) {
        this.manager.addAnimationController(value);
    }

    public AnimationData getAnimationData() {
        return manager;
    }

    public Identifier getTextureLocation() {
        if (currentGeckoContainer != null) {
            return currentGeckoContainer.texture();
        }
        return MissingTextureAtlasSprite.getLocation();
    }

    public boolean isModelPresent() {
        return currentGeckoContainer != null;
    }

    @Nullable
    public Animation getAnimation(String name) {
        if (currentGeckoContainer != null) {
            return currentGeckoContainer.animation().animations().get(name);
        }
        return null;
    }

    public boolean isPreviewEntity() {
        return false;
    }

    public boolean isFakePlayer() {
        return false;
    }

    public Optional<AudioStreamProvider> getSoundStream(String name) {
        if (currentGeckoContainer != null) {
            var soundData = currentGeckoContainer.asset().sounds().get(name);
            if (soundData != null && soundData.soundFormat() == SoundFormat.VORBIS) {
                return Optional.of(() -> new VorbisAudioStream(soundData.byteBuffer()));
            }
        }
        return Optional.empty();
    }

    @Nullable
    public IValue getUserFunction(String name) {
        if (currentGeckoContainer != null) {
            return currentGeckoContainer.asset().userFunctions().get(name);
        }
        return null;
    }

    @Nullable
    public final List<IValue> getEventHandler(String name) {
        if (currentGeckoContainer != null) {
            return currentGeckoContainer.asset().eventHandlers().get(name);
        }
        return null;
    }

    @Nullable
    public AnimationControllerData getAnimationControllerData(String name) {
        if (currentGeckoContainer != null) {
            return currentGeckoContainer.animControllers().get(name);
        }
        return null;
    }

    @Nullable
    public List<IValue> getMolangDeferHandler() {
        return deferHandler;
    }

    public PhysicsManager getPhysicsManager(AnimationEvent<?> event) {
        var ctx = event.getRenderContext();
        if (ctx.immutable()) {
            return physicsManager;
        } else {
            if (alterPhysicsManager == null) {
                alterPhysicsManager = new PhysicsManager();
            }
            return alterPhysicsManager;
        }
    }

    protected float getSwingMotionAniMathHelperreshold() {
        return 0.15f;
    }

    /**
     * 更新动画之前调用，
     * 如果由于频率限制、renderTick 倒退等原因导致动画不更新，则不会调用
     */
    protected void preAnimationSetup(float seekTime, boolean shouldTick) {
    }

    /**
     * 更新动画之后调用，
     * 如果由于频率限制、renderTick 倒退等原因导致动画不更新，则不会调用
     */
    protected void postAnimationSetup(float seekTime, boolean shouldTick) {
    }

    public final TEntity getEntity() {
        return entity;
    }

    @Nullable
    public IBoneView getBone(int boneName) {
        return animationProcessor.getBone(boneName);
    }

    protected boolean allowEmitting() {
        return true;
    }

    public int getFrameRateLimit() {
        var localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null && localPlayer != entity) {
            var localPos = localPlayer.position();
            if (localPos.x != 0 || localPos.y != 0 || localPos.z != 0) {
                // 未渲染：屏幕外、太远、被 EntityCulling 剔除
                if (!lastFrameRendered) {
                    return 10;
                }

                var distance = localPlayer.distanceTo(entity);
                // 超远距离
                if (distance > 56) {
                    return 30;
                }
                // 一般远距离
                if (distance > 40) {
                    return 60;
                }
            }
        }
        return ClientTickEvent.getRefreshRate();
    }

    protected @Nullable GeckoRenderData updateAnimation(EntityRenderState state, RenderContext renderContext) {
        if (this.currentModel == null) {
            return null;
        }
        final Entity entity = this.entity;
        final LivingEntityRenderState livingState = state instanceof LivingEntityRenderState ? (LivingEntityRenderState) state : null;
        int entityTickCount = isPreviewEntity() ? ClientTickEvent.getTickCount() : entity.tickCount;
        float realPartialTicks = state.partialTick != 1f ? state.partialTick : Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
        float limbSwingAmount = 0;
        float limbSwing = 0;

        if (!shouldSit && entity.isAlive() && livingState != null) {
            limbSwingAmount = livingState.walkAnimationSpeed;
            limbSwing = livingState.walkAnimationPos;
            if (livingState.isBaby) {
                limbSwing *= 3.0F;
            }
        }

        EntityModelData entityModelData = new EntityModelData();
        entityModelData.isSitting = shouldSit;

        float lerpBodyRot = 0;
        float netHeadYaw = 0;
        float headPitch = 0;

        if (livingState != null) {
            entityModelData.isChild = livingState.isBaby;
            lerpBodyRot = livingState.bodyRot;
            netHeadYaw = -livingState.yRot;
            headPitch = livingState.xRot;
        } else {
            headPitch = Mth.lerp(state.partialTick, entity.xRotO, entity.getXRot());
        }
/*
        if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity vehicle = (LivingEntity) entity.getVehicle();
            lerpBodyRot = Mth.rotLerp(state.partialTick, vehicle.yBodyRotO, vehicle.yBodyRot);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;
            if (clampedHeadYaw * clampedHeadYaw > 2500f) {
                lerpBodyRot += clampedHeadYaw * 0.2f;
            }
            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }
*/
        entityModelData.rawHeadPitch = headPitch;
        entityModelData.headPitch = -entityModelData.rawHeadPitch;
        entityModelData.rawNetHeadYaw = netHeadYaw;
        entityModelData.netHeadYaw = netHeadYaw;
        entityModelData.lerpBodyRot = lerpBodyRot;
        entityModelData.lerpedAge = entityTickCount + state.partialTick;

        AnimationEvent<AnimatableEntity<TEntity>> event = new AnimationEvent<>(this,
                limbSwing, limbSwingAmount,
                entityTickCount, realPartialTicks,
                (limbSwingAmount <= -getSwingMotionAniMathHelperreshold() || limbSwingAmount <= getSwingMotionAniMathHelperreshold()),
                renderContext,
                entityModelData, state);
        MolangContext<?> ctx = new MolangContext<>(entity, this, event, entityModelData);
        ctx.setDebugSource(getDebugSource());

        var ticked = this.tickAnimation(ctx, event);

        if (!renderContext.offScreen()) {
            currentFrameRendered = true;
            var data = createRenderData();
            data.returnFunc = this::returnModelState;
            data.modelData = event.getExtraData();
            extractRenderData(state, renderContext, data, ticked);
            return data;
        }

        return null;
    }

    protected GeckoRenderData createRenderData() {
        return new GeckoRenderData();
    }

    protected void extractRenderData(EntityRenderState state, RenderContext ctx, GeckoRenderData data, boolean ticked) {
        if (ctx.immutable()) {
            if (ticked) {
                GeoModelStateExtractor.extract(this.currentModel, this.mainModelState);
            }
            data.modelState = this.mainModelState;
        } else {
            var cache = modelStateCache.get(ctx);
            if (cache != null && !cache.isEmpty()) {
                data.modelState = cache.removeLast();
            } else {
                data.modelState = new GeoModelState();
            }
            GeoModelStateExtractor.extract(this.currentModel, data.modelState);
        }

        data.texture = getTextureLocation();
        data.ctx = ctx;
    }

    protected void checkGeckoContainerUpdate() {
        var tickCount = ClientTickEvent.getTickCount();
        if (lastCheckUpdateTime < tickCount) {
            checkGeckoContainerUpdateInner();
            lastCheckUpdateTime = tickCount;
        }
    }

    public final GeckoContainer getGeckoContainer() {
        return currentGeckoContainer;
    }

    protected final void setModelId(Identifier modelId) {
        this.modelId = modelId;
        checkGeckoContainerUpdateInner();
    }

    private void checkGeckoContainerUpdateInner() {
        if (modelId != null) {
            var model = GeckoLibCache.getInstance().getModels().get(modelId);
            if (model != null) {
                if (model != currentGeckoContainer) {
                    currentGeckoContainer = model;
                    onLoadGeckoContainer(currentGeckoContainer);
                    loadGeoModel(model.model(), model.asset().eventHandlers());
                }
            } else {
                resetGeckoContainer();
            }
        }
    }

    protected void onLoadGeckoContainer(GeckoContainer newModel) {
        deferHandler = getEventHandler(MolangEventWrapper.DEFER);
        syncHandler = getEventHandler(MolangEventWrapper.SYNC);
    }

    protected void resetGeckoContainer() {
        currentGeckoContainer = null;
        deferHandler = null;
        syncHandler = null;
        resetGeoModel();
    }

    protected void reset() {
        modelId = null;
        resetGeckoContainer();
    }

    public void returnModelState(RenderContext ctx, GeoModelState modelState) {
        if (modelState != mainModelState) {
            var cache = modelStateCache.computeIfAbsent(ctx, k -> new ReferenceArrayList<>(2));
            if (cache.size() < 2) {
                cache.add(modelState);
            }
        }
    }

    public void molangSync(FloatArrayList args) {
        if (syncHandler != null) {
            executeMolangExp(MolangEventWrapper.wrap(syncHandler, args), true, false, null);
        }
    }

    /**
     * @return ticked
     */
    protected boolean tickAnimation(MolangContext<?> ctx, @NotNull AnimationEvent<AnimatableEntity<TEntity>> animationEvent) {
        var frameTime = animationEvent.getRenderTicks();
        var mutableRender = !animationEvent.getRenderContext().immutable();

        if (frameTime > lastFrameTime) {
            currentFrameTicked = false;
            currentFrameShouldTick = false;
            lastFrameTime = frameTime;
            rateLimiter.setLimit(getFrameRateLimit());
            lastFrameRendered = currentFrameRendered;
            currentFrameRendered = false;
        } else {
            // 目前不允许倒退，可能会影响 replay 的回放
            frameTime = lastFrameTime;
        }

        if (manager.startTick == -1) {
            manager.startTick = frameTime;
        } else {
            float currentTick = frameTime - manager.startTick;
            float deltaTicks = currentTick - manager.lastTick;
            if (deltaTicks > 0f) {
                manager.lastTick = currentTick;
                this.seekTime += deltaTicks;
            }
        }
        animationEvent.setRenderTicks(this.seekTime);

        if (!animationProcessor.isModelEmpty()) {
            // 这坨不要轻易动，除非真的吃透了
            currentFrameShouldTick |= rateLimiter.request(seekTime / 20);
            var shouldUpdate = (currentFrameShouldTick && !currentFrameTicked) || lastMutableRender || mutableRender;
            var shouldTick = (!mutableRender || (seekTime == 0 && !currentFrameTicked)) && currentFrameShouldTick && !currentFrameTicked;
            recoverLastCodedAnimation(lastFrameUpdated);
            if (shouldUpdate) {
                if (shouldTick) {
                    currentFrameTicked = true;
                    stateTracker.update(animationEvent.getEntityTickCount(), this.seekTime, animationEvent.getPartialTick());
                }
                getPhysicsManager(ctx.animationEvent()).update(this.seekTime);
                preAnimationSetup(this.seekTime, shouldTick);
                getAnimationProcessor().tickAnimation(animationEvent, ctx, shouldTick, allowEmitting());
                postAnimationSetup(this.seekTime, shouldTick);
                lastMutableRender = mutableRender;
            }
            codeAnimation(animationEvent, shouldUpdate);
            lastFrameUpdated = shouldUpdate;
            return shouldTick;
        }
        return false;
    }

    protected void codeAnimation(AnimationEvent<? extends AnimatableEntity<TEntity>> animationEvent, boolean shouldUpdate) {
    }

    protected void recoverLastCodedAnimation(boolean lastFrameUpdated) {
    }

    public AnimationProcessor<TEntity> getAnimationProcessor() {
        return this.animationProcessor;
    }

    protected abstract void onSetupAnimationController();

    /**
     * 设置模型
     */
    protected void loadGeoModel(@NotNull GeoModel model, Object2ReferenceMap<String, List<IValue>> eventHandlers) {
        resetGeoModel();
        this.currentModel = new AnimatedGeoModel(model);
        onSetupAnimationController();
        this.animationProcessor.loadModel(currentModel, eventHandlers);
        onLoadGeoModel(this.currentModel);
    }

    protected void reloadGeoModel() {
        if (this.currentModel != null) {
            resetGeoModel();
            loadGeoModel(currentGeckoContainer.model(), currentGeckoContainer.asset().eventHandlers());
        }
    }

    protected void resetGeoModel() {
        if (currentModel != null) {
            currentModel = null;
            animationProcessor.clearModel();
            physicsManager.reset();
            rateLimiter.reset();
            manager.reset();
            stateTracker.reset();
            lastFrameTime = -1;
            lastMutableRender = false;
            currentFrameTicked = false;
            currentFrameShouldTick = false;
            lastFrameRendered = true;
            currentFrameRendered = false;
            lastFrameUpdated = false;
            seekTime = 0;
            codedAnimationStates.clear();
            mainModelState = new GeoModelState();
            modelStateCache.clear();
            alterPhysicsManager = null;
            lastCheckUpdateTime = 0;
        }
    }

    /**
     * 获取当前正在使用的模型
     */
    @Nullable
    public final AnimatedGeoModel getLoadedGeoModel() {
        return currentModel;
    }

    /**
     * 更新当前使用的模型后调用
     */
    protected void onLoadGeoModel(AnimatedGeoModel model) {
    }

    /**
     * extract EntityState 之后若不会额外修改其属性，即为 immutable；
     * mutable 上下文内不会 tick 动画，避免污染状态（但是会重新求值骨骼关键帧 molang）；
     * 未识别的上下文一律视为 mutable。
     */
    public boolean determinImmutableContext(RenderContext ctx) {
        return ctx.level() || ctx.offScreen() || ctx.irisShadow();
    }

    public void executeMolangExp(IValue value, boolean allowEmitting, boolean pre, @Nullable Consumer<String> resultConsumer) {
        animationProcessor.enqueueMolangTask(value, allowEmitting, pre, resultConsumer);
    }

    @Nullable
    public DebugSource getDebugSource() {
        return null;
    }

    @SuppressWarnings("resource")
    public boolean isActive() {
        return Minecraft.getInstance().level == entity.level() && !entity.isRemoved();
    }

    public void setCodedAnimationStates(String controllerName, AnimationState state) {
        this.codedAnimationStates.put(controllerName, state);
    }

    public AnimationState getCodedAnimationStates(String controllerName) {
        return this.codedAnimationStates.getOrDefault(controllerName, AnimationState.IDLE);
    }

    public GeckoUpdateTask<?> createUpdateTask(EntityRenderState state) {
        return createUpdateTask(state, RenderContextManager.extract(this));
    }

    public GeckoUpdateTask<?> createUpdateTask(EntityRenderState state, RenderContext renderContext) {
        RenderSystem.assertOnRenderThread();
        waitForAsyncUpdate();
        checkGeckoContainerUpdate();
        GeckoUpdateManager.recordUpdate(this, renderContext);

        if (!isModelPresent()) {
            return GeckoUpdateTask.nop();
        }

        var async = asyncUpdate(renderContext);
        lastUpdateTask = async ?
                new GeckoAsyncTask<>(() -> updateAnimation(state, renderContext)) :
                new GeckoSyncTask<>(() -> updateAnimation(state, renderContext));
        if (immediateUpdate(renderContext, async)) {
            lastUpdateTask.start();
        }
        return lastUpdateTask;
    }

    public boolean asyncUpdate(RenderContext ctx) {
        return true;
    }

    @Nullable
    public GeckoUpdateTask<?> getLastUpdateTask() {
        return lastUpdateTask;
    }

    public void waitForAsyncUpdate() {
        if (lastUpdateTask != null) {
            try {
                lastUpdateTask.getResult();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean immediateUpdate(RenderContext ctx, boolean async) {
        return ctx.immutable() && !ctx.inventory();
    }
}
