package com.github.tartaricacid.touhoulittlemaid.compat.aquaculture;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.compat.aquaculture.client.AquacultureClientRegister;
import com.github.tartaricacid.touhoulittlemaid.compat.aquaculture.entity.AquacultureFishingHook;
import com.github.tartaricacid.touhoulittlemaid.compat.aquaculture.entity.AquacultureFishingType;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.fishing.FishingTypeManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.RegisterEvent;

public class AquacultureCompat {
    private static final String MOD_ID = "aquaculture";
    private static boolean INSTALLED;

    public static void init(IEventBus eventBus) {
        INSTALLED = FMLLoader.getCurrent().getLoadingModList().getModFileById(MOD_ID) != null;
        if (INSTALLED) {
            registerAll(eventBus);
        }
    }

    public static void registerFishingType(FishingTypeManager manager) {
        if (INSTALLED) {
            manager.addFishingType(new AquacultureFishingType());
        }
    }

    private static void registerAll(IEventBus eventBus) {
        eventBus.register(new AquacultureCompat());
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            eventBus.register(new AquacultureClientRegister());
        }
    }

    @SubscribeEvent
    public void register(RegisterEvent event) {
        Identifier id = IdentifierUtil.modLoc("aquaculture_fishing_hook");
        event.register(BuiltInRegistries.ENTITY_TYPE.key(), helper ->
                helper.register(id, AquacultureFishingHook.TYPE)
        );
    }
}
