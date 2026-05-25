package com.github.tartaricacid.touhoulittlemaid.entity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public record ProfileData(
        String modelId,
        String soundPackId
) {
    private static final String DEFAULT_MODEL_ID = "touhou_little_maid:hakurei_reimu";
    private static final String DEFAULT_SOUND_PACK_ID = "touhou_little_maid";
    private static final String PECO_SOUND_PACK_ID = "littlemaid_peco";
    private static final double PECO_CHANCE = 0.75;

    private static final MapCodec<ProfileData> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.STRING.fieldOf("model_id").forGetter(ProfileData::modelId),
            Codec.STRING.fieldOf("sound_pack_id").forGetter(ProfileData::soundPackId)
    ).apply(ins, ProfileData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ProfileData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ProfileData::modelId,
            ByteBufCodecs.STRING_UTF8, ProfileData::soundPackId,
            ProfileData::new
    );

    public static final AttachmentType<ProfileData> TYPE = AttachmentType
            .builder(() -> new ProfileData(DEFAULT_MODEL_ID, getInitSoundPackId()))
            .serialize(CODEC)
            .sync(STREAM_CODEC)
            .build();

    private static String getInitSoundPackId() {
        if (Math.random() < PECO_CHANCE) {
            return PECO_SOUND_PACK_ID;
        }
        return DEFAULT_SOUND_PACK_ID;
    }

    public ProfileData withModelId(String modelId) {
        return new ProfileData(modelId, this.soundPackId);
    }

    public ProfileData withSoundPackId(String soundPackId) {
        return new ProfileData(this.modelId, soundPackId);
    }
}
