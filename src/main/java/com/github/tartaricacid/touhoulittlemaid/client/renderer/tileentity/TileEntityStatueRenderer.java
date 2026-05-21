package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.StatueRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.compat.ysm.YsmCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityStatue;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader.STATUE_BASE;
import static com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil.clearMaidDataResidue;

public class TileEntityStatueRenderer implements BlockEntityRenderer<TileEntityStatue, StatueRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/block/statue_base.png");
    private final @Nullable SimpleBedrockModel<EntityRenderState> baseModel;

    public TileEntityStatueRenderer(BlockEntityRendererProvider.Context context) {
        baseModel = BedrockModelLoader.getModel(STATUE_BASE);
    }

    @Override
    public StatueRenderState createRenderState() {
        return new StatueRenderState();
    }

    @Override
    public void extractRenderState(TileEntityStatue te, StatueRenderState state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTick, cameraPos, breakProgress);
        state.isCoreBlock = te.isCoreBlock();
        state.facing = te.getFacing();
        state.size = te.getSize().getScale();
        state.statueSize = te.getSize();
        state.extraMaidData = te.getExtraMaidData();
        state.entityRenderState = null;

        // 提取实体渲染状态
        if (state.extraMaidData == null) {
            return;
        }
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        EntityType.byString(state.extraMaidData.getString("id").orElse("")).ifPresent(type -> {
            try {
                extractEntityRenderState(te, state, state.extraMaidData, world, type, partialTick);
            } catch (ExecutionException e) {
                TouhouLittleMaid.LOGGER.error("Failed to extract statue entity render state", e);
            }
        });
    }

    private void extractEntityRenderState(TileEntityStatue te, StatueRenderState state, CompoundTag data,
                                          Level world, EntityType<?> type, float partialTick) throws ExecutionException {
        Entity entity;
        if (type.equals(InitEntities.MAID.get())) {
            long posId = te.getBlockPos().asLong();
            entity = EntityCacheUtil.STATUE_CACHE.get(posId, () -> new EntityMaid(world));
        } else {
            entity = EntityCacheUtil.ENTITY_CACHE.get(type, () -> {
                Entity e = type.create(world, EntitySpawnReason.LOAD);
                return Objects.requireNonNullElseGet(e, () -> new EntityMaid(world));
            });
        }

        entity.load(TagValueInput.create(ProblemReporter.DISCARDING, entity.registryAccess(), data));
        if (entity instanceof EntityMaid maid) {
            clearMaidDataResidue(maid, true);
            maid.renderState = MaidRenderState.STATUE;
            if (YsmCompat.isInstalled() && maid.isYsmModel()) {
                maid.tickCount = (int) world.getGameTime();
            } else {
                maid.tickCount = 0;
            }
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        state.entityRenderState = dispatcher.extractEntity(entity, partialTick);
    }

    @Override
    public void submit(StatueRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.isCoreBlock) {
            return;
        }
        if (baseModel == null) {
            return;
        }

        // 渲染底座模型
        poseStack.pushPose();
        setBaseTranslateAndPose(state, poseStack);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            baseModel.renderToBuffer(poseStack, buffer, state.lightCoords, 0);
        });
        poseStack.popPose();

        // 渲染实体预览
        if (state.entityRenderState != null) {
            renderEntityPart(state, poseStack, collector, camera);
        }
    }

    private void renderEntityPart(StatueRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.entityRenderState == null) {
            return;
        }

        float size = state.statueSize.getScale();
        float offset = 0;
        if (state.statueSize == TileEntityStatue.Size.MIDDLE) {
            offset = 1.0f / 4.0f;
        } else if (state.statueSize == TileEntityStatue.Size.BIG) {
            offset = 1.0f / 3.0f;
        }

        poseStack.pushPose();
        poseStack.scale(size, size, size);
        poseStack.translate(0.5 / size, 0.21328125, 0.5 / size);
        switch (state.facing) {
            case EAST:
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                break;
            case WEST:
                poseStack.mulPose(Axis.YP.rotationDegrees(270));
                break;
            case SOUTH:
                break;
            case NORTH:
            default:
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                break;
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.submit(state.entityRenderState, camera, offset, 0, -offset, poseStack, collector);
        poseStack.popPose();
    }

    private void setBaseTranslateAndPose(StatueRenderState state, PoseStack poseStack) {
        float size = state.size;
        float offset = 0;
        if (state.statueSize == TileEntityStatue.Size.MIDDLE) {
            offset = 1.0f / 4.0f;
        } else if (state.statueSize == TileEntityStatue.Size.BIG) {
            offset = 1.0f / 3.0f;
        }

        switch (state.facing) {
            case EAST:
                poseStack.translate(-offset * size, 0, -offset * size);
                break;
            case NORTH:
                poseStack.translate(-offset * size, 0, offset * size);
                break;
            case WEST:
                poseStack.translate(offset * size, 0, offset * size);
                break;
            case SOUTH:
                poseStack.translate(offset * size, 0, -offset * size);
                break;
            default:
                poseStack.translate(0, 0, 0);
        }
        poseStack.scale(size, size, size);
        poseStack.translate(0.5 / size, 1.5, 0.5 / size);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
