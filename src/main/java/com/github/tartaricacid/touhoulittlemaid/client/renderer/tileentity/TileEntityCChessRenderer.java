package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.api.game.xqwlight.Position;
import com.github.tartaricacid.touhoulittlemaid.block.BlockGomoku;
import com.github.tartaricacid.touhoulittlemaid.client.model.CChessPiecesModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.CChessRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityCChess;
import com.github.tartaricacid.touhoulittlemaid.util.CChessUtil;
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
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TileEntityCChessRenderer implements BlockEntityRenderer<TileEntityCChess, CChessRenderState> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/cchess.png");
    private static final Identifier PIECES_TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/cchess_pieces.png");
    private static final int TIPS_RENDER_DISTANCE = 16;
    private static final int PIECE_RENDER_DISTANCE = 24;
    private final Font font;
    private final SimpleBedrockModel<Unit> chessModel;
    private final CChessPiecesModel[] chessPiecesModels;
    private final CChessPiecesModel selectedModels;

    public TileEntityCChessRenderer(BlockEntityRendererProvider.Context context) {
        chessModel = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.CCHESS);
        chessPiecesModels = CChessPiecesModel.initModel();
        selectedModels = CChessPiecesModel.getSelectedModel();
        font = context.font();
    }

    @Override
    public CChessRenderState createRenderState() {
        return new CChessRenderState();
    }

    @Override
    public void extractRenderState(TileEntityCChess te, CChessRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
        state.facing = te.getBlockState().getValue(BlockGomoku.FACING);

        BlockPos pos = te.getBlockPos();
        state.inPieceRenderDistance = cameraPosition.distanceToSqr(Vec3.atCenterOf(pos)) < PIECE_RENDER_DISTANCE * PIECE_RENDER_DISTANCE;
        state.inTipsRenderDistance = cameraPosition.distanceToSqr(Vec3.atCenterOf(pos)) < TIPS_RENDER_DISTANCE * TIPS_RENDER_DISTANCE;

        // Chess game state
        state.chessData = te.getChessData().squares.clone();
        state.selectX = Position.FILE_X(te.getSelectChessPoint());
        state.selectY = Position.RANK_Y(te.getSelectChessPoint());

        // Tips state
        state.showTips = te.isCheckmate() || te.isRepeat() || te.isMoveNumberLimit();
        state.isCheckmate = te.isCheckmate();
        state.isRepeat = te.isRepeat();
        state.isMoveNumberLimit = te.isMoveNumberLimit();
        state.isPlayerTurn = te.isPlayerTurn();
        state.chessCounter = te.getChessCounter();
    }

    @Override
    public void submit(CChessRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Direction facing = state.facing;
        this.renderChessboard(poseStack, submitNodeCollector, state.lightCoords, facing);
        this.renderPiece(state, poseStack, submitNodeCollector, facing);
        this.renderTipsText(state, poseStack, submitNodeCollector, camera);
    }

    private void renderTipsText(CChessRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.showTips || !state.inTipsRenderDistance) {
            return;
        }

        MutableComponent loseTips = null;
        MutableComponent resetTips = Component.translatable("message.touhou_little_maid.cchess.reset").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.AQUA);
        MutableComponent roundText = Component.translatable("message.touhou_little_maid.gomoku.round", state.chessCounter).withStyle(ChatFormatting.WHITE);
        MutableComponent preRoundIcon = Component.literal("⏹ ").withStyle(ChatFormatting.GREEN);
        MutableComponent postRoundIcon = Component.literal(" ⏹").withStyle(ChatFormatting.GREEN);
        MutableComponent roundTips = preRoundIcon.append(roundText).append(postRoundIcon);

        if (state.isCheckmate) {
            if (!state.isPlayerTurn) {
                loseTips = Component.translatable("message.touhou_little_maid.gomoku.win").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
            } else {
                loseTips = Component.translatable("message.touhou_little_maid.gomoku.lose").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
            }
        } else if (state.isMoveNumberLimit) {
            loseTips = Component.translatable("message.touhou_little_maid.cchess.move_limit").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
        } else if (state.isRepeat) {
            loseTips = Component.translatable("message.touhou_little_maid.cchess.repeat").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
        }
        if (loseTips == null) {
            return;
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
                Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, 0xFFFFFF, 0, 0);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        submitNodeCollector.submitText(poseStack, roundTipsWidth, -30, roundSeq, true,
                Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, 0xFFFFFF, 0, 0);
        submitNodeCollector.submitText(poseStack, resetTipsWidth, 0, resetSeq, true,
                Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, 0xFFFFFF, 0, 0);
        poseStack.popPose();
    }

    private void renderPiece(CChessRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Direction facing) {
        if (!state.inPieceRenderDistance) {
            return;
        }
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
        submitNodeCollector.submitCustomGeometry(poseStack, piecesRenderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            for (int y = Position.RANK_TOP; y <= Position.RANK_BOTTOM; y++) {
                for (int x = Position.FILE_LEFT; x <= Position.FILE_RIGHT; x++) {
                    byte piecesIndex = data[Position.COORD_XY(x, y)];
                    if (CChessUtil.isRed(piecesIndex) || CChessUtil.isBlack(piecesIndex)) {
                        CChessPiecesModel chessPiecesModel = chessPiecesModels[piecesIndex];
                        chessPiecesModel.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                        if (selectX == x && selectY == y) {
                            selectedModels.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                        }
                    }
                    poseStack.translate(0.304, 0, 0);
                }
                poseStack.translate(-0.304 * 9, 0, -0.304);
            }
            poseStack.popPose();
        });
        poseStack.popPose();
    }

    private void renderChessboard(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Direction facing) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.get2DDataValue() * 90));
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(180));
        }
        RenderType renderType = RenderTypes.entityCutout(TEXTURE);
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            poseStack.pushPose();
            poseStack.last().set(pose);
            chessModel.renderToBuffer(poseStack, buffer, lightCoords, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        });
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TileEntityCChess blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return RenderHelper.getAABB(pos.offset(-3, 0, -3), pos.offset(3, 1, 3));
    }
}
