package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityModelSwitcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record SaveSwitcherDataPackage(BlockPos pos,
                                      List<BlockEntityModelSwitcher.ModeInfo> modeInfos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SaveSwitcherDataPackage> TYPE = new CustomPacketPayload.Type<>(modLoc("save_switcher_data"));

    public static final StreamCodec<ByteBuf, List<BlockEntityModelSwitcher.ModeInfo>> COLLECTION_STREAM_CODEC =
            ByteBufCodecs.collection(
                    ArrayList::new,
                    BlockEntityModelSwitcher.ModeInfo.MODE_INFO_STREAM_CODEC,
                    1024);

    public static final StreamCodec<ByteBuf, SaveSwitcherDataPackage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SaveSwitcherDataPackage::pos,
            COLLECTION_STREAM_CODEC,
            SaveSwitcherDataPackage::modeInfos,
            SaveSwitcherDataPackage::new
    );

    public static void handle(SaveSwitcherDataPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                Level world = sender.level();
                if (world.isLoaded(message.pos)) {
                    BlockEntity te = world.getBlockEntity(message.pos);
                    if (te instanceof BlockEntityModelSwitcher switcher) {
                        switcher.setInfoList(message.modeInfos);
                    }
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
