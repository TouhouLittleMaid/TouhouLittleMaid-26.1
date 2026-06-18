package com.github.tartaricacid.touhoulittlemaid.datagen.tag;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
                .add(InitBlocks.PINK_MAID_BED.get())
                .add(InitBlocks.WHITE_MAID_BED.get())
                .add(InitBlocks.BLACK_MAID_BED.get())
                .add(InitBlocks.YELLOW_MAID_BED.get())
                .add(InitBlocks.BLUE_MAID_BED.get())
                .add(InitBlocks.GREEN_MAID_BED.get())
                .add(InitBlocks.PURPLE_MAID_BED.get());

        tag(MAID_JUMP_FORBIDDEN_BLOCK)
                .addTag(BlockTags.DOORS)
                .addTag(BlockTags.FENCES)
                .addTag(BlockTags.CLIMBABLE);

        tag(ALTAR_TORII)
                .add(Blocks.RED_WOOL, Blocks.RED_CONCRETE)
                .add(TagEntry.optionalElement(Identifier.parse("biomesoplenty:redwood_planks")));
        tag(ALTAR_PILLAR).addTag(BlockTags.LOGS);

        var blacklist = tag(CARRYON_BLOCK_BLACKLIST);
        BuiltInRegistries.BLOCK.keySet().stream().filter(id -> id.getNamespace().equals(TouhouLittleMaid.MOD_ID))
                .forEach(id -> blacklist.add(BuiltInRegistries.BLOCK.getValue(id)));

        tag(MAID_SNACK_STAND_BLOCK)
                .add(InitBlocks.SNACK_CABINET.get())
                .add(TagEntry.optionalTag(Identifier.parse("kaleidoscope_cookery:table")));

        tag(SNACK_CABINET_FULL)
                // 蛋糕全部是完整玻璃橱窗
                .add(Blocks.CAKE)
                .add(TagEntry.optionalTag(Identifier.parse("forge:cakes")))
                .add(TagEntry.optionalTag(Identifier.parse("c:cakes")))
                .add(TagEntry.optionalTag(Identifier.parse("jmc:cakes")))
                // 农夫乐事的盛宴
                .add(TagEntry.optionalElement(Identifier.parse("farmersdelight:roast_chicken_block")))
                .add(TagEntry.optionalElement(Identifier.parse("farmersdelight:stuffed_pumpkin_block")))
                .add(TagEntry.optionalElement(Identifier.parse("farmersdelight:honey_glazed_ham_block")))
                .add(TagEntry.optionalElement(Identifier.parse("farmersdelight:shepherds_pie_block")))
                .add(TagEntry.optionalElement(Identifier.parse("farmersdelight:rice_roll_medley_block")));

        tag(SNACK_CABINET_HALF)
                // 农夫乐事的糕点
                .add(TagEntry.optionalElement(Identifier.parse("farmersdelight:apple_pie")))
                .add(TagEntry.optionalElement(Identifier.parse("farmersdelight:sweet_berry_cheesecake")))
                .add(TagEntry.optionalElement(Identifier.parse("farmersdelight:chocolate_pie")))
                // 森罗物语的方块菜，后续应该让森罗物语添加专门的 tag
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:dark_cuisine")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:suspicious_stir_fry")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:slime_ball_meal")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:fondant_pie")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:dongpo_pork")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:fondant_spider_eye")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:chorus_fried_egg")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:braised_fish")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:golden_salad")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:spicy_chicken")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:yakitori")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:pan_seared_knight_steak")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:stargazy_pie")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:sweet_and_sour_ender_pearls")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:crystal_lamb_chop")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:blaze_lamb_chop")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:frost_lamb_chop")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:nether_style_sashimi")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:end_style_sashimi")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:desert_style_sashimi")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:tundra_style_sashimi")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:cold_style_sashimi")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:shengjian_mantou")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:candied_potato")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:dough_drop_soup")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:stuffed_tiger_skin_pepper")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:spicy_rabbit_head")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:four_joy_meatball_soup")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:numbing_spicy_chicken")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:fried_caterpillar")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:fried_spring_roll")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:spicy_blood_stew")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:fruit_platter")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:braised_pork_ribs")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:cold_roasted_meat")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:oil_splashed_fish")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:brown_mushroom_pot_soup")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:red_mushroom_pot_soup")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:warped_fungus_pot_soup")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:crimson_fungus_pot_soup")))
                .add(TagEntry.optionalElement(Identifier.parse("kaleidoscope_cookery:buddha_jumps_over_the_wall")));

        tag(MAID_AVOID_BLOCK)
                // 怎么能在吃饭的桌子上跳来跳去呢
                .addTag(MAID_SNACK_STAND_BLOCK)
                // 机械动力
                .add(TagEntry.optionalElement(Identifier.parse("create:mechanical_saw")))
                .add(TagEntry.optionalElement(Identifier.parse("create:crushing_wheel")))
                .add(TagEntry.optionalElement(Identifier.parse("create:crushing_wheel_controller")))
                // 黄蜂领域
                .add(TagEntry.optionalElement(Identifier.parse("the_bumblezone:heavy_air")))
                .add(TagEntry.optionalElement(Identifier.parse("the_bumblezone:windy_air")))
                // 农夫乐事
                .add(TagEntry.optionalElement(Identifier.parse("farmersdelight:stove")))
                // 暮色森林
                .add(TagEntry.optionalElement(Identifier.parse("twilightforest:hedge")))
                .add(TagEntry.optionalElement(Identifier.parse("twilightforest:fiery_block")))
                .add(TagEntry.optionalElement(Identifier.parse("twilightforest:knightmetal_block")))
                // Alex 的洞穴
                .add(TagEntry.optionalElement(Identifier.parse("alexscaves:primal_magma")))
                .add(TagEntry.optionalElement(Identifier.parse("alexscaves:primal_magma")))
                // MEK 反应堆的聚变堆和超临界移相器
                .add(TagEntry.optionalElement(Identifier.parse("mekanismgenerators:fusion_reactor_frame")))
                .add(TagEntry.optionalElement(Identifier.parse("mekanism:sps_casing")))
                // 机械动力附属的铁丝网
                .add(TagEntry.optionalElement(Identifier.parse("createaddition:barbed_wire")))
                // 沉浸工程的铁丝网
                .add(TagEntry.optionalElement(Identifier.parse("immersiveengineering:razor_wire")))
                // 铁魔法的两个火堆
                .add(TagEntry.optionalElement(Identifier.parse("irons_spellbooks:brazier")))
                .add(TagEntry.optionalElement(Identifier.parse("irons_spellbooks:brazier_soul")))
                // 刷怪塔实用设备的锥刺和研磨机
                .add(TagEntry.optionalElement(Identifier.parse("mob_grinding_utils:spikes")))
                .add(TagEntry.optionalElement(Identifier.parse("mob_grinding_utils:saw")));
    }
}
