package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.HashMap;
import java.util.Map;

import static com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid.GAME_STATUE;
import static com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid.WIN_COUNTS;

public class MaidGameRecordManager {
    private static final String WIN_COUNT_TAG = "MaidGameSkillData";
    private static final String GOMOKU = "Gomoku";
    private static final byte NONE = 0, WIN = 1, LOSE = 2;


    public static final StreamCodec<FriendlyByteBuf, Map<String, Integer>> WIN_COUNT_STREAM_CODEC = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT);
    public static final Codec<Map<String, Integer>> WIN_COUNT_CODEC = Codec.unboundedMap(Codec.STRING, Codec.INT);
    public static final EntityDataSerializer<Map<String, Integer>> WIN_COUNT_SERIALIZER = EntityDataSerializer.forValueType(WIN_COUNT_STREAM_CODEC);

    private final EntityMaid maid;

    public MaidGameRecordManager(EntityMaid maid) {
        this.maid = maid;
    }

    void defineSyncedData(SynchedEntityData.Builder builder) {
        builder.define(WIN_COUNTS, new HashMap<>());
        builder.define(GAME_STATUE, (byte) 0);
    }

    void addAdditionalSaveData(ValueOutput output) {
        output.store(WIN_COUNT_TAG, WIN_COUNT_CODEC, getWinCounts());
    }

    void readAdditionalSaveData(ValueInput input) {
        input.read(WIN_COUNT_TAG, WIN_COUNT_CODEC).ifPresent(this::setWinCounts);
    }


    void tick() {
        if (!(this.maid.getVehicle() instanceof EntitySit) && getGameStatue() != NONE) {
            resetStatue();
        }
    }

    public Map<String, Integer> getWinCounts() {
        return maid.getEntityData().get(WIN_COUNTS);
    }

    public void setWinCounts(Map<String, Integer> stringIntegerMap) {
        maid.getEntityData().set(WIN_COUNTS, stringIntegerMap, true);
    }

    private byte getGameStatue() {
        return maid.getEntityData().get(GAME_STATUE);
    }

    private void setGameStatue(byte gameStatue) {
        maid.getEntityData().set(GAME_STATUE, gameStatue);
    }

    public int getGomokuWinCount() {
        return getWinCounts().getOrDefault(GOMOKU, 0);
    }

    public void increaseGomokuWinCount() {
        Map<String, Integer> winCounts = getWinCounts();
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
}
