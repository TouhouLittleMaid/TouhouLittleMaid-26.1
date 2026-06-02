package com.github.tartaricacid.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.compat.aquaculture.AquacultureCompat;
import com.github.tartaricacid.touhoulittlemaid.config.CommonConfig;
import com.github.tartaricacid.touhoulittlemaid.config.ServerConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.info.CommonDefaultPack;
import com.github.tartaricacid.touhoulittlemaid.init.*;
import com.google.common.collect.Lists;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(TouhouLittleMaid.MOD_ID)
public final class TouhouLittleMaid {
    public static final String MOD_ID = "touhou_little_maid";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static List<ILittleMaid> EXTENSIONS = Lists.newArrayList();
    public static boolean DEBUG = !FMLEnvironment.isProduction();

    public TouhouLittleMaid(IEventBus modEventBus, ModContainer modContainer) {
        initRegister(modEventBus);
        registerConfiguration(modContainer);
        CommonDefaultPack.initCommonDefaultPack();
        AquacultureCompat.init(modEventBus);
    }

    private static void initRegister(IEventBus eventBus) {
        InitAttribute.ATTRIBUTES.register(eventBus);

        InitEntities.ENTITY_TYPES.register(eventBus);
        InitEntities.DATA_SERIALIZERS.register(eventBus);

        InitBrains.MEMORY_MODULE_TYPES.register(eventBus);
        InitBrains.SENSOR_TYPES.register(eventBus);
        InitBrains.ENVIRONMENT_ATTRIBUTES.register(eventBus);
        InitBrains.ACTIVITIES.register(eventBus);

        InitBlocks.BLOCKS.register(eventBus);
        InitBlocks.BLOCK_ENTITY_TYPES.register(eventBus);

        InitItems.ITEMS.register(eventBus);
        InitCreativeTabs.TABS.register(eventBus);
        InitContainer.CONTAINER_TYPE.register(eventBus);
        InitSounds.SOUNDS.register(eventBus);

        InitRecipes.RECIPE_SERIALIZERS.register(eventBus);
        InitRecipes.RECIPE_TYPES.register(eventBus);
        InitRecipes.RECIPE_BOOK_CATEGORIES.register(eventBus);

        InitCommand.ARGUMENT_TYPE.register(eventBus);
        InitPoi.POI_TYPES.register(eventBus);
        InitTrigger.TRIGGERS.register(eventBus);
        InitDataAttachment.ATTACHMENT_TYPES.register(eventBus);
        InitDataComponent.DATA_COMPONENTS.register(eventBus);

        InitLootModifier.LOOT_CONDITION_TYPES.register(eventBus);
        InitLootModifier.LOOT_FUNCTION_TYPES.register(eventBus);
    }

    private static void registerConfiguration(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.init());
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.init());
    }
}
