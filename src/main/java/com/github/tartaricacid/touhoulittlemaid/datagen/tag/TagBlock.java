package com.github.tartaricacid.touhoulittlemaid.datagen.tag;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class TagBlock extends BlockTagsProvider {
    /**
     * 女仆有时候会在一些不该触发跳跃逻辑的方块上反复尝试跳来跳去，
     * 故添加此标签来将一些方块放入黑名单中
     */
    public static final TagKey<Block> MAID_JUMP_FORBIDDEN_BLOCK = createTagKey("maid_jump_forbidden_block");

    /**
     * 女仆避让方块标签，女仆在寻路、传送时会尽可能避让这些方块
     */
    public static final TagKey<Block> MAID_AVOID_BLOCK = createTagKey("maid_avoid_block");

    /**
     * 在修建祭坛时，可以当做祭坛鸟居部分的方块
     */
    public static final TagKey<Block> ALTAR_TORII = createTagKey("altar_torii");

    /**
     * 在修建祭坛时，可以当做祭坛柱子材料的方块；
     * <p>
     * 默认已经包含 <code>#minecraft:logs</code> 标签
     */
    public static final TagKey<Block> ALTAR_PILLAR = createTagKey("altar_pillar");

    /**
     * 女仆有偷吃方块食物的机制，但是这可能会误把一些拿来做装饰的食物方块也偷吃掉
     * <p>
     * 故我们现在为一些方块添加 tag，只有放在此方块上承载的食物方块女仆才会偷吃
     */
    public static final TagKey<Block> MAID_SNACK_STAND_BLOCK = createTagKey("maid_snack_stand_block");

    /**
     * 零食柜会在上方摆放特定方块时，渲染出玻璃橱窗的效果
     * <p>
     * 在此标签中的方块才会让下方零食柜渲染完整玻璃橱窗
     */
    public static final TagKey<Block> SNACK_CABINET_FULL = createTagKey("snack_cabinet_full");

    /**
     * 在此标签中的方块才会让下方零食柜渲染半高玻璃橱窗
     */
    public static final TagKey<Block> SNACK_CABINET_HALF = createTagKey("snack_cabinet_half");

    /**
     * 所有颜色的女仆床方块
     */
    public static final TagKey<Block> MAID_BED = createTagKey("maid_bed");

    /**
     * CarryOn 黑名单标签，被此标签包含的方块将无法被 CarryOn 抱起
     */
    public static final TagKey<Block> CARRYON_BLOCK_BLACKLIST = createTagKey(Identifier.parse("carryon:block_blacklist"));

    public TagBlock(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                    String modId) {
        super(output, lookupProvider, modId);
    }

    public static TagKey<Block> createTagKey(String name) {
        return TagKey.create(Registries.BLOCK, IdentifierUtil.modLoc(name));
    }

    public static TagKey<Block> createTagKey(Identifier resourceLocation) {
        return TagKey.create(Registries.BLOCK, resourceLocation);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MAID_BED)
                .add(element("touhou_little_maid:pink_maid_bed"))
                .add(element("touhou_little_maid:white_maid_bed"))
                .add(element("touhou_little_maid:black_maid_bed"))
                .add(element("touhou_little_maid:yellow_maid_bed"))
                .add(element("touhou_little_maid:blue_maid_bed"))
                .add(element("touhou_little_maid:green_maid_bed"))
                .add(element("touhou_little_maid:purple_maid_bed"));

        tag(MAID_JUMP_FORBIDDEN_BLOCK)
                .addTag(BlockTags.DOORS)
                .addTag(BlockTags.FENCES)
                .addTag(BlockTags.CLIMBABLE);

        tag(ALTAR_TORII)
                .add(element("minecraft:red_wool"))
                .add(element("minecraft:red_concrete"))
                .add(optionalElement("biomesoplenty:redwood_planks"));
        tag(ALTAR_PILLAR).addTag(BlockTags.LOGS);

        var blacklist = tag(CARRYON_BLOCK_BLACKLIST);
        BuiltInRegistries.BLOCK.keySet().stream().filter(id -> id.getNamespace().equals(TouhouLittleMaid.MOD_ID))
                .forEach(id -> blacklist.add(element(id.toString())));

        tag(MAID_SNACK_STAND_BLOCK)
                .add(element("touhou_little_maid:snack_cabinet"))
                .add(optionalTag("kaleidoscope_cookery:table"));

        tag(SNACK_CABINET_FULL)
                // 蛋糕全部是完整玻璃橱窗
                .add(element("minecraft:cake"))
                .add(optionalTag("forge:cakes"))
                .add(optionalTag("c:cakes"))
                .add(optionalTag("jmc:cakes"))
                // 农夫乐事的盛宴
                .add(optionalElement("farmersdelight:roast_chicken_block"))
                .add(optionalElement("farmersdelight:stuffed_pumpkin_block"))
                .add(optionalElement("farmersdelight:honey_glazed_ham_block"))
                .add(optionalElement("farmersdelight:shepherds_pie_block"))
                .add(optionalElement("farmersdelight:rice_roll_medley_block"));

        tag(SNACK_CABINET_HALF)
                // 农夫乐事的糕点
                .add(optionalElement("farmersdelight:apple_pie"))
                .add(optionalElement("farmersdelight:sweet_berry_cheesecake"))
                .add(optionalElement("farmersdelight:chocolate_pie"))
                // 森罗物语的方块菜，后续应该让森罗物语添加专门的 tag
                .add(optionalElement("kaleidoscope_cookery:dark_cuisine"))
                .add(optionalElement("kaleidoscope_cookery:suspicious_stir_fry"))
                .add(optionalElement("kaleidoscope_cookery:slime_ball_meal"))
                .add(optionalElement("kaleidoscope_cookery:fondant_pie"))
                .add(optionalElement("kaleidoscope_cookery:dongpo_pork"))
                .add(optionalElement("kaleidoscope_cookery:fondant_spider_eye"))
                .add(optionalElement("kaleidoscope_cookery:chorus_fried_egg"))
                .add(optionalElement("kaleidoscope_cookery:braised_fish"))
                .add(optionalElement("kaleidoscope_cookery:golden_salad"))
                .add(optionalElement("kaleidoscope_cookery:spicy_chicken"))
                .add(optionalElement("kaleidoscope_cookery:yakitori"))
                .add(optionalElement("kaleidoscope_cookery:pan_seared_knight_steak"))
                .add(optionalElement("kaleidoscope_cookery:stargazy_pie"))
                .add(optionalElement("kaleidoscope_cookery:sweet_and_sour_ender_pearls"))
                .add(optionalElement("kaleidoscope_cookery:crystal_lamb_chop"))
                .add(optionalElement("kaleidoscope_cookery:blaze_lamb_chop"))
                .add(optionalElement("kaleidoscope_cookery:frost_lamb_chop"))
                .add(optionalElement("kaleidoscope_cookery:nether_style_sashimi"))
                .add(optionalElement("kaleidoscope_cookery:end_style_sashimi"))
                .add(optionalElement("kaleidoscope_cookery:desert_style_sashimi"))
                .add(optionalElement("kaleidoscope_cookery:tundra_style_sashimi"))
                .add(optionalElement("kaleidoscope_cookery:cold_style_sashimi"))
                .add(optionalElement("kaleidoscope_cookery:shengjian_mantou"))
                .add(optionalElement("kaleidoscope_cookery:candied_potato"))
                .add(optionalElement("kaleidoscope_cookery:dough_drop_soup"))
                .add(optionalElement("kaleidoscope_cookery:stuffed_tiger_skin_pepper"))
                .add(optionalElement("kaleidoscope_cookery:spicy_rabbit_head"))
                .add(optionalElement("kaleidoscope_cookery:four_joy_meatball_soup"))
                .add(optionalElement("kaleidoscope_cookery:numbing_spicy_chicken"))
                .add(optionalElement("kaleidoscope_cookery:fried_caterpillar"))
                .add(optionalElement("kaleidoscope_cookery:fried_spring_roll"))
                .add(optionalElement("kaleidoscope_cookery:spicy_blood_stew"))
                .add(optionalElement("kaleidoscope_cookery:fruit_platter"))
                .add(optionalElement("kaleidoscope_cookery:braised_pork_ribs"))
                .add(optionalElement("kaleidoscope_cookery:cold_roasted_meat"))
                .add(optionalElement("kaleidoscope_cookery:oil_splashed_fish"))
                .add(optionalElement("kaleidoscope_cookery:brown_mushroom_pot_soup"))
                .add(optionalElement("kaleidoscope_cookery:red_mushroom_pot_soup"))
                .add(optionalElement("kaleidoscope_cookery:warped_fungus_pot_soup"))
                .add(optionalElement("kaleidoscope_cookery:crimson_fungus_pot_soup"))
                .add(optionalElement("kaleidoscope_cookery:buddha_jumps_over_the_wall"));

        tag(MAID_AVOID_BLOCK)
                // 怎么能在吃饭的桌子上跳来跳去呢
                .addTag(MAID_SNACK_STAND_BLOCK)
                // 机械动力
                .add(optionalElement("create:mechanical_saw"))
                .add(optionalElement("create:crushing_wheel"))
                .add(optionalElement("create:crushing_wheel_controller"))
                // 黄蜂领域
                .add(optionalElement("the_bumblezone:heavy_air"))
                .add(optionalElement("the_bumblezone:windy_air"))
                // 农夫乐事
                .add(optionalElement("farmersdelight:stove"))
                // 暮色森林
                .add(optionalElement("twilightforest:hedge"))
                .add(optionalElement("twilightforest:fiery_block"))
                .add(optionalElement("twilightforest:knightmetal_block"))
                // Alex 的洞穴
                .add(optionalElement("alexscaves:primal_magma"))
                .add(optionalElement("alexscaves:primal_magma"))
                // MEK 反应堆的聚变堆和超临界移相器
                .add(optionalElement("mekanismgenerators:fusion_reactor_frame"))
                .add(optionalElement("mekanism:sps_casing"))
                // 机械动力附属的铁丝网
                .add(optionalElement("createaddition:barbed_wire"))
                // 沉浸工程的铁丝网
                .add(optionalElement("immersiveengineering:razor_wire"))
                // 铁魔法的两个火堆
                .add(optionalElement("irons_spellbooks:brazier"))
                .add(optionalElement("irons_spellbooks:brazier_soul"))
                // 刷怪塔实用设备的锥刺和研磨机
                .add(optionalElement("mob_grinding_utils:spikes"))
                .add(optionalElement("mob_grinding_utils:saw"));
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
