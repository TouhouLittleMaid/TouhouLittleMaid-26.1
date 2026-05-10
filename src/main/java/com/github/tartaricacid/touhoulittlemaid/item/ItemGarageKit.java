package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.TileEntityItemStackGarageKitRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.compat.ysm.YsmCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.inventory.tooltip.YsmMaidInfo;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.Objects;
import java.util.function.Supplier;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.ENTITY_ID_TAG_NAME;
import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.MODEL_ID_TAG_NAME;

public class ItemGarageKit extends BlockItem {
    public static final IClientItemExtensions ITEM_EXTENSIONS = FMLEnvironment.dist == Dist.CLIENT ? new IClientItemExtensions() {
        private static final Supplier<TileEntityItemStackGarageKitRenderer> MEMOIZE = Suppliers.memoize(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            return new TileEntityItemStackGarageKitRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        });

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return MEMOIZE.get();
        }
    } : null;
    private static final String DEFAULT_ENTITY_ID = "touhou_little_maid:maid";
    private static final String DEFAULT_MODEL_ID = "touhou_little_maid:hakurei_reimu";
    private static final CustomData DEFAULT_DATA = getDefaultData();

    public ItemGarageKit() {
        super(InitBlocks.GARAGE_KIT.get(), (new Item.Properties()).stacksTo(1));
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
    @OnlyIn(Dist.CLIENT)
    public Component getName(ItemStack stack) {
        // 仅在客户端添加这个名称
        if (FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance().level != null) {
            // 手办名字前缀
            MutableComponent prefix = Component.translatable("block.touhou_little_maid.garage_kit.prefix");
            CustomData data = getMaidData(stack);

            String entityId = data.read(Codec.STRING.fieldOf(ENTITY_ID_TAG_NAME)).result().orElse(DEFAULT_ENTITY_ID);
            // 如果是其他实体，那么不需要显示 model id
            if (!entityId.equals(DEFAULT_ENTITY_ID)) {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityId));
                return prefix.append(entityType.getDescription());
            }

            // 优先使用 YSM 模型名称
            if (YsmCompat.isInstalled()) {
                YsmMaidInfo ysmMaidInfo = YsmCompat.getYsmMaidInfo(data.copyTag());
                if (ysmMaidInfo.isYsmModel()) {
                    MutableComponent name = Component.Serializer.fromJson(ysmMaidInfo.name(), Minecraft.getInstance().level.registryAccess());
                    if (name == null || name.equals(Component.empty())) {
                        return prefix.append(ysmMaidInfo.modelId());
                    }
                    return prefix.append(name);
                }
            }

            // 然后才是默认模型名
            String modelId = data.read(Codec.STRING.fieldOf(MODEL_ID_TAG_NAME)).result().orElse(DEFAULT_MODEL_ID);
            MaidModelInfo info = CustomPackLoader.MAID_MODELS.getInfo(modelId).orElse(null);
            if (info != null) {
                return prefix.append(ParseI18n.parse(info.getName()));
            }
            return super.getName(stack);
        }
        return super.getName(stack);
    }
}
