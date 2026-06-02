package com.github.tartaricacid.touhoulittlemaid.world.data;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityTombstone;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class MaidWorldData extends SavedData {
    private static final Codec<List<MaidInfo>> MAID_INFO_CODEC_LIST = MaidInfo.MAID_INFO_CODEC.listOf().xmap(Lists::newArrayList, Function.identity());
    private static final Codec<List<MaidInfo>> TOMBS_STONE_CODEC_LIST = MaidInfo.TOMBS_STONE_CODEC.listOf().xmap(Lists::newArrayList, Function.identity());

    public static final Codec<MaidWorldData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, MAID_INFO_CODEC_LIST).xmap(Maps::newHashMap, Function.identity())
                    .fieldOf("MaidInfos").forGetter(o -> (HashMap<UUID, List<MaidInfo>>) o.infos),
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, TOMBS_STONE_CODEC_LIST).xmap(Maps::newHashMap, Function.identity())
                    .fieldOf("MaidTombstones").forGetter(o -> (HashMap<UUID, List<MaidInfo>>) o.tombstones),
            Codec.BOOL.fieldOf("dirty").forGetter(SavedData::isDirty)
    ).apply(ins, (MaidWorldData::new)));

    private static final Identifier IDENTIFIER = IdentifierUtil.modLoc("world_data");
    private final Map<UUID, List<MaidInfo>> infos;
    private final Map<UUID, List<MaidInfo>> tombstones;

    private MaidWorldData(Map<UUID, List<MaidInfo>> infos, Map<UUID, List<MaidInfo>> tombstones) {
        this.infos = infos;
        this.tombstones = tombstones;
    }

    private MaidWorldData(Map<UUID, List<MaidInfo>> infos, Map<UUID, List<MaidInfo>> tombstones, boolean dirty) {
        this.infos = infos;
        this.tombstones = tombstones;
        this.setDirty(dirty);
    }

    private MaidWorldData() {
        this.infos = Maps.newHashMap();
        this.tombstones = Maps.newHashMap();
    }

    public static SavedDataType<@NotNull MaidWorldData> factory() {
        // 可能是 ENTITY_CHUNK 吧
        return new SavedDataType<@NotNull MaidWorldData>(IDENTIFIER, MaidWorldData::new, CODEC, DataFixTypes.ENTITY_CHUNK);
    }

    @Nullable
    public static MaidWorldData get(Level level) {
        if (level instanceof ServerLevel) {
            ServerLevel overWorld = level.getServer().getLevel(Level.OVERWORLD);
            if (overWorld == null) {
                return null;
            }
            SavedDataStorage storage = overWorld.getDataStorage();
            MaidWorldData data = storage.computeIfAbsent(MaidWorldData.factory());
            data.setDirty();
            return data;
        }
        return null;
    }

    public void addInfo(MaidInfo info) {
        UUID ownerId = info.ownerId();
        List<MaidInfo> maidInfos = this.infos.computeIfAbsent(ownerId, uuid -> Lists.newArrayList());
        maidInfos.add(info);
        this.setDirty();
    }

    //TODO : 这里先简单判null
    public void addInfo(EntityMaid maid) {
        String dimension = maid.level.dimension().identifier().toString();
        BlockPos chunkPos = maid.blockPosition();
        LivingEntity owner = maid.getOwner();
        if (owner == null) {
            return;
        }
        UUID ownerId = owner.getUUID();
        UUID maidId = maid.getUUID();
        long timestamp = System.currentTimeMillis();
        Component name = maid.getDisplayName();
        this.addInfo(new MaidInfo(dimension, chunkPos, ownerId, maidId, timestamp, name));
    }

    public void removeInfo(EntityMaid maid) {
        LivingEntity owner = maid.getOwner();
        if (owner == null) {
            return;
        }
        UUID ownerId = owner.getUUID();
        if (this.infos.containsKey(ownerId)) {
            UUID maidId = maid.getUUID();
            this.infos.get(ownerId).removeIf(info -> info.entityId().equals(maidId));
            this.setDirty();
        }
    }

    @Nullable
    public List<MaidInfo> getInfos(UUID owner) {
        return infos.get(owner);
    }

    @Nullable
    public List<MaidInfo> getPlayerMaidInfos(Player player) {
        return this.infos.get(player.getUUID());
    }

    public void addTombstones(MaidInfo info) {
        UUID ownerId = info.ownerId();
        List<MaidInfo> tombstoneInfos = this.tombstones.computeIfAbsent(ownerId, uuid -> Lists.newArrayList());
        tombstoneInfos.add(info);
        this.setDirty();
    }

    public void addTombstones(EntityMaid maid, EntityTombstone tombstone) {
        String dimension = maid.level.dimension().identifier().toString();
        BlockPos chunkPos = tombstone.blockPosition();
        LivingEntity owner = maid.getOwner();
        if (owner == null) {
            return;
        }
        UUID ownerId = owner.getUUID();
        UUID tombstoneId = tombstone.getUUID();
        long timestamp = System.currentTimeMillis();
        Component name = maid.getDisplayName();
        this.addTombstones(new MaidInfo(dimension, chunkPos, ownerId, tombstoneId, timestamp, name));
    }

    public void removeTombstones(EntityTombstone tombstone) {
        UUID ownerId = tombstone.getOwnerId();
        if (this.tombstones.containsKey(ownerId)) {
            UUID tombstoneId = tombstone.getUUID();
            this.tombstones.get(ownerId).removeIf(info -> info.entityId().equals(tombstoneId));
            this.setDirty();
        }
    }

    @Nullable
    public List<MaidInfo> getTombstones(UUID owner) {
        return tombstones.get(owner);
    }

    @Nullable
    public List<MaidInfo> getPlayerMaidTombstones(Player player) {
        return this.tombstones.get(player.getUUID());
    }
}