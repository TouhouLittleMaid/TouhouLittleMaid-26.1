package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.entity.data.GameData;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;

import java.util.Map;

/**
 * 女仆游戏相关内容，主要维护管理下棋相关的数据
 */
public class MaidGameManager {
    private static final String GOMOKU = "Gomoku";
    private static final byte NONE = 0, WIN = 1, LOSE = 2;

    private final EntityMaid maid;

    public MaidGameManager(EntityMaid maid) {
        this.maid = maid;
    }

    public Object2IntArrayMap<String> getWinCounts() {
        return maid.getData(InitDataAttachment.GAME).winCounts();
    }

    public void setWinCounts(Map<String, Integer> stringIntegerMap) {
        GameData gameData = maid.getData(InitDataAttachment.GAME).withWinCounts(stringIntegerMap);
        maid.setData(InitDataAttachment.GAME, gameData);
    }

    private byte getGameStatue() {
        return maid.getData(InitDataAttachment.GAME).gameStatue();
    }

    private void setGameStatue(byte gameStatue) {
        GameData gameData = maid.getData(InitDataAttachment.GAME).withGameStatue(gameStatue);
        maid.setData(InitDataAttachment.GAME, gameData);
    }

    public int getGomokuWinCount() {
        return getWinCounts().getOrDefault(GOMOKU, 0);
    }

    public void increaseGomokuWinCount() {
        var winCounts = getWinCounts();
        winCounts.put(GOMOKU, winCounts.getOrDefault(GOMOKU, 0) + 1);
        this.setWinCounts(winCounts);
    }

    public boolean isWin() {
        return this.getGameStatue() == WIN;
    }

    public boolean isLost() {
        return this.getGameStatue() == LOSE;
    }

    public void markStatue(boolean isWin) {
        this.setGameStatue(isWin ? WIN : LOSE);
        if (isWin) {
            maid.playSound(InitSounds.GAME_WIN.get(), 1, 1);
        } else {
            maid.playSound(InitSounds.GAME_LOST.get(), 1, 1);
        }
    }

    public void resetStatue() {
        this.setGameStatue(NONE);
    }

    void tick() {
        if (!(this.maid.getVehicle() instanceof EntitySit) && getGameStatue() != NONE) {
            resetStatue();
        }
    }

    public interface View {
        MaidGameManager getGameManager();
    }
}
