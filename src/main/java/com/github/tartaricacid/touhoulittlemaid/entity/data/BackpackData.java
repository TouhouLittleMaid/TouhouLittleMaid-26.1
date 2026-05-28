package com.github.tartaricacid.touhoulittlemaid.entity.data;

import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public record BackpackData(String type) {
    private static final MapCodec<BackpackData> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.STRING.fieldOf("type").forGetter(BackpackData::type)
    ).apply(ins, BackpackData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, BackpackData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BackpackData::type,
            BackpackData::new
    );

    public static final AttachmentType<BackpackData> TYPE = AttachmentType
            .builder(BackpackData::defaultBackpack)
            .serialize(CODEC)
            .sync(STREAM_CODEC)
            .build();

    private static BackpackData defaultBackpack() {
        return new BackpackData(BackpackManager.getEmptyBackpack().getId().toString());
    }
}
