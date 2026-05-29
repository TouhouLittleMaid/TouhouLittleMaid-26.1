package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.util.ByteBufUtils;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SyncBaublePackage(boolean isFull, int entityId,
                                Int2ObjectSortedMap<ItemStack> baubles) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncBaublePackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("sync_bauble"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBaublePackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SyncBaublePackage::isFull,
            ByteBufCodecs.VAR_INT,
            SyncBaublePackage::entityId,
            ByteBufUtils.INT_2_ITEM_STACK_SORTED_MAP_CODEC,
            SyncBaublePackage::baubles,
            SyncBaublePackage::new
    );

    public static SyncBaublePackage fullSync(int entityId, Int2ObjectSortedMap<ItemStack> baubles) {
        return new SyncBaublePackage(true, entityId, baubles);
    }

    public static SyncBaublePackage partialSync(int entityId, int slot, ItemStack stack) {
        Int2ObjectSortedMap<ItemStack> baubles = new Int2ObjectRBTreeMap<>();
        baubles.put(slot, stack);
        return new SyncBaublePackage(false, entityId, baubles);
    }

    public static SyncBaublePackage partialDel(int entityId, int slot) {
        Int2ObjectSortedMap<ItemStack> baubles = new Int2ObjectRBTreeMap<>();
        baubles.put(slot, ItemStack.EMPTY);
        return new SyncBaublePackage(false, entityId, baubles);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncBaublePackage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleClient(message));
        }
    }

    private static void handleClient(SyncBaublePackage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(message.entityId);
        if (entity instanceof EntityMaid maid) {
            BaubleItemHandler maidBauble = maid.components.item.getMaidBauble();
            // 全量同步前需要清空
            if (message.isFull) {
                maidBauble.clearAll();
            }
            message.baubles.forEach((slot, itemStack) -> ItemsUtil.setStackInSlot(maidBauble, slot, itemStack));
        }
    }
}
