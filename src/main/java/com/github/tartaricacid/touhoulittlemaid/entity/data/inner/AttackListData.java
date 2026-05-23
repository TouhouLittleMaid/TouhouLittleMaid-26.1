package com.github.tartaricacid.touhoulittlemaid.entity.data.inner;

import com.github.tartaricacid.touhoulittlemaid.entity.misc.MonsterType;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record AttackListData(Map<Identifier, MonsterType> attackGroups) {
    private static final Codec<Map<Identifier, MonsterType>> LIST_CODEC = Codec.unboundedMap(Codec.STRING, MonsterType.CODEC)
            .xmap(t -> {
                HashMap<Identifier, MonsterType> map = new HashMap<>();
                t.forEach((key, value) -> map.put(Identifier.parse(key), value));
                return map;
            }, t -> {
                HashMap<String, MonsterType> map = new HashMap<>();
                t.forEach((key, value) -> map.put(key.toString(), value));
                return map;
            });
    public static final Codec<AttackListData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LIST_CODEC.fieldOf("attack_groups").forGetter(AttackListData::attackGroups)
    ).apply(instance, AttackListData::new));

    public static AttackListData empty() {
        return new AttackListData(Maps.newHashMap());
    }
}
