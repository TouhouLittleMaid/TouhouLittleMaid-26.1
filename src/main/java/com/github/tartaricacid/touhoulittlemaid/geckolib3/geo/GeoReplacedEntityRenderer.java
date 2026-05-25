package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.extended.LivingEntityRendererAccessor;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.EModelRenderCycle;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.IRenderCycle;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

@SuppressWarnings({"unchecked"})
public abstract class GeoReplacedEntityRenderer<TEntity extends LivingEntity, TState extends LivingEntityRenderState, TData extends GeckoRenderData>
        extends LivingEntityRenderer<TEntity, TState, EntityModel<TState>> implements IGeoRenderer<TState, TData> {
    protected final List<GeoLayerRenderer<? super TState, ? super TData>> layerRenderers = new ObjectArrayList<>();
    private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

    public GeoReplacedEntityRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel<TState>) new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5F);
    }

    @Override
    public void submit(@NotNull TState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, @NotNull CameraRenderState camera) {
        submit(state, getGeckoRenderData(state), poseStack, submitNodeCollector, camera);
    }

    public void submit(@NotNull TState state, @Nullable TData data, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, @NotNull CameraRenderState camera) {
        if (NeoForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(state, this, state.partialTick, poseStack, submitNodeCollector)).isCanceled()) {
            return;
        }

        if (data != null && !data.isClosed()) {
            final var modelData = data.modelData;
            final var ctx = data.ctx;

            setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
            poseStack.pushPose();

            if (state.pose == Pose.SLEEPING) {
                Direction direction = state.bedOrientation;
                if (direction != null) {
                    float eyeOffset = state.eyeHeight - 0.1f;
                    poseStack.translate(-direction.getStepX() * eyeOffset, 0, -direction.getStepZ() * eyeOffset);
                }
            }

            float scale = state.scale;
            poseStack.scale(scale, scale, scale);

            setupRotations(state, poseStack, modelData.lerpBodyRot, scale);
            scale(state, poseStack);
            poseStack.translate(0, 0.01f, 0);

            var bodyVisible = this.isBodyVisible(state);
            var glowing = state.appearsGlowing();
            var renderType = getRenderType(data, bodyVisible, glowing);
            var isSpectator = state instanceof AvatarRenderState avatarRenderState && avatarRenderState.isSpectator;

            preSubmit(state, data, ctx, poseStack, submitNodeCollector);
            if (renderType != null) {
                submit(state, data, ctx, poseStack, submitNodeCollector, renderType);
            }
            if (!isSpectator) {
                renderLayer(submitNodeCollector, poseStack, state, data, camera);
            }
            poseStack.popPose();
        }
        ((LivingEntityRendererAccessor) this).tlm$renderNameTag(state, poseStack, submitNodeCollector, camera);
        NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.RenderLivingEvent.Post<>(state, this, state.partialTick, poseStack, submitNodeCollector));
    }


    protected void renderLayer(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, TState state, TData data, CameraRenderState camera) {
        for (var layerRenderer : this.layerRenderers) {
            layerRenderer.submit(submitNodeCollector, poseStack, state, data, camera);
        }
    }

    @Override
    protected void setupRotations(@NonNull TState state, @NonNull PoseStack poseStack, float bodyRot, float entityScale) {
        var deathTime = state.deathTime;
        boolean autoSpineAttach = state.isAutoSpinAttack;
        if (deathTime > 0) {
            state.deathTime = 0;
        }
        if (autoSpineAttach) {
            state.isAutoSpinAttack = false;
        }

        super.setupRotations(state, poseStack, bodyRot, entityScale);

        if (deathTime > 0) {
            state.deathTime = deathTime;
        }
        if (autoSpineAttach) {
            state.isAutoSpinAttack = true;
        }
    }

    @Nullable
    public abstract TData getGeckoRenderData(TState state);

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull TState state) {
        var result = getGeckoRenderData(state);
        if (result != null) {
            return result.texture;
        }
        return MissingTextureAtlasSprite.getLocation();
    }

    @Override
    @NotNull
    public IRenderCycle getCurrentModelRenderCycle() {
        return this.currentModelRenderCycle;
    }

    @Override
    public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
        this.currentModelRenderCycle = currentModelRenderCycle;
    }

    public final void addLayer(GeoLayerRenderer<? super TState, ? super TData> layer) {
        this.layerRenderers.add(layer);
    }
}
