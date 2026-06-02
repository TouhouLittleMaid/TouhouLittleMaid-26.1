package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.network.client.ItemBreakPackageProxy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.modLoc;

public record ItemBreakPackage(int id, ItemStack item) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ItemBreakPackage> TYPE = new CustomPacketPayload.Type<>(modLoc("item_break"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemBreakPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ItemBreakPackage::id,
            ItemStack.STREAM_CODEC,
            ItemBreakPackage::item,
            ItemBreakPackage::new
    );

    public static void handle(ItemBreakPackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> ItemBreakPackageProxy.handle(message));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
