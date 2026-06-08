package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.api.game.xqwlight.Position;
import com.github.tartaricacid.touhoulittlemaid.block.BlockCChess;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityCChess;
import com.github.tartaricacid.touhoulittlemaid.client.model.CChessPiecesModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.CChessRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.util.CChessUtil;
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
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CChessRenderer implements BlockEntityRenderer<BlockEntityCChess, CChessRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/cchess.png");
    private static final Identifier PIECES_TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/cchess_pieces.png");

    private static final int TIPS_RENDER_DISTANCE_SQ = 16 * 16;
    private static final int PIECE_RENDER_DISTANCE_SQ = 24 * 24;

    private static final MutableComponent RESET_TIP_COMPONENT = Component
            .translatable("message.touhou_little_maid.cchess.reset")
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

    private static final MutableComponent MOVE_LIMIT_TIP_COMPONENT = Component
            .translatable("message.touhou_little_maid.cchess.move_limit")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.DARK_PURPLE);

    private static final MutableComponent REPEAT_TIP_COMPONENT = Component
            .translatable("message.touhou_little_maid.cchess.repeat")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.DARK_PURPLE);

    private final Font font;
    private final SimpleBedrockModel<Unit> chessModel;
    private final CChessPiecesModel[] chessPiecesModels;
    private final CChessPiecesModel selectedModels;

    public CChessRenderer(BlockEntityRendererProvider.Context context) {
        this.chessModel = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.CCHESS);
        this.chessPiecesModels = CChessPiecesModel.initModel();
        this.selectedModels = CChessPiecesModel.getSelectedModel();
        this.font = context.font();
    }

    @Override
    public CChessRenderState createRenderState() {
        return new CChessRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityCChess te, CChessRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
        state.facing = te.getBlockState().getValue(BlockCChess.FACING);

        Vec3 pos = Vec3.atCenterOf(te.getBlockPos());
        state.inPieceRenderDistance = cameraPosition.distanceToSqr(pos) < PIECE_RENDER_DISTANCE_SQ;
        state.inTipsRenderDistance = cameraPosition.distanceToSqr(pos) < TIPS_RENDER_DISTANCE_SQ;

        // 棋局数据
        int point = te.getSelectChessPoint();
        state.chessData = te.getChessData().squares;
        state.selectX = Position.FILE_X(point);
        state.selectY = Position.RANK_Y(point);

        // 提示信息
        state.showTips = te.isCheckmate() || te.isRepeat() || te.isMoveNumberLimit();
        state.isCheckmate = te.isCheckmate();
        state.isRepeat = te.isRepeat();
        state.isMoveNumberLimit = te.isMoveNumberLimit();
        state.isPlayerTurn = te.isPlayerTurn();
        state.chessCounter = te.getChessCounter();
    }

    @Override
    public void submit(CChessRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        this.renderChessboard(poseStack, collector, state);
        this.renderPiece(state, poseStack, collector);
        this.renderTipsText(state, poseStack, collector, camera);
    }

    private void renderChessboard(PoseStack poseStack, SubmitNodeCollector collector, CChessRenderState state) {
        Direction facing = state.facing;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.get2DDataValue() * 90));
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(180));
        }

        collector.submitModel(
                this.chessModel, Unit.INSTANCE, poseStack, RenderTypes.entityCutout(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress
        );

        poseStack.popPose();
    }

    private void renderPiece(CChessRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (!state.inPieceRenderDistance) {
            return;
        }

        Direction facing = state.facing;
        int selectX = state.selectX;
        int selectY = state.selectY;
        byte[] data = state.chessData;

        poseStack.pushPose();

        switch (facing) {
            case NORTH:
                poseStack.translate(1.365 + 0.5, 1.625, 1.370 + 0.5);
                break;
            case EAST:
                poseStack.translate(-1.365 + 0.5, 1.625, 1.370 + 0.5);
                break;
            case WEST:
                poseStack.translate(1.365 + 0.5, 1.625, -1.370 + 0.5);
                break;
            default:
                poseStack.translate(-1.365 + 0.5, 1.625, -1.370 + 0.5);
                break;
        }

        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.get2DDataValue() * 90));
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(180));
        }

        RenderType piecesRenderType = RenderTypes.entityCutout(PIECES_TEXTURE);
        collector.submitCustomGeometry(poseStack, piecesRenderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            this.submitPiece(state, poseStack, buffer, data, selectX, selectY);
            poseStack.popPose();
        });

        poseStack.popPose();
    }

    private void submitPiece(CChessRenderState state, PoseStack poseStack, VertexConsumer buffer, byte[] data, int selectX, int selectY) {
        for (int y = Position.RANK_TOP; y <= Position.RANK_BOTTOM; y++) {
            for (int x = Position.FILE_LEFT; x <= Position.FILE_RIGHT; x++) {
                byte index = data[Position.COORD_XY(x, y)];
                if (CChessUtil.isRed(index) || CChessUtil.isBlack(index)) {
                    CChessPiecesModel model = chessPiecesModels[index];
                    model.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    if (selectX == x && selectY == y) {
                        selectedModels.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    }
                }
                poseStack.translate(0.304, 0, 0);
            }
            poseStack.translate(-0.304 * 9, 0, -0.304);
        }
    }

    private void renderTipsText(CChessRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.showTips || !state.inTipsRenderDistance) {
            return;
        }

        FormattedCharSequence loseSeq = this.getLoseTipText(state);
        FormattedCharSequence roundSeq = this.getRoundTipText(state.chessCounter);
        FormattedCharSequence resetSeq = RESET_TIP_COMPONENT.getVisualOrderText();
        if (loseSeq == null) {
            return;
        }

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

    @Nullable
    private FormattedCharSequence getLoseTipText(CChessRenderState state) {
        if (state.isCheckmate) {
            if (state.isPlayerTurn) {
                return LOSE_TIP_COMPONENT.getVisualOrderText();
            } else {
                return WIN_TIP_COMPONENT.getVisualOrderText();
            }
        }
        if (state.isMoveNumberLimit) {
            return MOVE_LIMIT_TIP_COMPONENT.getVisualOrderText();
        }
        if (state.isRepeat) {
            return REPEAT_TIP_COMPONENT.getVisualOrderText();
        }
        return null;
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BlockEntityCChess be) {
        return RenderHelper.getAABB(
                be.getBlockPos().offset(-3, 0, -3),
                be.getBlockPos().offset(3, 1, 3)
        );
    }
}
