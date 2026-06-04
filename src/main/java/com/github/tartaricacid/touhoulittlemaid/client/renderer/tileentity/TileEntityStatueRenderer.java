package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.StatueRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityStatue;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ExecutionException;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry.STATUE_BASE;
import static com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil.clearMaidDataResidue;

public class TileEntityStatueRenderer implements BlockEntityRenderer<TileEntityStatue, StatueRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/statue_base.png");
    private final SimpleBedrockModel<Unit> baseModel;

    public TileEntityStatueRenderer(BlockEntityRendererProvider.Context context) {
        baseModel = InternalBedrockModelRegistry.getModel(STATUE_BASE);
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

    @SuppressWarnings("unchecked,rawtypes")
    private void extractEntityRenderState(TileEntityStatue te, StatueRenderState state, CompoundTag data,
                                          Level world, EntityType<?> type, float partialTick) throws ExecutionException {
        Entity entity;
        if (type.equals(InitEntities.MAID.get())) {
            long posId = te.getBlockPos().asLong();
            entity = EntityCacheUtil.STATUE_CACHE.get(posId, () -> new EntityMaid(world));
        } else {
            entity = EntityCacheUtil.getEntity((EntityType) type, (l, e) ->
                    new EntityMaid(l), world, EntitySpawnReason.LOAD);
        }

        entity.load(TagValueInput.create(ProblemReporter.DISCARDING, entity.registryAccess(), data));
        if (entity instanceof EntityMaid maid) {
            clearMaidDataResidue(maid, true);
            maid.renderState = MaidRenderState.STATUE;
            maid.tickCount = 0;
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        state.entityRenderState = dispatcher.extractEntity(entity, partialTick);
    }

    @Override
    public void submit(StatueRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.isCoreBlock) {
            return;
        }

        // 渲染底座模型
        poseStack.pushPose();
        setBaseTranslateAndPose(state, poseStack);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            baseModel.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
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

    @Override
    public AABB getRenderBoundingBox(TileEntityStatue blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        float scale = blockEntity.getSize().getScale();
        int size = Math.round(2 * scale);
        int height = Math.round(3 * scale);
        return RenderHelper.getAABB(pos.offset(-size, -1, -size), pos.offset(size, height, size));
    }
}
