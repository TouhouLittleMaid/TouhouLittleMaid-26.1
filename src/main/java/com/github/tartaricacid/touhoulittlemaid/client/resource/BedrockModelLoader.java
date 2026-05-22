package com.github.tartaricacid.touhoulittlemaid.client.resource;

import com.github.tartaricacid.simplebedrockmodel.client.manager.BedrockEntityModelRegister;
import com.github.tartaricacid.simplebedrockmodel.client.manager.BedrockEntityModelRegisterEvent;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.BroomModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.EntityBoxModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.EntityFairyModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.NewEntityFairyModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

/**
 * 把所有硬编码的模型全部资源包化，方便资源包替换模型
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class BedrockModelLoader {
    // 内部数据
    private static final Map<Identifier, Function<InputStream, ? extends SimpleBedrockModel<? extends EntityRenderState>>> ALL_MODELS = Maps.newHashMap();

    // 注册数据
    public static final Identifier ALTAR = registerSimpleBlockModel("altar");
    public static final Identifier BOOKSHELF = registerSimpleBlockModel("bookshelf");
    public static final Identifier COMPUTER = registerSimpleBlockModel("computer");
    public static final Identifier KEYBOARD = registerSimpleBlockModel("keyboard");
    public static final Identifier PICNIC_MAT = registerSimpleBlockModel("picnic_mat");
    public static final Identifier PICNIC_BASKET = registerSimpleBlockModel("picnic_basket");
    public static final Identifier STATUE_BASE = registerSimpleBlockModel("statue_base");
    public static final Identifier SHRINE = registerSimpleBlockModel("shrine");
    public static final Identifier GOMOKU = registerSimpleBlockModel("gomoku");
    public static final Identifier GOMOKU_PIECE = registerSimpleBlockModel("gomoku_piece");
    public static final Identifier CCHESS = registerSimpleBlockModel("cchess");
    public static final Identifier CCHESS_PIECES = registerSimpleBlockModel("cchess_pieces");
    public static final Identifier WCHESS = registerSimpleBlockModel("wchess");
    public static final Identifier WCHESS_PIECES = registerSimpleBlockModel("wchess_pieces");
    public static final Identifier SNACK_CABINET = registerSimpleBlockModel("snack_cabinet");

    public static final Identifier CAKE_BOX = registerEntityModel("cake_box", EntityBoxModel::new);
    public static final Identifier MAID_FAIRY = registerEntityModel("maid_fairy", EntityFairyModel::new);
    public static final Identifier NEW_MAID_FAIRY = registerEntityModel("new_maid_fairy", NewEntityFairyModel::new);
    public static final Identifier BABY_MAID_FAIRY = registerEntityModel("baby_maid_fairy", NewEntityFairyModel::new);
    public static final Identifier BROOM = registerEntityModel("broom", BroomModel::new);

    public static final Identifier REIMU_YUKKURI = registerSimpleEntityModel("reimu_yukkuri");
    public static final Identifier MARISA_YUKKURI = registerSimpleEntityModel("marisa_yukkuri");
    public static final Identifier TOMBSTONE = registerSimpleEntityModel("tombstone");
    public static final Identifier MAID_BANNER = registerSimpleEntityModel("maid_banner");

    public static final Identifier BIG_BACKPACK = registerSimpleEntityModel("backpack/big_backpack");
    public static final Identifier CRAFTING_TABLE_BACKPACK = registerSimpleEntityModel("backpack/crafting_table_backpack");
    public static final Identifier END_CHEST_BACKPACK = registerSimpleEntityModel("backpack/end_chest_backpack");
    public static final Identifier FURNACE_BACKPACK = registerSimpleEntityModel("backpack/furnace_backpack");
    public static final Identifier MIDDLE_BACKPACK = registerSimpleEntityModel("backpack/middle_backpack");
    public static final Identifier SMALL_BACKPACK = registerSimpleEntityModel("backpack/small_backpack");
    public static final Identifier TANK_BACKPACK = registerSimpleEntityModel("backpack/tank_backpack");

    public static final Identifier PINK_MAID_BED = registerSimpleBlockModel("maid_bed/pink");
    public static final Identifier WHITE_MAID_BED = registerSimpleBlockModel("maid_bed/white");
    public static final Identifier BLACK_MAID_BED = registerSimpleBlockModel("maid_bed/black");
    public static final Identifier YELLOW_MAID_BED = registerSimpleBlockModel("maid_bed/yellow");
    public static final Identifier BLUE_MAID_BED = registerSimpleBlockModel("maid_bed/blue");
    public static final Identifier GREEN_MAID_BED = registerSimpleBlockModel("maid_bed/green");
    public static final Identifier PURPLE_MAID_BED = registerSimpleBlockModel("maid_bed/purple");

    public static Identifier registerSimpleBlockModel(String name) {
        Identifier location = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/block/" + name);
        return registerSimpleModel(location);
    }

    public static Identifier registerSimpleEntityModel(String name) {
        Identifier location = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/entity/" + name);
        return registerSimpleModel(location);
    }

    public static Identifier registerSimpleModel(Identifier location) {
        return registerModel(location, SimpleBedrockModel::new);
    }

    public static Identifier registerBlockModel(String name, Function<InputStream, ? extends SimpleBedrockModel<? extends EntityRenderState>> function) {
        Identifier location = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/block/" + name);
        return registerModel(location, function);
    }

    public static Identifier registerEntityModel(String name, Function<InputStream, ? extends SimpleBedrockModel<? extends EntityRenderState>> function) {
        Identifier location = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "bedrock/entity/" + name);
        return registerModel(location, function);
    }

    public static Identifier registerModel(Identifier location, Function<InputStream, ? extends SimpleBedrockModel<? extends EntityRenderState>> function) {
        ALL_MODELS.put(location, function);
        return location;
    }

    @SubscribeEvent
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void onRegisterBedrockModelRenderers(BedrockEntityModelRegisterEvent event) {
        ALL_MODELS.forEach(event::register);
        ALL_MODELS.clear();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends EntityRenderState> SimpleBedrockModel<T> getModel(Identifier location) {
        return (SimpleBedrockModel<T>) BedrockEntityModelRegister.INSTANCE.getModel(location);
    }
}
