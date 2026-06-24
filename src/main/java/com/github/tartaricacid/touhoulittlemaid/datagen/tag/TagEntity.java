package com.github.tartaricacid.touhoulittlemaid.datagen.tag;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class TagEntity extends EntityTypeTagsProvider {
    /**
     * 女仆妖精的攻击目标，默认仅攻击铁傀儡和玩家
     */
    public static TagKey<EntityType<?>> MAID_FAIRY_ATTACK_GOAL = createTagKey("maid_fairy_attack_goal");

    /**
     * 女仆在骑乘时，为了朝向一致，会强制同步女仆朝向和当前骑乘实体朝向；
     * <p>
     * 但是部分模组（如机械动力）这么做反而会导致女仆异常旋转，故添加此标签
     */
    public static TagKey<EntityType<?>> MAID_VEHICLE_ROTATE_BLOCKLIST = createTagKey("maid_vehicle_rotate_blocklist");

    public static TagKey<EntityType<?>> CARRYON_ENTITY_BLACKLIST = createTagKey(Identifier.parse("carryon:entity_blacklist"));

    /**
     * 冰与火的石化效果免疫标签
     */
    public static final TagKey<EntityType<?>> IMMUNE_TO_GORGON_STONE = createTagKey(
            Identifier.parse("iceandfire:immune_to_gorgon_stone")
    );


    public TagEntity(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, TouhouLittleMaid.MOD_ID);
    }

    private static TagKey<EntityType<?>> createTagKey(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, IdentifierUtil.modLoc(name));
    }

    private static TagKey<EntityType<?>> createTagKey(Identifier id) {
        return TagKey.create(Registries.ENTITY_TYPE, id);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        tag(EntityTypeTags.IMPACT_PROJECTILES).add(element("touhou_little_maid:danmaku"));
        tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(element("touhou_little_maid:fairy"));
        tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(element("touhou_little_maid:fairy"));
        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(element("touhou_little_maid:fairy"));

        tag(MAID_FAIRY_ATTACK_GOAL).add(element("minecraft:iron_golem"))
                .add(optionalElement("guardvillagers:guard"))
                .add(optionalElement("earthtojavamobs:furnace_golem"))
                .add(optionalElement("earthmobsmod:furnace_golem"))
                .add(optionalElement("mutantmonsters:mutant_snow_golem"))
                .add(optionalElement("alexscaves:gingerbread_man"))
                .add(optionalElement("alexsmobs:bunfungus"));

        tag(MAID_VEHICLE_ROTATE_BLOCKLIST)
                .add(optionalElement("create:carriage_contraption"))
                .add(optionalElement("create:seat"));

        tag(CARRYON_ENTITY_BLACKLIST)
                .add(element("touhou_little_maid:tombstone"))
                .add(element("touhou_little_maid:sit"))
                .add(element("touhou_little_maid:broom"));

        // 让女仆免疫冰与火的石化效果，避免石化带来的各种问题
        tag(IMMUNE_TO_GORGON_STONE).add(element("touhou_little_maid:maid"));
    }

    private TagEntry element(String id) {
        return TagEntry.element(Identifier.parse(id));
    }

    private TagEntry optionalElement(String id) {
        return TagEntry.optionalElement(Identifier.parse(id));
    }
}
