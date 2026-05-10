package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.entity.data.inner.AttackListData;
import com.github.tartaricacid.touhoulittlemaid.entity.misc.MonsterType;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTaskData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SetAttackListPackage(int entityId,
                                   Map<ResourceLocation, MonsterType> attackGroups) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetAttackListPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("set_attack_list"));
    public static final StreamCodec<ByteBuf, Map<ResourceLocation, MonsterType>> LIST_STREAM_CODEC = ByteBufCodecs.map(HashMap::new,
            ResourceLocation.STREAM_CODEC,
            ByteBufCodecs.fromCodec(MonsterType.CODEC),
            65536
    );

    public static final StreamCodec<ByteBuf, SetAttackListPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetAttackListPackage::entityId,
            LIST_STREAM_CODEC, SetAttackListPackage::attackGroups,
            SetAttackListPackage::new
    );

    public static void handle(SetAttackListPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                writeList(message, context.player());
            });
        }
    }

    private static void writeList(SetAttackListPackage message, Player sender) {
        Entity entity = sender.level.getEntity(message.entityId);
        if (entity instanceof EntityMaid maid && maid.isOwnedBy(sender)) {
            maid.setAndSyncData(InitTaskData.ATTACK_LIST, new AttackListData(message.attackGroups));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
