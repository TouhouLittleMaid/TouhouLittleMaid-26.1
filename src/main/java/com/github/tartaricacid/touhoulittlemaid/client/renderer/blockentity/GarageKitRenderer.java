package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityGarageKit;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.GarageKitRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry.STATUE_BASE;
import static com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil.clearMaidDataResidue;
import static net.minecraft.util.ProblemReporter.DISCARDING;

public class GarageKitRenderer implements BlockEntityRenderer<BlockEntityGarageKit, GarageKitRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/statue_base.png");

    private final EntityRenderDispatcher dispatcher;
    private final SimpleBedrockModel<Unit> baseModel;

    public GarageKitRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.entityRenderer();
        this.baseModel = InternalBedrockModelRegistry.getModel(STATUE_BASE);
    }

    @Override
    public GarageKitRenderState createRenderState() {
        return new GarageKitRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityGarageKit kit, GarageKitRenderState state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(kit, state, partialTick, cameraPos, breakProgress);

        state.facing = kit.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        state.extraData = kit.getExtraData();
        state.entityRenderState = null;

        // 提取实体渲染状态
        if (state.extraData.isEmpty()) {
            return;
        }
        Level world = kit.getLevel();
        if (world == null) {
            return;
        }
        Optional<String> id = state.extraData.getString("id");
        if (id.isEmpty()) {
            return;
        }
        EntityType.byString(id.get()).ifPresent(type -> {
            try {
                this.extractEntityRenderState(kit, state, state.extraData, world, type, partialTick);
            } catch (ExecutionException e) {
                TouhouLittleMaid.LOGGER.error("Failed to extract garage kit entity render state", e);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private void extractEntityRenderState(BlockEntityGarageKit kit, GarageKitRenderState state, CompoundTag data,
                                          Level level, EntityType type, float partialTick) throws ExecutionException {
        Entity entity;
        if (type.equals(InitEntities.MAID.get())) {
            long key = kit.getBlockPos().asLong();
            entity = EntityCacheUtil.STATUE_CACHE.get(key, () -> new EntityMaid(level));
        } else {
            entity = EntityCacheUtil.ENTITY_CACHE.get(type, () -> {
                Entity e = type.create(level, EntitySpawnReason.COMMAND);
                return Objects.requireNonNullElseGet(e, () -> new EntityMaid(level));
            });
        }

        RegistryAccess access = entity.registryAccess();
        ValueInput valueInput = TagValueInput.create(DISCARDING, access, data);
        entity.load(valueInput);

        if (entity instanceof EntityMaid maid) {
            clearMaidDataResidue(maid, true);
            maid.renderState = MaidRenderState.GARAGE_KIT;
            maid.tickCount = 0;
        }

        state.entityRenderState = dispatcher.extractEntity(entity, partialTick);
        state.entityRenderState.lightCoords = state.lightCoords;
        state.entityRenderState.partialTick = 0;
    }

    @Override
    public void submit(GarageKitRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        // 渲染底座模型
        this.renderBase(state, poseStack, collector);
        // 渲染实体预览
        this.renderEntity(state, poseStack, collector, camera);
    }

    private void renderBase(GarageKitRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(1, 1.5, 1);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        collector.submitModel(
                this.baseModel, Unit.INSTANCE, poseStack, RenderTypes.entityCutout(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress
        );
        poseStack.popPose();
    }

    private void renderEntity(GarageKitRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
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

        dispatcher.submit(state.entityRenderState, camera, 0, 0, 0, poseStack, collector);
        poseStack.popPose();
    }
}
