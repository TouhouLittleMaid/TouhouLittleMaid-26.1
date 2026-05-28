package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.api.block.IBoardGameEntityBlock;
import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Point;
import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Statue;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.nio.ByteBuffer;
import java.util.List;

public class TileEntityGomoku extends TileEntityJoy implements IBoardGameEntityBlock {
    private static final String CHESS_DATA = "ChessData";
    private static final String STATUE = "Statue";
    private static final String PLAYER_TURN = "PlayerTurn";
    private static final String CHESS_COUNTER = "ChessCounter";
    private static final String LATEST_CHESS_POINT = "LatestChessPoint";

    private byte[][] chessData = new byte[15][15];
    private int statue = Statue.IN_PROGRESS.ordinal();
    private boolean playerTurn = true;
    private int chessCounter = 0;
    private Point latestChessPoint = Point.NULL;

    public TileEntityGomoku(BlockPos pos, BlockState blockState) {
        super(InitBlocks.GOMOKU_TE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.store(CHESS_DATA, ChessData.CODEC, new ChessData(chessData));
        output.putInt(STATUE, this.statue);
        output.putBoolean(PLAYER_TURN, this.playerTurn);
        output.putInt(CHESS_COUNTER, this.chessCounter);
        Point.toTag(this.latestChessPoint, output.child(LATEST_CHESS_POINT));
        super.saveAdditional(output);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.chessData = new byte[15][15];
        input.read(CHESS_DATA, ChessData.CODEC).ifPresent(data -> this.chessData = data.grid);
        this.statue = input.getIntOr(STATUE, Statue.IN_PROGRESS.ordinal());
        this.playerTurn = input.getBooleanOr(PLAYER_TURN, true);
        this.chessCounter = input.getIntOr(CHESS_COUNTER, 0);
        input.child(LATEST_CHESS_POINT).ifPresent(pointInput ->
                this.latestChessPoint = Point.fromTag(pointInput));
    }

    public void reset() {
        this.chessData = new byte[15][15];
        this.statue = Statue.IN_PROGRESS.ordinal();
        this.playerTurn = true;
        this.chessCounter = 0;
        this.latestChessPoint = Point.NULL;
    }

    public byte[][] getChessData() {
        return chessData;
    }

    public void setChessData(List<byte[]> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            this.chessData[i] = arrayList.get(i);
        }
    }

    public void setChessData(int x, int y, int type) {
        this.chessData[x][y] = (byte) type;
        this.latestChessPoint = new Point(x, y, type);
        this.chessCounter += 1;
    }

    // 调试功能，铺满棋盘，只剩三个位置
    public void clickWithDebug() {
        byte[][] drawBoard = new byte[15][15];
        for (int x = 0; x < 15; x++) {
            boolean blackFirst = (x / 2) % 2 == 0;
            for (int y = 0; y < 15; y++) {
                // 14,(12-14) 留空
                if (x == 14 && 12 <= y) {
                    drawBoard[x][y] = Point.EMPTY;
                    continue;
                }
                // 奇偶交替填充，避免出现五连
                if (blackFirst) {
                    drawBoard[x][y] = (y % 2 == 0) ? (byte) Point.BLACK : (byte) Point.WHITE;
                } else {
                    drawBoard[x][y] = (y % 2 == 0) ? (byte) Point.WHITE : (byte) Point.BLACK;
                }
            }
        }
        this.chessData = drawBoard;
        this.latestChessPoint = new Point(14, 10, Point.WHITE);
        this.chessCounter = 15 * 15 - 3;
        this.statue = Statue.IN_PROGRESS.ordinal();
        this.playerTurn = true;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    public void setStatue(Statue statue) {
        this.statue = statue.ordinal();
    }

    public Statue getStatue() {
        return Statue.values()[Mth.clamp(statue, 0, Statue.values().length - 1)];
    }

    public int getChessCounter() {
        return chessCounter;
    }

    public Point getLatestChessPoint() {
        return latestChessPoint;
    }

    private record ChessData(byte[][] grid) {
        private static final int SIZE = 15;
        private static final int TOTAL_SIZE = SIZE * SIZE;

        private static final Codec<ChessData> CODEC = Codec.BYTE_BUFFER.xmap(
                buffer -> {
                    byte[] bytes = new byte[TOTAL_SIZE];
                    buffer.get(bytes);

                    byte[][] grid = new byte[SIZE][SIZE];
                    for (int i = 0; i < TOTAL_SIZE; i++) {
                        grid[i / SIZE][i % SIZE] = bytes[i];
                    }
                    return new ChessData(grid);
                },
                data -> {
                    byte[] flattened = new byte[TOTAL_SIZE];
                    for (int x = 0; x < SIZE; x++) {
                        System.arraycopy(data.grid[x], 0, flattened, x * 15, SIZE);
                    }
                    return ByteBuffer.wrap(flattened);
                }
        );
    }
}
