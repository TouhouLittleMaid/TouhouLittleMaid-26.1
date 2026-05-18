package com.github.tartaricacid.touhoulittlemaid.entity.data;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.init.InitTaskData;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;

public class TaskDataRegister {
    private static final Map<Identifier, TaskDataRegKey<?>> CODEC_MAP = Maps.newHashMap();

    public record TaskDataRegKey<T>(Identifier id, Codec<T> codec,
                                    StreamCodec<RegistryFriendlyByteBuf, T> syncCodec) implements TaskDataKey<T> {
    }

    public static Optional<Codec> getCodec(Identifier key) {
        return Optional.ofNullable(CODEC_MAP.get(key)).map(TaskDataRegKey::codec);
    }

    public static Optional<StreamCodec> getSyncCodec(Identifier key) {
        return Optional.ofNullable(CODEC_MAP.get(key)).map(TaskDataRegKey::syncCodec);
    }

    public static void init() {
        TaskDataRegister register = new TaskDataRegister();
        // 注册本模组自己的数据
        InitTaskData.registerAll(register);
        // 注册第三方模组添加的数据
        for (ILittleMaid littleMaid : TouhouLittleMaid.EXTENSIONS) {
            littleMaid.registerTaskData(register);
        }
    }

    public <T> TaskDataRegKey<T> register(Identifier key, Codec<T> codec) {
        return register(key, codec, ByteBufCodecs.fromCodecWithRegistries(codec));
    }

    public <T> TaskDataRegKey<T> register(Identifier key, Codec<T> saveCodec, StreamCodec<RegistryFriendlyByteBuf, T> syncCodec) {
        TaskDataRegKey<T> regKey = new TaskDataRegKey<>(key, saveCodec, syncCodec);
        CODEC_MAP.put(key, regKey);
        return regKey;
    }
}
