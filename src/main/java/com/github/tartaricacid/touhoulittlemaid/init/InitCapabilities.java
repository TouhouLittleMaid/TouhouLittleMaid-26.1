package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID)
public class InitCapabilities {
    public static final EntityCapability<ResourceHandler<ItemResource>, @Nullable Direction> HAND_ITEM = EntityCapability.createSided(getResourceLocation("hand_item"), ResourceHandler.asClass());
    public static final EntityCapability<ResourceHandler<ItemResource>, @Nullable Direction> ARMOR_ITEM = EntityCapability.createSided(getResourceLocation("armor_item"), ResourceHandler.asClass());

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerEntity(HAND_ITEM, InitEntities.MAID.get(), (maid, direction) -> maid.getHandsInvWrapper());
        event.registerEntity(ARMOR_ITEM, InitEntities.MAID.get(), (maid, direction) -> maid.getArmorInvWrapper());

        // 适配 forge 的实体 cap
        event.registerEntity(Capabilities.Item.ENTITY, InitEntities.MAID.get(), (maid, direction) -> maid.getAllInv());
    }
}
