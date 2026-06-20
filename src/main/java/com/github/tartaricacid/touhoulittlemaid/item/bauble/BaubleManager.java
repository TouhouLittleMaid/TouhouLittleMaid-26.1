package com.github.tartaricacid.touhoulittlemaid.item.bauble;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;
import java.util.Map;

public final class BaubleManager {
    private static Map<DeferredItem<Item>, IMaidBauble> BAUBLES = Maps.newHashMap();

    private BaubleManager() {
        BAUBLES = Maps.newHashMap();
    }

    public static void init() {
        BaubleManager manager = new BaubleManager();

        manager.bind(InitItems.DROWN_PROTECT_BAUBLE, new DrownProtectBauble());
        manager.bind(InitItems.EXPLOSION_PROTECT_BAUBLE, new ExplosionProtectBauble());
        manager.bind(InitItems.FALL_PROTECT_BAUBLE, new FallProtectBauble());
        manager.bind(InitItems.FIRE_PROTECT_BAUBLE, new FireProtectBauble());
        manager.bind(InitItems.MAGIC_PROTECT_BAUBLE, new MagicProtectBauble());
        manager.bind(InitItems.PROJECTILE_PROTECT_BAUBLE, new ProjectileProtectBauble());

        manager.bind(InitItems.MUTE_BAUBLE, new MuteBauble());
        manager.bind(InitItems.NIMBLE_FABRIC, new NimbleFabricBauble());
        manager.bind(InitItems.ITEM_MAGNET_BAUBLE, new ItemMagnetBauble());
        manager.bind(InitItems.WIRELESS_IO, new WirelessIOBauble());

        manager.bind(InitItems.ULTRAMARINE_ORB_ELIXIR, new ExtraLifeBauble());
        manager.bind(Items.TOTEM_OF_UNDYING, new UndyingTotemBauble());

        for (ILittleMaid littleMaid : TouhouLittleMaid.EXTENSIONS) {
            littleMaid.bindMaidBauble(manager);
        }

        BAUBLES = ImmutableMap.copyOf(BAUBLES);
    }

    @Nullable
    public static IMaidBauble getBauble(DeferredItem<Item> item) {
        return BAUBLES.get(item);
    }

    @Nullable
    public static IMaidBauble getBauble(ItemStack stack) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return getBauble(DeferredItem.createItem(id));
    }

    public void bind(DeferredItem<Item> item, IMaidBauble bauble) {
        BAUBLES.put(item, bauble);
    }

    public void bind(Item item, IMaidBauble bauble) {
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        BAUBLES.put(DeferredItem.createItem(id), bauble);
    }
}
