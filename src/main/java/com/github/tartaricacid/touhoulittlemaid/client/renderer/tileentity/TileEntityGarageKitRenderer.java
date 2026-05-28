package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.GarageKitRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityGarageKit;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ExecutionException;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry.STATUE_BASE;
import static com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil.clearMaidDataResidue;

public class TileEntityGarageKitRenderer implements BlockEntityRenderer<TileEntityGarageKit, GarageKitRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/block/statue_base.png");
    private final SimpleBedrockModel<Unit> baseModel;

    public TileEntityGarageKitRenderer(BlockEntityRendererProvider.Context context) {
        baseModel = InternalBedrockModelRegistry.getModel(STATUE_BASE);
    }

    @Override
    public GarageKitRenderState createRenderState() {
        return new GarageKitRenderState();
    }

    @Override
    public void extractRenderState(TileEntityGarageKit te, GarageKitRenderState state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTick, cameraPos, breakProgress);
        state.facing = te.getFacing();
        state.extraData = te.getExtraData();
        state.entityRenderState = null;

        // 提取实体渲染状态
        if (state.extraData.isEmpty()) {
            return;
        }
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        EntityType.byString(state.extraData.getString("id").orElse("")).ifPresent(type -> {
            try {
                extractEntityRenderState(te, state, state.extraData, world, type, partialTick);
            } catch (ExecutionException e) {
                TouhouLittleMaid.LOGGER.error("Failed to extract garage kit entity render state", e);
            }
        });
    }

    @SuppressWarnings("unchecked,rawtypes")
    private void extractEntityRenderState(TileEntityGarageKit te, GarageKitRenderState state, CompoundTag data,
                                          Level world, EntityType<?> type, float partialTick) throws ExecutionException {
        Entity entity;
        if (type.equals(InitEntities.MAID.get())) {
            long posId = te.getBlockPos().asLong();
            entity = EntityCacheUtil.STATUE_CACHE.get(posId, () -> new EntityMaid(world));
        } else {
            entity = EntityCacheUtil.getEntity((EntityType) type, (l, r) ->
                    new EntityMaid(l), world, EntitySpawnReason.LOAD);
        }

        entity.load(TagValueInput.create(ProblemReporter.DISCARDING, entity.registryAccess(), data));
        if (entity instanceof EntityMaid maid) {
            clearMaidDataResidue(maid, true);
            maid.setModelId(data.getStringOr("model_id","touhou_little_maid:hakurei_reimu"));
            maid.renderState = MaidRenderState.GARAGE_KIT;
            maid.tickCount = 0;
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        state.entityRenderState = dispatcher.extractEntity(entity, partialTick);
    }

    @Override
    public void submit(GarageKitRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        // 渲染底座模型
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(1, 1.5, 1);
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

    private void renderEntityPart(GarageKitRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.entityRenderState == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(1, 0.21328125, 1);
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
        dispatcher.submit(state.entityRenderState, camera, 0, 0, 0, poseStack, collector);
        poseStack.popPose();
    }
}
