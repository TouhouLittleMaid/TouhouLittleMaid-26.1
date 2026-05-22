package com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.BroomModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.EntityBoxModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.EntityFairyModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.NewEntityFairyModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockEntityModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

/**
 * 把所有硬编码的模型全部资源包化，方便资源包替换模型
 */
public class BedrockModelLoader {
    // 普通模型，即继承了 Model 的类，主要用于方块实体渲染
    public static final Map<Identifier, Function<InputStream, ? extends SimpleBedrockModel<?>>> MODELS = Maps.newHashMap();
    // 实体模型，即继承了 EntityModel 的类，主要用于实体渲染
    public static final Map<Identifier, Function<InputStream, ? extends SimpleBedrockEntityModel<? extends EntityRenderState>>> ENTITY_MODELS = Maps.newHashMap();

    // 注册数据
    public static final Identifier ALTAR = addModel("altar");
    public static final Identifier BOOKSHELF = addModel("bookshelf");
    public static final Identifier COMPUTER = addModel("computer");
    public static final Identifier KEYBOARD = addModel("keyboard");
    public static final Identifier PICNIC_MAT = addModel("picnic_mat");
    public static final Identifier PICNIC_BASKET = addModel("picnic_basket");
    public static final Identifier STATUE_BASE = addModel("statue_base");
    public static final Identifier SHRINE = addModel("shrine");
    public static final Identifier GOMOKU = addModel("gomoku");
    public static final Identifier GOMOKU_PIECE = addModel("gomoku_piece");
    public static final Identifier CCHESS = addModel("cchess");
    public static final Identifier CCHESS_PIECES = addModel("cchess_pieces");
    public static final Identifier WCHESS = addModel("wchess");
    public static final Identifier WCHESS_PIECES = addModel("wchess_pieces");
    public static final Identifier SNACK_CABINET = addModel("snack_cabinet");

    public static final Identifier CAKE_BOX = addEntityModel("cake_box", EntityBoxModel::new);
    public static final Identifier MAID_FAIRY = addEntityModel("maid_fairy", EntityFairyModel::new);
    public static final Identifier NEW_MAID_FAIRY = addEntityModel("new_maid_fairy", NewEntityFairyModel::new);
    public static final Identifier BABY_MAID_FAIRY = addEntityModel("baby_maid_fairy", NewEntityFairyModel::new);
    public static final Identifier BROOM = addEntityModel("broom", BroomModel::new);

    public static final Identifier REIMU_YUKKURI = addEntityModel("reimu_yukkuri");
    public static final Identifier MARISA_YUKKURI = addEntityModel("marisa_yukkuri");
    public static final Identifier TOMBSTONE = addEntityModel("tombstone");
    public static final Identifier MAID_BANNER = addEntityModel("maid_banner");

    public static final Identifier BIG_BACKPACK = addEntityModel("backpack/big_backpack");
    public static final Identifier CRAFTING_TABLE_BACKPACK = addEntityModel("backpack/crafting_table_backpack");
    public static final Identifier END_CHEST_BACKPACK = addEntityModel("backpack/end_chest_backpack");
    public static final Identifier FURNACE_BACKPACK = addEntityModel("backpack/furnace_backpack");
    public static final Identifier MIDDLE_BACKPACK = addEntityModel("backpack/middle_backpack");
    public static final Identifier SMALL_BACKPACK = addEntityModel("backpack/small_backpack");
    public static final Identifier TANK_BACKPACK = addEntityModel("backpack/tank_backpack");

    public static final Identifier PINK_MAID_BED = addModel("maid_bed/pink");
    public static final Identifier WHITE_MAID_BED = addModel("maid_bed/white");
    public static final Identifier BLACK_MAID_BED = addModel("maid_bed/black");
    public static final Identifier YELLOW_MAID_BED = addModel("maid_bed/yellow");
    public static final Identifier BLUE_MAID_BED = addModel("maid_bed/blue");
    public static final Identifier GREEN_MAID_BED = addModel("maid_bed/green");
    public static final Identifier PURPLE_MAID_BED = addModel("maid_bed/purple");

    public static Identifier addModel(String name) {
        Identifier location = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/block/" + name);
        MODELS.put(location, SimpleBedrockModel::new);
        return location;
    }

    public static Identifier addEntityModel(String name) {
        Identifier location = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/entity/" + name);
        ENTITY_MODELS.put(location, SimpleBedrockEntityModel::new);
        return location;
    }

    public static Identifier addModel(String name, Function<InputStream, ? extends SimpleBedrockModel<?>> function) {
        Identifier location = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/block/" + name);
        MODELS.put(location, function);
        return location;
    }

    public static Identifier addEntityModel(String name, Function<InputStream, ? extends SimpleBedrockEntityModel<? extends EntityRenderState>> function) {
        Identifier location = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/entity/" + name);
        ENTITY_MODELS.put(location, function);
        return location;
    }

    @SuppressWarnings("unchecked")
    public static <T> SimpleBedrockModel<T> getModel(Identifier location) {
        return (SimpleBedrockModel<T>) BedrockEntityModelRegister.INSTANCE.getModel(location);
    }

    @SuppressWarnings("unchecked")
    public static <T extends EntityRenderState> SimpleBedrockEntityModel<T> getEntityModel(Identifier location) {
        return (SimpleBedrockEntityModel<T>) BedrockEntityModelRegister.INSTANCE.getEntityModel(location);
    }
}
