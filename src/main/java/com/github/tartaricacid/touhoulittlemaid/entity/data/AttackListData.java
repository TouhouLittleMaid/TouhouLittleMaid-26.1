package com.github.tartaricacid.touhoulittlemaid.entity.data;

import com.github.tartaricacid.touhoulittlemaid.entity.misc.MonsterType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashMap;
import java.util.Map;

public record AttackListData(Map<Identifier, MonsterType> attackGroups) {
    private static final Codec<Map<Identifier, MonsterType>> ATTACK_GROUPS_CODEC = Codec
            .unboundedMap(Identifier.CODEC, MonsterType.CODEC)
            .xmap(HashMap::new, map -> map);

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<Identifier, MonsterType>> ATTACK_GROUPS_STREAM_CODEC =
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, MonsterType.STREAM_CODEC);

    private static final MapCodec<AttackListData> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            ATTACK_GROUPS_CODEC.fieldOf("attack_groups").forGetter(AttackListData::attackGroups)
    ).apply(ins, AttackListData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AttackListData> STREAM_CODEC = StreamCodec.composite(
            ATTACK_GROUPS_STREAM_CODEC, AttackListData::attackGroups,
            AttackListData::new
    );

    public static final AttachmentType<AttackListData> TYPE = AttachmentType
            .builder(AttackListData::empty)
            .serialize(CODEC)
            .sync(STREAM_CODEC)
            .build();

    public AttackListData {
        attackGroups = attackGroups == null ? new HashMap<>() : new HashMap<>(attackGroups);
    }

    public static AttackListData empty() {
        return new AttackListData(new HashMap<>());
    }
}
