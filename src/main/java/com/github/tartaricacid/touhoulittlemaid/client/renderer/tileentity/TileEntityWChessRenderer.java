package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.game.chess.Position;
import com.github.tartaricacid.touhoulittlemaid.block.BlockGomoku;
import com.github.tartaricacid.touhoulittlemaid.client.model.WChessPiecesModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state.WChessRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityWChess;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.github.tartaricacid.touhoulittlemaid.util.WChessUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TileEntityWChessRenderer implements BlockEntityRenderer<TileEntityWChess, WChessRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/block/wchess.png");
    private static final Identifier PIECES_TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/block/wchess_pieces.png");
    private static final int TIPS_RENDER_DISTANCE = 16;
    private static final int PIECE_RENDER_DISTANCE = 24;
    private final Font font;
    private final @Nullable SimpleBedrockModel<EntityRenderState> chessModel;
    private final WChessPiecesModel[] chessPiecesModels;
    private final WChessPiecesModel selectedModels;

    public TileEntityWChessRenderer(BlockEntityRendererProvider.Context context) {
        chessModel = BedrockModelLoader.getModel(BedrockModelLoader.WCHESS);
        chessPiecesModels = WChessPiecesModel.initModel();
        selectedModels = WChessPiecesModel.getSelectedModel();
        font = context.font();
    }

    @Override
    public WChessRenderState createRenderState() {
        return new WChessRenderState();
    }

    @Override
    public void extractRenderState(TileEntityWChess te, WChessRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
        state.facing = te.getBlockState().getValue(BlockGomoku.FACING);

        BlockPos pos = te.getBlockPos();
        state.inPieceRenderDistance = cameraPosition.distanceToSqr(Vec3.atCenterOf(pos)) < PIECE_RENDER_DISTANCE * PIECE_RENDER_DISTANCE;
        state.inTipsRenderDistance = cameraPosition.distanceToSqr(Vec3.atCenterOf(pos)) < TIPS_RENDER_DISTANCE * TIPS_RENDER_DISTANCE;

        state.chessData = te.getChessData().squares.clone();
        state.selectX = Position.FILE_X(te.getSelectChessPoint());
        state.selectY = Position.RANK_Y(te.getSelectChessPoint());

        state.showTips = te.isCheckmate() || te.isRepeat() || te.isMoveNumberLimit();
        state.isCheckmate = te.isCheckmate();
        state.isRepeat = te.isRepeat();
        state.isMoveNumberLimit = te.isMoveNumberLimit();
        state.isPlayerTurn = te.isPlayerTurn();
        state.chessCounter = te.getChessCounter();
    }

    @Override
    public void submit(WChessRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Direction facing = state.facing;
        this.renderChessboard(poseStack, submitNodeCollector, state.lightCoords, facing);
        this.renderPiece(state, poseStack, submitNodeCollector, facing);
        this.renderTipsText(state, poseStack, submitNodeCollector, camera);
    }

    private void renderTipsText(WChessRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.showTips || !state.inTipsRenderDistance) {
            return;
        }

        MutableComponent loseTips = null;
        MutableComponent resetTips = Component.translatable("message.touhou_little_maid.wchess.reset")
                .withStyle(ChatFormatting.UNDERLINE)
                .withStyle(ChatFormatting.AQUA);
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

    private void renderPiece(WChessRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Direction facing) {
        if (!state.inPieceRenderDistance) {
            return;
        }
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
            for (int y = Position.RANK_TOP; y <= Position.RANK_BOTTOM; y++) {
                for (int x = Position.FILE_LEFT; x <= Position.FILE_RIGHT; x++) {
                    byte piecesIndex = data[Position.COORD_XY(x, y)];
                    if (WChessUtil.isWhite(piecesIndex) || WChessUtil.isBlack(piecesIndex)) {
                        WChessPiecesModel chessPiecesModel = chessPiecesModels[piecesIndex];
                        chessPiecesModel.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY,
                                1.0F, 1.0F, 1.0F, 1.0F);
                        if (selectX == x && selectY == y) {
                            selectedModels.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY,
                                    1.0F, 1.0F, 1.0F, 1.0F);
                        }
                    }
                    poseStack.translate(0.25, 0, 0);
                }
                poseStack.translate(-0.25 * 8, 0, -0.25);
            }
            poseStack.popPose();
        });
        poseStack.popPose();
    }

    private void renderChessboard(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Direction facing) {
        if (chessModel == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.get2DDataValue() * 90));
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(180));
        }
        RenderType renderType = RenderTypes.entityCutout(TEXTURE);
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) ->
                chessModel.renderToBuffer(poseStack, buffer, lightCoords, OverlayTexture.NO_OVERLAY));
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TileEntityWChess blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return RenderHelper.getAABB(pos.offset(-3, 0, -3), pos.offset(3, 1, 3));
    }
}
