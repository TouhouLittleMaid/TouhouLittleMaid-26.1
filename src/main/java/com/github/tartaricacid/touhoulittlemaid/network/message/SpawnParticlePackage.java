package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.SpawnParticlePackageProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ByIdMap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.IntFunction;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record SpawnParticlePackage(int entityId, Type particleType, int delayTicks) implements CustomPacketPayload {
    public SpawnParticlePackage(int entityId, Type particleType) {
        this(entityId, particleType, 0);
    }

    public static final CustomPacketPayload.Type<SpawnParticlePackage> TYPE = new CustomPacketPayload.Type<>(modLoc("spawn_particle"));
    public static final StreamCodec<ByteBuf, SpawnParticlePackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SpawnParticlePackage::entityId,
            Type.STREAM_CODEC,
            SpawnParticlePackage::particleType,
            ByteBufCodecs.VAR_INT,
            SpawnParticlePackage::delayTicks,
            SpawnParticlePackage::new
    );

    public static void handle(SpawnParticlePackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> SpawnParticlePackageProxy.handle(message));
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Type {
        /**
         * 粒子类型
         */
        EXPLOSION, BUBBLE, HEART, RANK_UP, HEAL;
        public static final IntFunction<Type> BY_ID =
                ByIdMap.continuous(
                        Type::ordinal,
                        Type.values(),
                        ByIdMap.OutOfBoundsStrategy.ZERO
                );
        public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(Type.BY_ID, Type::ordinal);
    }
}
