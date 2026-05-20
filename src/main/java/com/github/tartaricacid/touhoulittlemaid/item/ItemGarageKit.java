package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.compat.ysm.YsmCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.inventory.tooltip.YsmMaidInfo;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Objects;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.ENTITY_ID_TAG_NAME;
import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.MODEL_ID_TAG_NAME;

public class ItemGarageKit extends BlockItem {
    private static final String DEFAULT_ENTITY_ID = "touhou_little_maid:maid";
    private static final String DEFAULT_MODEL_ID = "touhou_little_maid:hakurei_reimu";
    private static final CustomData DEFAULT_DATA = getDefaultData();

    public ItemGarageKit(Identifier id) {
        super(InitBlocks.GARAGE_KIT.get(), (new Item.Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    public static CustomData getMaidData(ItemStack stack) {
        return Objects.requireNonNullElse(stack.get(InitDataComponent.MAID_INFO), DEFAULT_DATA);
    }

    private static CustomData getDefaultData() {
        CompoundTag data = new CompoundTag();
        data.putString(ENTITY_ID_TAG_NAME, DEFAULT_ENTITY_ID);
        data.putString(MODEL_ID_TAG_NAME, DEFAULT_MODEL_ID);
        // 默认数据需要强制指定 YSM 渲染为空
        data.putBoolean(EntityMaid.IS_YSM_MODEL_TAG, false);
        return CustomData.of(data);
    }

    @Override
    public Component getName(ItemStack stack) {
        // 仅在客户端添加这个名称
        if (FMLEnvironment.getDist() == Dist.CLIENT && Minecraft.getInstance().level != null) {
            // 手办名字前缀
            MutableComponent prefix = Component.translatable("block.touhou_little_maid.garage_kit.prefix");
            CustomData data = getMaidData(stack);
            CompoundTag tag = data.copyTag();

            String entityId = tag.getStringOr(ENTITY_ID_TAG_NAME, DEFAULT_ENTITY_ID);
            // 如果是其他实体，那么不需要显示 model id
            if (!entityId.equals(DEFAULT_ENTITY_ID)) {
                Identifier parseId = Identifier.parse(entityId);
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(parseId);
                return prefix.append(entityType.getDescription());
            }

            // 优先使用 YSM 模型名称
            if (YsmCompat.isInstalled()) {
                YsmMaidInfo ysmMaidInfo = YsmCompat.getYsmMaidInfo(data.copyTag());
                if (ysmMaidInfo.isYsmModel()) {
                    JsonObject object = GsonHelper.parse(ysmMaidInfo.name());
                    Component name = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, object)
                            .result()
                            .orElse(Component.empty());
                    if (name.equals(Component.empty())) {
                        return prefix.append(ysmMaidInfo.modelId());
                    }
                    return prefix.append(name);
                }
            }

            // 然后才是默认模型名
            String modelId = tag.getStringOr(MODEL_ID_TAG_NAME, DEFAULT_MODEL_ID);
            MaidModelInfo info = CustomPackLoader.MAID_MODELS.getInfo(modelId).orElse(null);
            if (info != null) {
                return prefix.append(ParseI18n.parse(info.getName()));
            }
            return super.getName(stack);
        }
        return super.getName(stack);
    }
}
