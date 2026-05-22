package com.github.tartaricacid.touhoulittlemaid.client.entity;

import com.github.tartaricacid.touhoulittlemaid.api.animation.IMagicCastingState;
import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.RenderContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoModel;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.joml.Vector2f;

import java.util.Optional;
import java.util.function.Consumer;

public class GeckoMaidEntity<T extends Mob> extends AnimatableEntity<T> implements IGeoEntity {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final AttachmentType<GeckoMaidEntity> TYPE = AttachmentType.builder(holder -> {
        if (holder instanceof Mob mob) {
            IMaid maid = IMaid.convert(mob);
            if (maid != null) {
                return new GeckoMaidEntity(mob, maid);
            }
        }
        throw new IllegalArgumentException();
    }).build();

    private final IMaid maid;
    private final Vector2f headRotBackup = new Vector2f();
    private MaidModelInfo maidInfo;

    /**
     * 上一次的魔法咏唱阶段，用于判断阶段过渡时的动画行为
     */
    private IMagicCastingState.CastingPhase lastCastingPhase = IMagicCastingState.CastingPhase.NONE;

    public GeckoMaidEntity(T mob, IMaid maid) {
        super(mob, true);
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
        maidData.showBackpack = maidInfo.isShowBackpack();
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
    protected void onLoadGeoModel(AnimatedGeoModel model) {
        super.onLoadGeoModel(model);
        if (model != null && model.head() != null) {
            var headRot = model.head().getRotation();
            headRotBackup.set(headRot.x, headRot.y);
        }
    }

    @Override
    protected void resetGeoModel() {
        super.resetGeoModel();
        headRotBackup.set(0);
    }

    @Override
    protected void codeAnimation(AnimationEvent<? extends AnimatableEntity<T>> event, boolean shouldUpdate) {
        var model = getLoadedGeoModel();
        if (model != null && model.head() != null) {
            var headRot = model.head().getRotation();
            // 更新头部旋转
            if (shouldUpdate) {
                headRotBackup.set(headRot.x, headRot.y);
            }
            var data = event.getExtraData();
            headRot.x = headRotBackup.x + (float) Math.toRadians(data.headPitch);
            headRot.y = headRotBackup.y + (float) Math.toRadians(data.netHeadYaw);
        }
    }

    @Override
    protected void recoverLastCodedAnimation(boolean lastFrameUpdated) {
        var model = getLoadedGeoModel();
        if (model != null && model.head() != null) {
            var headRot = model.head().getRotation();
            headRot.x = headRotBackup.x;
            headRot.y = headRotBackup.y;
        }
    }

    @Override
    public IMaid getMaid() {
        return maid;
    }

    @Override
    public MaidModelInfo getMaidInfo() {
        return maidInfo;
    }

    @Override
    public void setMaidInfo(MaidModelInfo info) {
        if (this.maidInfo != info) {
            this.maidInfo = info;
            setModelId(this.maidInfo.getModelId());
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.maidInfo = null;
    }

    @Override
    public void setYsmModel(String modelId, String texture) {
    }

    @Override
    public void updateRoamingVars(Object2FloatOpenHashMap<String> roamingVars) {
    }

    public IMagicCastingState.CastingPhase getLastCastingPhase() {
        return lastCastingPhase;
    }

    public void setLastCastingPhase(IMagicCastingState.CastingPhase phase) {
        this.lastCastingPhase = phase;
    }
}
