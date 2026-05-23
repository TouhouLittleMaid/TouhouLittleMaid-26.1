package com.github.tartaricacid.touhoulittlemaid.entity.data;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class MaidTaskDataMaps {
    private static final String TAG_NAME = "MaidTaskDataMaps";
    private final Reference2ObjectMap<Identifier, Optional<?>> dataMaps = new Reference2ObjectOpenHashMap<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, MaidTaskDataMaps> STREAM_CODEC = StreamCodec.of(
            (buf, maps) -> {
                buf.writeInt(maps.dataMaps.size());
                for (Identifier key : maps.dataMaps.keySet()) {
                    buf.writeIdentifier(key);
                    Optional<?> vOpt = maps.dataMaps.get(key);
                    if (vOpt.isEmpty())
                        continue;
                    Optional<StreamCodec> codec = TaskDataRegister.getSyncCodec(key);
                    codec.ifPresentOrElse(value -> {
                        buf.writeBoolean(true);
                        value.encode(buf, vOpt.get());
                    }, () -> buf.writeBoolean(false));
                }
            },
            (buf) -> {
                MaidTaskDataMaps maps = new MaidTaskDataMaps();
                int size = buf.readInt();
                for (int i = 0; i < size; i++) {
                    Identifier key = buf.readIdentifier();
                    if (buf.readBoolean()) {
                        Optional<StreamCodec> codec = TaskDataRegister.getSyncCodec(key);
                        codec.ifPresent(value -> {
                            Object v = value.decode(buf);
                            maps.dataMaps.put(key, Optional.of(v));
                        });
                    }
                }
                return maps;
            }
    );
    public static final EntityDataSerializer<MaidTaskDataMaps> SERIALIZER_INSTANCE = new EntityDataSerializer<>() {

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, MaidTaskDataMaps> codec() {
            return MaidTaskDataMaps.STREAM_CODEC;
        }

        @Override
        public MaidTaskDataMaps copy(MaidTaskDataMaps maidTaskDataMaps) {
            MaidTaskDataMaps maps = new MaidTaskDataMaps();
            maps.dataMaps.putAll(maidTaskDataMaps.dataMaps);
            return maps;
        }
    };

    @Nullable
    @SuppressWarnings("all")
    public <T> T getData(TaskDataKey<T> dataKey) {
        Optional<T> optional = (Optional<T>) dataMaps.get(dataKey);
        if (optional != null && optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public <T> T getOrCreateData(TaskDataKey<T> dataKey, T defaultValue) {
        if (dataMaps.containsKey(dataKey.id())) {
            T data = this.getData(dataKey);
            if (data != null) {
                return data;
            }
        }
        dataMaps.put(dataKey.id(), Optional.of(defaultValue));
        return defaultValue;
    }

    public <T> void setData(Identifier dataKey, T value) {
        dataMaps.put(dataKey, Optional.of(value));
    }

    @SuppressWarnings("all")
    public void writeSaveData(ValueOutput entityTag) {
        ValueOutput toStoreAs = entityTag.child(TAG_NAME);
        dataMaps.forEach((key, value) -> {
            value.ifPresent(data -> {
                TaskDataRegister.getCodec(key).ifPresent(codec -> toStoreAs.store(key.toString(), codec, (Object) data));
            });
        });
    }

    public void readSaveData(ValueInput entityTag) {
        dataMaps.clear();
        ValueInput dataTags = entityTag.childOrEmpty(TAG_NAME);
        for (String key : dataTags.keySet()) {
            Identifier id = Identifier.parse(key);
            TaskDataRegister.getCodec(id).map(codec -> dataTags.read(key, codec)).ifPresent(t -> dataMaps.put(id, t));
        }
    }
}
