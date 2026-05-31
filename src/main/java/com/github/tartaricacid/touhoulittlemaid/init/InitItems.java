package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.monster.EntityFairy;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface InitItems {
    DeferredRegister.Items ITEMS = DeferredRegister.createItems(TouhouLittleMaid.MOD_ID);

    // 生物蛋
    DeferredItem<Item> MAID_SPAWN_EGG = ITEMS.register("maid_spawn_egg", id -> new SpawnEggItem(
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).spawnEgg(EntityMaid.TYPE))
    );
    DeferredItem<Item> FAIRY_SPAWN_EGG = ITEMS.register("fairy_spawn_egg", id -> new SpawnEggItem(
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).spawnEgg(EntityFairy.TYPE))
    );

    // 女仆背包
    DeferredItem<Item> MAID_BACKPACK_SMALL = ITEMS.register("maid_backpack_small", ItemMaidBackpack::new);
    DeferredItem<Item> MAID_BACKPACK_MIDDLE = ITEMS.register("maid_backpack_middle", ItemMaidBackpack::new);
    DeferredItem<Item> MAID_BACKPACK_BIG = ITEMS.register("maid_backpack_big", ItemMaidBackpack::new);

    // 御币
    DeferredItem<Item> HAKUREI_GOHEI = ITEMS.register("hakurei_gohei", ItemGohei::new);
    DeferredItem<Item> SANAE_GOHEI = ITEMS.register("sanae_gohei", ItemGohei::new);

    // 有耐久的女仆饰品
    DeferredItem<Item> EXPLOSION_PROTECT_BAUBLE = ITEMS.register("explosion_protect_bauble", id -> new ItemDamageableBauble(id, 32));
    DeferredItem<Item> FIRE_PROTECT_BAUBLE = ITEMS.register("fire_protect_bauble", id -> new ItemDamageableBauble(id, 128));
    DeferredItem<Item> PROJECTILE_PROTECT_BAUBLE = ITEMS.register("projectile_protect_bauble", id -> new ItemDamageableBauble(id, 64));
    DeferredItem<Item> MAGIC_PROTECT_BAUBLE = ITEMS.register("magic_protect_bauble", id -> new ItemDamageableBauble(id, 128));
    DeferredItem<Item> FALL_PROTECT_BAUBLE = ITEMS.register("fall_protect_bauble", id -> new ItemDamageableBauble(id, 32));
    DeferredItem<Item> DROWN_PROTECT_BAUBLE = ITEMS.register("drown_protect_bauble", id -> new ItemDamageableBauble(id, 64));

    // 特殊饰品
    DeferredItem<Item> ULTRAMARINE_ORB_ELIXIR = ITEMS.register("ultramarine_orb_elixir", id -> new ItemDamageableBauble(id, 6));
    DeferredItem<Item> NIMBLE_FABRIC = ITEMS.register("nimble_fabric", id -> new ItemDamageableBauble(id, 64));

    // 无耐久的女仆饰品
    DeferredItem<Item> ITEM_MAGNET_BAUBLE = ITEMS.register("item_magnet_bauble", ItemNormalBauble::new);
    DeferredItem<Item> MUTE_BAUBLE = ITEMS.register("mute_bauble", ItemNormalBauble::new);

    // 女仆存储道具
    DeferredItem<Item> SMART_SLAB_INIT = ITEMS.register("smart_slab_init", id -> new ItemSmartSlab(id, ItemSmartSlab.Type.INIT));
    DeferredItem<Item> SMART_SLAB_EMPTY = ITEMS.register("smart_slab_empty", id -> new ItemSmartSlab(id, ItemSmartSlab.Type.EMPTY));
    DeferredItem<Item> SMART_SLAB_HAS_MAID = ITEMS.register("smart_slab_has_maid", id -> new ItemSmartSlab(id, ItemSmartSlab.Type.HAS_MAID));

    // 相机、照片与胶片
    DeferredItem<Item> CAMERA = ITEMS.register("camera", ItemCamera::new);
    DeferredItem<Item> PHOTO = ITEMS.register("photo", ItemPhoto::new);
    DeferredItem<Item> FILM = ITEMS.register("film", ItemFilm::new);

    // 寻回道具
    DeferredItem<Item> RED_FOX_SCROLL = ITEMS.register("red_fox_scroll", ItemFoxScroll::new);
    DeferredItem<Item> WHITE_FOX_SCROLL = ITEMS.register("white_fox_scroll", ItemFoxScroll::new);
    DeferredItem<Item> SERVANT_BELL = ITEMS.register("servant_bell", ItemServantBell::new);
    DeferredItem<Item> TRUMPET = ITEMS.register("trumpet", ItemTrumpet::new);

    // 家具
    DeferredItem<Item> CHAIR = ITEMS.register("chair", ItemChair::new);
    DeferredItem<Item> MAID_BED = ITEMS.register("maid_bed", ItemMaidBed::new);
    DeferredItem<Item> PICNIC_BASKET = ITEMS.register("picnic_basket", ItemPicnicBasket::new);
    DeferredItem<Item> SNACK_CABINET = ITEMS.register("snack_cabinet", ItemSnackCabinet::new);
    DeferredItem<Item> SCARECROW = ITEMS.register("scarecrow", ItemScarecrow::new);
    DeferredItem<Item> MAID_BEACON = ITEMS.register("maid_beacon", ItemMaidBeacon::new);

    // 杂项工具
    DeferredItem<Item> POWER_POINT = ITEMS.register("power_point", ItemPowerPoint::new);
    DeferredItem<Item> WIRELESS_IO = ITEMS.register("wireless_io", ItemWirelessIO::new);
    DeferredItem<Item> KAPPA_COMPASS = ITEMS.register("kappa_compass", ItemKappaCompass::new);
    DeferredItem<Item> EXTINGUISHER = ITEMS.register("extinguisher", ItemExtinguisher::new);
    DeferredItem<Item> ENTITY_ID_COPY = ITEMS.register("entity_id_copy", ItemEntityIdCopy::new);
    DeferredItem<Item> BROOM = ITEMS.register("broom", ItemBroom::new);

    // 模型与展示相关
    DeferredItem<Item> GARAGE_KIT = ITEMS.register("garage_kit", ItemGarageKit::new);
    DeferredItem<Item> MODEL_SWITCHER = ITEMS.register("model_switcher", ItemModelSwitcher::new);
    DeferredItem<Item> CHAIR_SHOW = ITEMS.register("chair_show", ItemChairShow::new);
    DeferredItem<Item> CHISEL = ITEMS.register("chisel", ItemChisel::new);

    // 调试与辅助工具
    DeferredItem<Item> FAVORABILITY_TOOL_ADD = ITEMS.register("favorability_tool_add", id -> new ItemFavorabilityTool(id, "add"));
    DeferredItem<Item> FAVORABILITY_TOOL_REDUCE = ITEMS.register("favorability_tool_reduce", id -> new ItemFavorabilityTool(id, "reduce"));
    DeferredItem<Item> FAVORABILITY_TOOL_FULL = ITEMS.register("favorability_tool_full", id -> new ItemFavorabilityTool(id, "full"));
    DeferredItem<Item> OWNER_CONVERSION_TOOL = ITEMS.register("owner_conversion_tool", ItemOwnerConversionTool::new);
    DeferredItem<Item> SUBSTITUTE_JIZO = ITEMS.register("substitute_jizo", ItemSubstituteJizo::new);

    // 成就图标
    DeferredItem<Item> CHANGE_CHAIR_MODEL = ITEMS.register("change_chair_model", ItemAdvancementIcon::new);
    DeferredItem<Item> CHANGE_MAID_MODEL = ITEMS.register("change_maid_model", ItemAdvancementIcon::new);
    DeferredItem<Item> MAID_100_HEALTHY = ITEMS.register("maid_100_healthy", ItemAdvancementIcon::new);
    DeferredItem<Item> KILL_100 = ITEMS.register("kill_100", ItemAdvancementIcon::new);
    DeferredItem<Item> KILL_SLIME_300 = ITEMS.register("kill_slime_300", ItemAdvancementIcon::new);
    DeferredItem<Item> ALL_NETHERITE_EQUIPMENT = ITEMS.register("all_netherite_equipment", ItemAdvancementIcon::new);
    DeferredItem<Item> KILL_WITHER = ITEMS.register("kill_wither", ItemAdvancementIcon::new);
    DeferredItem<Item> KILL_DRAGON = ITEMS.register("kill_dragon", ItemAdvancementIcon::new);
    DeferredItem<Item> TACZ_GUN_ICON = ITEMS.register("tacz_gun_icon", ItemAdvancementIcon::new);

    // 棋类
    DeferredItem<Item> GOMOKU = ITEMS.register("gomoku", id ->
            new BlockItem(InitBlocks.GOMOKU.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id))
                    .overrideDescription("block.touhou_little_maid.gomoku"))
    );

    DeferredItem<Item> CCHESS = ITEMS.register("cchess", id ->
            new BlockItem(InitBlocks.CCHESS.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id))
                    .overrideDescription("block.touhou_little_maid.cchess"))
    );

    DeferredItem<Item> WCHESS = ITEMS.register("wchess", id ->
            new BlockItem(InitBlocks.WCHESS.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id))
                    .overrideDescription("block.touhou_little_maid.wchess"))
    );

    // 娱乐方块
    DeferredItem<Item> KEYBOARD = ITEMS.register("keyboard", id ->
            new BlockItem(InitBlocks.KEYBOARD.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id))
                    .overrideDescription("block.touhou_little_maid.keyboard"))
    );

    DeferredItem<Item> BOOKSHELF = ITEMS.register("bookshelf", id ->
            new BlockItem(InitBlocks.BOOKSHELF.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id))
                    .overrideDescription("block.touhou_little_maid.bookshelf"))
    );

    DeferredItem<Item> COMPUTER = ITEMS.register("computer", id ->
            new BlockItem(InitBlocks.COMPUTER.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id))
                    .overrideDescription("block.touhou_little_maid.computer"))
    );

    // 神龛
    DeferredItem<Item> SHRINE = ITEMS.register("shrine", id ->
            new BlockItem(InitBlocks.SHRINE.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id))
                    .rarity(Rarity.RARE)
                    .overrideDescription("block.touhou_little_maid.shrine"))
    );
}
