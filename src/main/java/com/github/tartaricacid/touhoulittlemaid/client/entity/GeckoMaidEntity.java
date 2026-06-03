package com.github.tartaricacid.touhoulittlemaid.client.entity;

import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.MolangEventWrapper;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.event.ClientTickEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.RenderContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoLocatorType;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoContainer;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.joml.Math;

import java.util.Optional;
import java.util.function.Consumer;

public class GeckoMaidEntity<T extends EntityMaid> extends AnimatableEntity<T> {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final AttachmentType<GeckoMaidEntity> TYPE = AttachmentType.builder(holder -> {
        if (holder instanceof EntityMaid maid) {
            return new GeckoMaidEntity(maid);
        }
        throw new IllegalArgumentException();
    }).build();

    private final EntityMaid maid;
    private final FloatArrayList headRotBackup = new FloatArrayList(2);
    private MaidModelInfo maidInfo;

    private boolean fireInitEvent = false;
    private IValue wrappedUpdateHandler = null;
    private final BooleanList updateHandlerArgs = new BooleanArrayList(1);

    public GeckoMaidEntity(T maid) {
        super(maid, maid.renderState == MaidRenderState.ENTITY);
        this.maid = maid;
    }

    @Override
    protected GeckoMaidStateTracker<T> createStateTracker(T entity) {
        return new GeckoMaidStateTracker<>(entity);
    }

    @Override
    public GeckoMaidStateTracker<T> getStateTracker() {
        return (GeckoMaidStateTracker<T>) super.getStateTracker();
    }

    @Override
    protected GeckoRenderData createRenderData() {
        return new GeckoMaidRenderData();
    }

    @Override
    @SuppressWarnings("resource")
    protected void extractRenderData(EntityRenderState state, RenderContext ctx, GeckoRenderData data, boolean ticked) {
        super.extractRenderData(state, ctx, data, ticked);
        // 懒得泛型了，凑合用
        var maidState = (EntityMaidRenderState) state;
        var maidData = (GeckoMaidRenderData) data;
        if (((Object) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(state)) instanceof EntityMaidRenderer renderer) {
            data.overlayUV = LivingEntityRenderer.getOverlayCoords(maidState, renderer.getWhiteOverlayProgress(maidState));
        }
        maidData.climbRotation = Float.NaN;
        if (entity.onClimbable()) {
            Optional<BlockPos> climbablePos = entity.getLastClimbablePos();
            if (climbablePos.isPresent()) {
                BlockState blockState = entity.level().getBlockState(climbablePos.get());
                Optional<Direction> optionalValue = blockState.getOptionalValue(HorizontalDirectionalBlock.FACING);
                optionalValue.ifPresent(direction -> maidData.climbRotation = direction.getOpposite().get2DDataValue() * 90);
            }
        }
    }

    @Override
    public boolean isPreviewEntity() {
        return entity.renderState != MaidRenderState.ENTITY;
    }

    @Override
    public boolean asyncUpdate(RenderContext ctx) {
        // 非共享的实体才能异步
        return entity.renderState == MaidRenderState.ENTITY
                || entity.renderState == MaidRenderState.STATUE
                || entity.renderState == MaidRenderState.GARAGE_KIT;
    }

    @Override
    public boolean determinImmutableContext(RenderContext ctx) {
        return entity.renderState != MaidRenderState.ENTITY || super.determinImmutableContext(ctx);
    }

    @Override
    public int getFrameRateLimit() {
        if (entity.renderState == MaidRenderState.ENTITY) {
            return super.getFrameRateLimit();
        } else if (entity.renderState == MaidRenderState.GUI) {
            // gui 预览时使用屏幕刷新率
            return ClientTickEvent.getRefreshRate();
        } else {
            // 雕像和手办固定 30 帧
            return 30;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onSetupAnimationController() {
        var container = getGeckoContainer();
        if (container != null) {
            ((Consumer<GeckoMaidEntity<T>>) container.controllerFactory()).accept(this);
        }
    }

    @Override
    protected void onLoadGeckoContainer(GeckoContainer newModel) {
        super.onLoadGeckoContainer(newModel);
        var updateHandlers = newModel.asset().eventHandlers().get(MolangEventWrapper.MAID_UPDATE);
        if (updateHandlers != null) {
            wrappedUpdateHandler = MolangEventWrapper.wrap(updateHandlers, updateHandlerArgs);
        } else {
            wrappedUpdateHandler = null;
        }
    }

    @Override
    protected void resetGeckoContainer() {
        super.resetGeckoContainer();
        wrappedUpdateHandler = null;
    }

    @Override
    protected void onLoadGeoModel(AnimatedGeoModel model) {
        super.onLoadGeoModel(model);
        var heads = model.locatorGroup(GeoLocatorType.HEAD);
        var headRot = headRotBackup;
        headRot.size(heads.size() * 2);
        for (var i = 0; i < heads.size(); i++) {
            var head = heads.get(i);
            var rot = head.getRotation();
            headRot.set(i * 2, rot.x);
            headRot.set(i * 2 + 1, rot.y);
        }
    }

    @Override
    protected void resetGeoModel() {
        super.resetGeoModel();
        headRotBackup.clear();
        fireInitEvent = true;
    }

    @Override
    protected void codeAnimation(AnimationEvent<? extends AnimatableEntity<T>> event, boolean shouldUpdate) {
        var model = getLoadedGeoModel();
        if (model != null) {
            // 更新头部旋转
            var heads = model.locatorGroup(GeoLocatorType.HEAD);
            var headRotBak = headRotBackup;
            for (var i = 0; i < heads.size(); i++) {
                var head = heads.get(i);
                var rot = head.getRotation();

                if (shouldUpdate) {
                    headRotBak.set(i * 2, rot.x);
                    headRotBak.set(i * 2 + 1, rot.y);
                }

                var data = event.getExtraData();
                rot.x = headRotBak.getFloat(i * 2) + Math.toRadians(data.headPitch);
                rot.y = headRotBak.getFloat(i * 2 + 1) + Math.toRadians(data.netHeadYaw);
            }
        }
    }

    @Override
    protected void recoverLastCodedAnimation(boolean lastFrameUpdated) {
        var model = getLoadedGeoModel();
        if (model != null) {
            var heads = model.locatorGroup(GeoLocatorType.HEAD);
            var headRotBak = headRotBackup;
            for (var i = 0; i < heads.size(); i++) {
                var head = heads.get(i);
                var rot = head.getRotation();
                rot.x = headRotBak.getFloat(i * 2);
                rot.y = headRotBak.getFloat(i * 2 + 1);
            }
        }
    }

    @Override
    protected void preAnimationSetup(float seekTime, boolean shouldTick) {
        super.preAnimationSetup(seekTime, shouldTick);
        if (fireInitEvent) {
            fireInitEvent = false;
            var initEvent = getEventHandler(MolangEventWrapper.MAID_INIT);
            if (initEvent != null) {
                executeMolangExp(MolangEventWrapper.wrap(initEvent), true, true, null);
            }
        }
        if (wrappedUpdateHandler != null) {
            updateHandlerArgs.set(0, shouldTick);
            executeMolangExp(wrappedUpdateHandler, true, true, null);
        }
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public MaidModelInfo getMaidInfo() {
        return maidInfo;
    }

    public void setMaidInfo(MaidModelInfo info) {
        waitForAsyncUpdate();
        if (this.maidInfo != info) {
            this.maidInfo = info;
            setModelId(this.maidInfo.getModelId());
        }
    }

    @Override
    public void reset() {
        waitForAsyncUpdate();
        super.reset();
        this.maidInfo = null;
    }
}
