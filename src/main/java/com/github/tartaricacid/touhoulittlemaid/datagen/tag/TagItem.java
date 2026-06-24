package com.github.tartaricacid.touhoulittlemaid.datagen.tag;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class TagItem extends ItemTagsProvider {
    /**
     * 能够附魔御币专属附魔的物品
     */
    public static final TagKey<Item> GOHEI_ENCHANTABLE = createTagKey("gohei_enchantable");
    /**
     * 女仆可种植的种子
     * <p>
     * 默认已经把所有带有 <code>#forge:villager_plantable_seeds</code> 和 <code>#forge:seeds</code> 标签的物品加入其中了
     */
    public static final TagKey<Item> MAID_PLANTABLE_SEEDS = createTagKey("maid_plantable_seeds");
    /**
     * 能够驯服女仆的物品
     * <p>
     * 默认已经把所有带有 <code>#forge:cakes</code> 和 <code>#c:cakes</code> 标签的物品加入其中了
     */
    public static final TagKey<Item> MAID_TAMED_ITEM = createTagKey("maid_tamed_item");

    /**
     * 能够吸引女仆的物品
     * <p>
     * 默认已经把所有带有 <code>#forge:cakes</code> 和 <code>#c:cakes</code> 标签的物品加入其中了
     */
    public static final TagKey<Item> MAID_TEMPTATION_ITEM = createTagKey("maid_temptation_item");

    /**
     * 物品拥有经验修补后，女仆在吸收经验或者 P 点时能够进行修复；
     * <p>
     * 但是部分物品不能这么做，可将其加入此 tag 下
     */
    public static final TagKey<Item> MAID_MENDING_BLOCKLIST_ITEM = createTagKey("maid_mending_blocklist_item");

    /**
     * 女仆和玩家类似，在穿戴拥有消失诅咒附魔的装备（或者饰品）后死亡，其对应的物品会直接消失；
     * <p>
     * 但是部分物品不能这么做，可将其加入此 tag 下
     */
    public static final TagKey<Item> MAID_VANISHING_BLOCKLIST_ITEM = createTagKey("maid_vanishing_blocklist_item");

    /**
     * 女仆进食黑名单，与配置文件协同作用，方便拓展兼容
     * <p>
     * 全局的，适用于工作餐、回血餐和家庭餐
     */
    public static final TagKey<Item> MAID_EAT_BLOCKLIST_ITEM = createTagKey("maid_eat_blocklist_item");

    /**
     * 所有颜色的女仆床物品
     */
    public static final TagKey<Item> MAID_BED = createTagKey("maid_bed");

    public TagItem(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                   String modId) {
        super(pOutput, pLookupProvider, modId);
    }

    public static TagKey<Item> createTagKey(String name) {
        return TagKey.create(Registries.ITEM, IdentifierUtil.modLoc(name));
    }

    public static TagKey<Item> createTagKey(Identifier resourceLocation) {
        return TagKey.create(Registries.ITEM, resourceLocation);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(GOHEI_ENCHANTABLE).add(element("touhou_little_maid:hakurei_gohei"));
        this.tag(GOHEI_ENCHANTABLE).add(element("touhou_little_maid:sanae_gohei"));

        this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(element("touhou_little_maid:hakurei_gohei"))
                .add(element("touhou_little_maid:sanae_gohei"))
                .add(element("touhou_little_maid:ultramarine_orb_elixir"))
                .add(element("touhou_little_maid:explosion_protect_bauble"))
                .add(element("touhou_little_maid:fire_protect_bauble"))
                .add(element("touhou_little_maid:projectile_protect_bauble"))
                .add(element("touhou_little_maid:magic_protect_bauble"))
                .add(element("touhou_little_maid:fall_protect_bauble"))
                .add(element("touhou_little_maid:drown_protect_bauble"))
                .add(element("touhou_little_maid:nimble_fabric"));

        this.tag(MAID_PLANTABLE_SEEDS)
                .addTag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .addTag(Tags.Items.SEEDS)
                .add(optionalTag("kaleidoscope_cookery:cookery_mod_seeds"));
        this.tag(MAID_PLANTABLE_SEEDS).add(element("minecraft:nether_wart"));

        this.addCakeItems(MAID_TAMED_ITEM);
        this.addCakeItems(MAID_TEMPTATION_ITEM);

        tag(MAID_MENDING_BLOCKLIST_ITEM).add(element("touhou_little_maid:ultramarine_orb_elixir"));
        tag(MAID_VANISHING_BLOCKLIST_ITEM).add(element("touhou_little_maid:ultramarine_orb_elixir"));

        // 森罗物语辣椒
        tag(MAID_EAT_BLOCKLIST_ITEM)
                .add(optionalElement("kaleidoscope_cookery:red_chili"))
                .add(optionalElement("kaleidoscope_cookery:green_chili"));

        this.tag(MAID_BED)
                .add(element("touhou_little_maid:pink_maid_bed"))
                .add(element("touhou_little_maid:white_maid_bed"))
                .add(element("touhou_little_maid:black_maid_bed"))
                .add(element("touhou_little_maid:yellow_maid_bed"))
                .add(element("touhou_little_maid:blue_maid_bed"))
                .add(element("touhou_little_maid:green_maid_bed"))
                .add(element("touhou_little_maid:purple_maid_bed"));
    }

    private void addCakeItems(TagKey<Item> tagKey) {
        this.tag(tagKey)
                .add(element("minecraft:cake"))
                .add(optionalTag("forge:cakes"))
                .add(optionalTag("c:cakes"))
                .add(optionalTag("jmc:cakes"))
                .add(optionalElement("kawaiidishes:cheese_cake"))
                .add(optionalElement("kawaiidishes:honey_cheese_cake"))
                .add(optionalElement("kawaiidishes:chocolate_cheese_cake"))
                .add(optionalElement("kawaiidishes:piece_of_cake"))
                .add(optionalElement("kawaiidishes:piece_of_cheesecake"))
                .add(optionalElement("kawaiidishes:piece_of_chocolate_cheesecake"))
                .add(optionalElement("kawaiidishes:piece_of_honey_cheesecake"));
    }

    private TagEntry element(String id) {
        return TagEntry.element(Identifier.parse(id));
    }

    private TagEntry optionalElement(String id) {
        return TagEntry.optionalElement(Identifier.parse(id));
    }

    private TagEntry optionalTag(String id) {
        return TagEntry.optionalTag(Identifier.parse(id));
    }
}
