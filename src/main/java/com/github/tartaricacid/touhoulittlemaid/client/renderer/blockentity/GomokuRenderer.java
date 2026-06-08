package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Point;
import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Statue;
import com.github.tartaricacid.touhoulittlemaid.block.BlockGomoku;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityGomoku;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.GomokuRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class GomokuRenderer implements BlockEntityRenderer<BlockEntityGomoku, GomokuRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/gomoku.png");
    private static final Identifier BLACK_PIECE_TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/gomoku_black_piece.png");
    private static final Identifier WHITE_PIECE_TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/gomoku_white_piece.png");

    private static final int TIPS_RENDER_DISTANCE_SQ = 16 * 16;
    private static final int PIECE_RENDER_DISTANCE_SQ = 24 * 24;

    private static final MutableComponent RESET_TIP_COMPONENT = Component
            .translatable("message.touhou_little_maid.gomoku.reset")
            .withStyle(ChatFormatting.UNDERLINE)
            .withStyle(ChatFormatting.AQUA);

    private static final MutableComponent WIN_TIP_COMPONENT = Component
            .translatable("message.touhou_little_maid.gomoku.win")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.DARK_PURPLE);

    private static final MutableComponent LOSE_TIP_COMPONENT = Component
            .translatable("message.touhou_little_maid.gomoku.lose")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.DARK_PURPLE);

    private static final MutableComponent DRAW_TIP_COMPONENT = Component
            .translatable("message.touhou_little_maid.gomoku.draw")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.DARK_PURPLE);

    private static final FormattedCharSequence LATEST_CHESS_ARROW = FormattedCharSequence.forward("▼", Style.EMPTY);

    private final Font font;
    private final SimpleBedrockModel<Unit> boardModel;
    private final SimpleBedrockModel<Unit> pieceModel;

    public GomokuRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
        this.boardModel = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.GOMOKU);
        this.pieceModel = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.GOMOKU_PIECE);
    }

    @Override
    public GomokuRenderState createRenderState() {
        return new GomokuRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityGomoku te, GomokuRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
        state.facing = te.getBlockState().getValue(BlockGomoku.FACING);

        BlockPos pos = te.getBlockPos();
        Vec3 centerPos = Vec3.atCenterOf(pos);
        state.inPieceRenderDistance = cameraPosition.distanceToSqr(centerPos) < PIECE_RENDER_DISTANCE_SQ;
        state.inTipsRenderDistance = cameraPosition.distanceToSqr(centerPos) < TIPS_RENDER_DISTANCE_SQ;

        state.chessData = te.getChessData();
        state.latestChessPoint = te.getLatestChessPoint();
        state.statue = te.getStatue();
        state.isPlayerTurn = te.isPlayerTurn();
        state.chessCounter = te.getChessCounter();
    }

    @Override
    public void submit(GomokuRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        this.renderChessboard(poseStack, collector, state);
        this.renderPiece(state, poseStack, collector);
        this.renderLatestChessTips(state, poseStack, collector, camera);
        this.renderTipsText(state, poseStack, collector, camera);
    }

    private void renderLatestChessTips(GomokuRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.latestChessPoint.equals(Point.NULL) || !state.inPieceRenderDistance) {
            return;
        }

        Point point = state.latestChessPoint;
        float width = -this.font.width(LATEST_CHESS_ARROW) / 2f + 0.5f;

        poseStack.pushPose();
        poseStack.translate(-0.42, 0.25, -0.42);
        poseStack.translate(point.x * 0.1316, 0, point.y * 0.1316);
        poseStack.mulPose(Axis.YN.rotationDegrees(180 + camera.yRot));
        poseStack.scale(0.015625F, -0.015625F, 0.015625F);

        collector.submitText(
                poseStack, width, -1.5f, LATEST_CHESS_ARROW, false,
                Font.DisplayMode.POLYGON_OFFSET, state.lightCoords,
                0xFFFF0000, 0, 0
        );

        poseStack.popPose();
    }

    private void renderChessboard(PoseStack poseStack, SubmitNodeCollector collector, GomokuRenderState state) {
        Direction facing = state.facing;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.get2DDataValue() * 90));
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(180));
        }

        collector.submitModel(
                this.boardModel, Unit.INSTANCE, poseStack, RenderTypes.entityCutout(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress
        );

        poseStack.popPose();
    }

    private void renderPiece(GomokuRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (!state.inPieceRenderDistance) {
            return;
        }

        byte[][] chessData = state.chessData;

        poseStack.pushPose();

        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));

        poseStack.pushPose();
        poseStack.translate(0.92, -0.1, -1.055);
        RenderType blackRenderType = RenderTypes.entityCutout(BLACK_PIECE_TEXTURE);
        collector.submitCustomGeometry(poseStack, blackRenderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            this.submitPiece(state, poseStack, buffer, chessData, Point.BLACK);
            poseStack.popPose();
        });
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.92, -0.1, -1.055);
        RenderType whiteRenderType = RenderTypes.entityCutout(WHITE_PIECE_TEXTURE);
        collector.submitCustomGeometry(poseStack, whiteRenderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            this.submitPiece(state, poseStack, buffer, chessData, Point.WHITE);
            poseStack.popPose();
        });
        poseStack.popPose();

        poseStack.popPose();
    }

    private void submitPiece(GomokuRenderState state, PoseStack poseStack, VertexConsumer buffer, byte[][] chessData, int pieceType) {
        for (byte[] row : chessData) {
            for (int j = 0; j < chessData[0].length; j++) {
                poseStack.translate(0, 0, 0.1316);
                if (row[j] == pieceType) {
                    this.pieceModel.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                }
            }
            poseStack.translate(-0.1316, 0, -1.974);
        }
    }

    private void renderTipsText(GomokuRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.statue == Statue.IN_PROGRESS || !state.inTipsRenderDistance) {
            return;
        }

        FormattedCharSequence loseSeq = this.getLoseTipText(state);
        FormattedCharSequence roundSeq = this.getRoundTipText(state.chessCounter);
        FormattedCharSequence resetSeq = RESET_TIP_COMPONENT.getVisualOrderText();

        float loseTipsWidth = -this.font.width(loseSeq) / 2f;
        float resetTipsWidth = -this.font.width(resetSeq) / 2f;
        float roundTipsWidth = -this.font.width(roundSeq) / 2f;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.75, 0.5);
        poseStack.mulPose(Axis.YN.rotationDegrees(180 + camera.yRot));
        poseStack.mulPose(Axis.XN.rotationDegrees(camera.xRot));
        poseStack.scale(0.03F, -0.03F, 0.03F);

        collector.submitText(
                poseStack, loseTipsWidth, -10, loseSeq, true,
                Font.DisplayMode.POLYGON_OFFSET, state.lightCoords,
                0xFFFFFFFF, 0, 0
        );

        poseStack.scale(0.5F, 0.5F, 0.5F);

        collector.submitText(
                poseStack, roundTipsWidth, -30, roundSeq, true,
                Font.DisplayMode.POLYGON_OFFSET, state.lightCoords,
                0xFFFFFFFF, 0, 0
        );

        collector.submitText(
                poseStack, resetTipsWidth, 0, resetSeq, true,
                Font.DisplayMode.POLYGON_OFFSET, state.lightCoords,
                0xFFFFFFFF, 0, 0
        );

        poseStack.popPose();
    }

    private FormattedCharSequence getRoundTipText(int count) {
        MutableComponent roundText = Component
                .translatable("message.touhou_little_maid.gomoku.round", count)
                .withStyle(ChatFormatting.WHITE);

        MutableComponent preRoundIcon = Component
                .literal("⏹ ")
                .withStyle(ChatFormatting.GREEN);

        MutableComponent postRoundIcon = Component
                .literal(" ⏹")
                .withStyle(ChatFormatting.GREEN);

        return preRoundIcon
                .append(roundText)
                .append(postRoundIcon)
                .getVisualOrderText();
    }

    private FormattedCharSequence getLoseTipText(GomokuRenderState state) {
        if (state.statue == Statue.WIN) {
            if (state.isPlayerTurn) {
                return WIN_TIP_COMPONENT.getVisualOrderText();
            } else {
                return LOSE_TIP_COMPONENT.getVisualOrderText();
            }
        }
        return DRAW_TIP_COMPONENT.getVisualOrderText();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BlockEntityGomoku blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return RenderHelper.getAABB(
                pos.offset(-3, 0, -3),
                pos.offset(3, 1, 3)
        );
    }
}
