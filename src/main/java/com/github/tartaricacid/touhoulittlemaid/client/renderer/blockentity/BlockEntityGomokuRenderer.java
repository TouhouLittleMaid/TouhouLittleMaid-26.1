package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Point;
import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Statue;
import com.github.tartaricacid.touhoulittlemaid.block.BlockGomoku;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.GomokuRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityGomoku;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
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

public class BlockEntityGomokuRenderer implements BlockEntityRenderer<BlockEntityGomoku, GomokuRenderState> {
    private static final Identifier CHECKER_BOARD_TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/gomoku.png");
    private static final Identifier BLACK_PIECE_TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/gomoku_black_piece.png");
    private static final Identifier WHITE_PIECE_TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/gomoku_white_piece.png");
    private static final int TIPS_RENDER_DISTANCE = 16;
    private static final int PIECE_RENDER_DISTANCE = 24;
    private final SimpleBedrockModel<Unit> checkerBoardModel;
    private final SimpleBedrockModel<Unit> pieceModel;
    private final Font font;

    public BlockEntityGomokuRenderer(BlockEntityRendererProvider.Context context) {
        checkerBoardModel = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.GOMOKU);
        pieceModel = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.GOMOKU_PIECE);
        this.font = context.font();
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
        state.inPieceRenderDistance = cameraPosition.distanceToSqr(Vec3.atCenterOf(pos)) < PIECE_RENDER_DISTANCE * PIECE_RENDER_DISTANCE;
        state.inTipsRenderDistance = cameraPosition.distanceToSqr(Vec3.atCenterOf(pos)) < TIPS_RENDER_DISTANCE * TIPS_RENDER_DISTANCE;

        byte[][] chessData = te.getChessData();
        state.chessData = new byte[chessData.length][];
        for (int i = 0; i < chessData.length; i++) {
            state.chessData[i] = chessData[i].clone();
        }

        state.latestChessPoint = te.getLatestChessPoint();
        state.statue = te.getStatue();
        state.isPlayerTurn = te.isPlayerTurn();
        state.chessCounter = te.getChessCounter();
    }

    @Override
    public void submit(GomokuRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        this.renderChessboard(state, poseStack, submitNodeCollector);
        this.renderPiece(state, poseStack, submitNodeCollector);
        this.renderLatestChessTips(state, poseStack, submitNodeCollector, camera);
        this.renderTipsText(state, poseStack, submitNodeCollector, camera);
    }

    private void renderLatestChessTips(GomokuRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.latestChessPoint.equals(Point.NULL) && state.inPieceRenderDistance) {
            Point point = state.latestChessPoint;
            poseStack.pushPose();
            poseStack.translate(-0.42, 0.25, -0.42);
            poseStack.translate(point.x * 0.1316, 0, point.y * 0.1316);
            poseStack.mulPose(Axis.YN.rotationDegrees(180 + camera.yRot));
            poseStack.scale(0.015625F, -0.015625F, 0.015625F);
            FormattedCharSequence arrow = FormattedCharSequence.forward("▼", Style.EMPTY);
            float width = (float) (-this.font.width(arrow) / 2) + 0.5f;
            submitNodeCollector.submitText(poseStack, width, -1.5f, arrow, false,
                    Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, 0xFFFF0000, 0, 0);
            poseStack.popPose();
        }
    }

    private void renderChessboard(GomokuRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        Direction facing = state.facing;
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.get2DDataValue() * 90));
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(180));
        }
        RenderType renderType = RenderTypes.entityCutout(CHECKER_BOARD_TEXTURE);
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            checkerBoardModel.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        });
        poseStack.popPose();
    }

    private void renderPiece(GomokuRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (!state.inPieceRenderDistance) {
            return;
        }
        byte[][] chessData = state.chessData;
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));

        // Render black pieces
        poseStack.pushPose();
        poseStack.translate(0.92, -0.1, -1.055);
        RenderType blackRenderType = RenderTypes.entityCutout(BLACK_PIECE_TEXTURE);
        submitNodeCollector.submitCustomGeometry(poseStack, blackRenderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            for (byte[] row : chessData) {
                for (int j = 0; j < chessData[0].length; j++) {
                    poseStack.translate(0, 0, 0.1316);
                    if (row[j] == Point.BLACK) {
                        pieceModel.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    }
                }
                poseStack.translate(-0.1316, 0, -1.974);
            }
            poseStack.popPose();
        });
        poseStack.popPose();

        // Render white pieces
        poseStack.pushPose();
        poseStack.translate(0.92, -0.1, -1.055);
        RenderType whiteRenderType = RenderTypes.entityCutout(WHITE_PIECE_TEXTURE);
        submitNodeCollector.submitCustomGeometry(poseStack, whiteRenderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            for (byte[] row : chessData) {
                for (int j = 0; j < chessData[0].length; j++) {
                    poseStack.translate(0, 0, 0.1316);
                    if (row[j] == Point.WHITE) {
                        pieceModel.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    }
                }
                poseStack.translate(-0.1316, 0, -1.974);
            }
            poseStack.popPose();
        });
        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderTipsText(GomokuRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Statue statue = state.statue;
        if (statue != Statue.IN_PROGRESS && state.inTipsRenderDistance) {
            MutableComponent loseTips;
            MutableComponent resetTips = Component.translatable("message.touhou_little_maid.gomoku.reset")
                    .withStyle(ChatFormatting.UNDERLINE)
                    .withStyle(ChatFormatting.AQUA);
            MutableComponent roundText = Component.translatable("message.touhou_little_maid.gomoku.round", state.chessCounter)
                    .withStyle(ChatFormatting.WHITE);
            MutableComponent preRoundIcon = Component.literal("⏹ ").withStyle(ChatFormatting.GREEN);
            MutableComponent postRoundIcon = Component.literal(" ⏹").withStyle(ChatFormatting.GREEN);
            MutableComponent roundTips = preRoundIcon.append(roundText).append(postRoundIcon);
            if (statue == Statue.WIN) {
                if (state.isPlayerTurn) {
                    loseTips = Component.translatable("message.touhou_little_maid.gomoku.win").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
                } else {
                    loseTips = Component.translatable("message.touhou_little_maid.gomoku.lose").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
                }
            } else {
                loseTips = Component.translatable("message.touhou_little_maid.gomoku.draw").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
            }
            FormattedCharSequence loseSeq = loseTips.getVisualOrderText();
            FormattedCharSequence resetSeq = resetTips.getVisualOrderText();
            FormattedCharSequence roundSeq = roundTips.getVisualOrderText();
            float loseTipsWidth = (float) (-this.font.width(loseSeq) / 2);
            float resetTipsWidth = (float) (-this.font.width(resetSeq) / 2);
            float roundTipsWidth = (float) (-this.font.width(roundSeq) / 2);
            poseStack.pushPose();
            poseStack.translate(0.5, 0.75, 0.5);
            poseStack.mulPose(Axis.YN.rotationDegrees(180 + camera.yRot));
            poseStack.mulPose(Axis.XN.rotationDegrees(camera.xRot));
            poseStack.scale(0.03F, -0.03F, 0.03F);
            submitNodeCollector.submitText(poseStack, loseTipsWidth, -10, loseSeq, true,
                    Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, 0xFFFFFFFF, 0, 0);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            submitNodeCollector.submitText(poseStack, roundTipsWidth, -30, roundSeq, true,
                    Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, 0xFFFFFFFF, 0, 0);
            submitNodeCollector.submitText(poseStack, resetTipsWidth, 0, resetSeq, true,
                    Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, 0xFFFFFFFF, 0, 0);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BlockEntityGomoku blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return RenderHelper.getAABB(pos.offset(-3, 0, -3), pos.offset(3, 1, 3));
    }
}
