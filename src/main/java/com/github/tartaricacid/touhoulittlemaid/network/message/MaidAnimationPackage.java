package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.client.MaidAnimationPackageProxy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

/**
 * 用于同步客户端播放动画的消息
 * 目前只包含拾取雪球的动画
 */
public record MaidAnimationPackage(int maidId, int animationId) implements CustomPacketPayload {
    public static final int NONE = 0;
    public static final int PICK_UP_SNOWBALL = 1;
    public static final int SWF_AIM = 2;
    public static final int SWF_RELOAD = 3;
    public static final int SWF_FIRE = 4;

    public static final CustomPacketPayload.Type<MaidAnimationPackage> TYPE = new CustomPacketPayload.Type<>(modLoc("maid_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MaidAnimationPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MaidAnimationPackage::maidId,
            ByteBufCodecs.VAR_INT, MaidAnimationPackage::animationId,
            MaidAnimationPackage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static MaidAnimationPackage pickUpSnowball(EntityMaid maid) {
        // 播放丢雪球动画之前，先禁止女仆移动
        // 标记服务端事件
        maid.getAnimationManager().animationId = PICK_UP_SNOWBALL;
        maid.getAnimationManager().animationRecordTime = System.currentTimeMillis();
        // 返回消息
        return new MaidAnimationPackage(maid.getId(), PICK_UP_SNOWBALL);
    }

    public static void handle(MaidAnimationPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> MaidAnimationPackageProxy.handle(message));
        }
    }
}
