package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state;

import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Point;
import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Statue;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class GomokuRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean inPieceRenderDistance;
    public boolean inTipsRenderDistance;
    public byte[][] chessData = new byte[0][];
    public Point latestChessPoint = Point.NULL;
    public Statue statue = Statue.IN_PROGRESS;
    public boolean isPlayerTurn;
    public int chessCounter;
}
