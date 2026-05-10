package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.compat.ysm.event.UpdateRemoteStructEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SyncYsmMaidDataPackage(int entityId, String rouletteAnim, boolean isRouletteAnimPlaying,
                                     Object2FloatOpenHashMap<String> roamingVars) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncYsmMaidDataPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("sync_ysm_maid_data"));
    public static final StreamCodec<ByteBuf, SyncYsmMaidDataPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SyncYsmMaidDataPackage::entityId,
            ByteBufCodecs.STRING_UTF8,
            SyncYsmMaidDataPackage::rouletteAnim,
            ByteBufCodecs.BOOL,
            SyncYsmMaidDataPackage::isRouletteAnimPlaying,
            ByteBufUtils.OBJECT_2_FLOAT_OPEN_HASH_MAP_CODEC,
            SyncYsmMaidDataPackage::roamingVars,
            SyncYsmMaidDataPackage::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncYsmMaidDataPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> onHandle(message));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void onHandle(SyncYsmMaidDataPackage message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(message.entityId);
        if (!(entity instanceof EntityMaid maid)) {
            return;
        }
        NeoForge.EVENT_BUS.post(new UpdateRemoteStructEvent(maid, message.roamingVars));
        if (message.isRouletteAnimPlaying) {
            maid.playRouletteAnim(message.rouletteAnim);
        } else {
            maid.stopRouletteAnim();
        }
    }
}
