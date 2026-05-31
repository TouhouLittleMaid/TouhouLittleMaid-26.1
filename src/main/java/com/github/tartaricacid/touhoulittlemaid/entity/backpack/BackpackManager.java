package com.github.tartaricacid.touhoulittlemaid.entity.backpack;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.MaidBackpackRenderData;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.tartaricacid.touhoulittlemaid.api.backpack.MaidBackpackRenderData.EMPTY;

public class BackpackManager {
    // 渲染数据，客户端才能用
    public static final Function<Identifier, MaidBackpackRenderData> RENDER_DATA_CACHE = Util.memoize(id ->
            BackpackManager.findBackpack(id).map(IMaidBackpack::getRenderData).orElse(EMPTY)
    );

    private static Map<Identifier, IMaidBackpack> BACKPACK_ID_MAP = Maps.newHashMap();
    private static Map<Item, IMaidBackpack> BACKPACK_ITEM_MAP = Maps.newHashMap();
    private static IMaidBackpack EMPTY_BACKPACK = new EmptyBackpack();

    private BackpackManager() {
        EMPTY_BACKPACK = new EmptyBackpack();
        BACKPACK_ID_MAP = Maps.newHashMap();
        BACKPACK_ITEM_MAP = Maps.newHashMap();
    }

    public static void init() {
        BackpackManager manager = new BackpackManager();
        manager.add(EMPTY_BACKPACK);
        manager.add(new SmallBackpack());
        manager.add(new MiddleBackpack());
        manager.add(new BigBackpack());
        for (ILittleMaid littleMaid : TouhouLittleMaid.EXTENSIONS) {
            littleMaid.addMaidBackpack(manager);
        }
        BACKPACK_ID_MAP = ImmutableMap.copyOf(BACKPACK_ID_MAP);
        // 将物品和背包绑定
        BACKPACK_ID_MAP.forEach((id, backpack) -> BACKPACK_ITEM_MAP.put(backpack.getItem(), backpack));
        BACKPACK_ITEM_MAP = ImmutableMap.copyOf(BACKPACK_ITEM_MAP);
    }

    public static IMaidBackpack getEmptyBackpack() {
        return EMPTY_BACKPACK;
    }

    public static Optional<IMaidBackpack> findBackpack(Identifier id) {
        return Optional.ofNullable(BACKPACK_ID_MAP.get(id));
    }

    public static Optional<IMaidBackpack> findBackpack(ItemStack stack) {
        return Optional.ofNullable(BACKPACK_ITEM_MAP.get(stack.getItem()));
    }

    public static void addBackpackCooldown(Player player) {
        for (Item backpack : BACKPACK_ITEM_MAP.keySet()) {
            player.getCooldowns().addCooldown(backpack.getDefaultInstance(), 20);
        }
    }

    public void add(IMaidBackpack backpack) {
        BACKPACK_ID_MAP.put(backpack.getId(), backpack);
    }
}
