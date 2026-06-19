package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.inventory.container.other.WirelessIOContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record WirelessIOFilterSlotPackage(int slotIndex, ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WirelessIOFilterSlotPackage> TYPE = new CustomPacketPayload.Type<>(modLoc("wireless_io_filter_slot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WirelessIOFilterSlotPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            WirelessIOFilterSlotPackage::slotIndex,
            ItemStack.OPTIONAL_STREAM_CODEC,
            WirelessIOFilterSlotPackage::stack,
            WirelessIOFilterSlotPackage::new
    );

    public static void handle(WirelessIOFilterSlotPackage message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                if (sender.containerMenu instanceof WirelessIOContainer menu) {
                    menu.setFilterSlot(message.slotIndex, message.stack);
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
