package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class CChessRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean inPieceRenderDistance;
    public boolean inTipsRenderDistance;
    public byte[] chessData = new byte[256];
    public int selectX;
    public int selectY;
    public boolean showTips;
    public boolean isCheckmate;
    public boolean isRepeat;
    public boolean isMoveNumberLimit;
    public boolean isPlayerTurn;
    public int chessCounter;
}
