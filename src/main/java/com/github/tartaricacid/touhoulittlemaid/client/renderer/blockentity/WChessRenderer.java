package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.api.game.chess.Position;
import com.github.tartaricacid.touhoulittlemaid.block.BlockGomoku;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityWChess;
import com.github.tartaricacid.touhoulittlemaid.client.model.WChessPiecesModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.WChessRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.github.tartaricacid.touhoulittlemaid.util.WChessUtil;
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
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class WChessRenderer implements BlockEntityRenderer<BlockEntityWChess, WChessRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/wchess.png");
    private static final Identifier PIECES_TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/wchess_pieces.png");

    private static final int TIPS_RENDER_DISTANCE_SQ = 16 * 16;
    private static final int PIECE_RENDER_DISTANCE_SQ = 24 * 24;

    private static final MutableComponent RESET_TIP_COMPONENT = Component
            .translatable("message.touhou_little_maid.wchess.reset")
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
    private final WChessPiecesModel[] chessPiecesModels;
    private final WChessPiecesModel selectedModels;

    public WChessRenderer(BlockEntityRendererProvider.Context context) {
        this.chessModel = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.WCHESS);
        this.chessPiecesModels = WChessPiecesModel.initModel();
        this.selectedModels = WChessPiecesModel.getSelectedModel();
        this.font = context.font();
    }

    @Override
    public WChessRenderState createRenderState() {
        return new WChessRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityWChess te, WChessRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
        state.facing = te.getBlockState().getValue(BlockGomoku.FACING);

        Vec3 pos = Vec3.atCenterOf(te.getBlockPos());
        state.inPieceRenderDistance = cameraPosition.distanceToSqr(pos) < PIECE_RENDER_DISTANCE_SQ;
        state.inTipsRenderDistance = cameraPosition.distanceToSqr(pos) < TIPS_RENDER_DISTANCE_SQ;

        int point = te.getSelectChessPoint();
        state.chessData = te.getChessData().squares;
        state.selectX = Position.FILE_X(point);
        state.selectY = Position.RANK_Y(point);

        state.showTips = te.isCheckmate() || te.isRepeat() || te.isMoveNumberLimit();
        state.isCheckmate = te.isCheckmate();
        state.isRepeat = te.isRepeat();
        state.isMoveNumberLimit = te.isMoveNumberLimit();
        state.isPlayerTurn = te.isPlayerTurn();
        state.chessCounter = te.getChessCounter();
    }

    @Override
    public void submit(WChessRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        this.renderChessboard(poseStack, submitNodeCollector, state);
        this.renderPiece(state, poseStack, submitNodeCollector);
        this.renderTipsText(state, poseStack, submitNodeCollector, camera);
    }

    private void renderTipsText(WChessRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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

        submitNodeCollector.submitText(
                poseStack, loseTipsWidth, -10, loseSeq, true,
                Font.DisplayMode.POLYGON_OFFSET, state.lightCoords,
                0xFFFFFFFF, 0, 0
        );

        poseStack.scale(0.5F, 0.5F, 0.5F);

        submitNodeCollector.submitText(
                poseStack, roundTipsWidth, -30, roundSeq, true,
                Font.DisplayMode.POLYGON_OFFSET, state.lightCoords,
                0xFFFFFFFF, 0, 0
        );

        submitNodeCollector.submitText(
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
    private FormattedCharSequence getLoseTipText(WChessRenderState state) {
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

    private void renderPiece(WChessRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
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
                poseStack.translate(0.875 + 0.5, 1.625, 0.875 + 0.5);
                break;
            case EAST:
                poseStack.translate(-0.875 + 0.5, 1.625, 0.875 + 0.5);
                break;
            case WEST:
                poseStack.translate(0.875 + 0.5, 1.625, -0.875 + 0.5);
                break;
            default:
                poseStack.translate(-0.875 + 0.5, 1.625, -0.875 + 0.5);
                break;
        }

        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.get2DDataValue() * 90));
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(180));
        }

        RenderType piecesRenderType = RenderTypes.entityCutout(PIECES_TEXTURE);
        submitNodeCollector.submitCustomGeometry(poseStack, piecesRenderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            this.submitPiece(state, poseStack, buffer, data, selectX, selectY);
            poseStack.popPose();
        });

        poseStack.popPose();
    }

    private void submitPiece(WChessRenderState state, PoseStack poseStack, VertexConsumer buffer, byte[] data, int selectX, int selectY) {
        for (int y = Position.RANK_TOP; y <= Position.RANK_BOTTOM; y++) {
            for (int x = Position.FILE_LEFT; x <= Position.FILE_RIGHT; x++) {
                byte index = data[Position.COORD_XY(x, y)];
                if (WChessUtil.isWhite(index) || WChessUtil.isBlack(index)) {
                    WChessPiecesModel model = this.chessPiecesModels[index];
                    model.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    if (selectX == x && selectY == y) {
                        this.selectedModels.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    }
                }
                poseStack.translate(0.25, 0, 0);
            }
            poseStack.translate(-0.25 * 8, 0, -0.25);
        }
    }

    private void renderChessboard(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, WChessRenderState state) {
        Direction facing = state.facing;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.get2DDataValue() * 90));
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(180));
        }

        submitNodeCollector.submitModel(
                this.chessModel, Unit.INSTANCE, poseStack, RenderTypes.entityCutout(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress
        );

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BlockEntityWChess blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return RenderHelper.getAABB(pos.offset(-3, 0, -3), pos.offset(3, 1, 3));
    }
}
