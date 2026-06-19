package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.client.proxy.ItemGarageKitProxy;
import com.github.tartaricacid.touhoulittlemaid.entity.data.ProfileData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.ENTITY_ID_TAG_NAME;

public class ItemGarageKit extends BlockItem {
    public static final String DEFAULT_MODEL_ID = "touhou_little_maid:hakurei_reimu";
    public static final CustomData DEFAULT_DATA = getDefaultData();

    public ItemGarageKit(Identifier id) {
        super(InitBlocks.GARAGE_KIT.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    public static CustomData getMaidData(ItemStack stack) {
        return stack.getOrDefault(InitDataComponent.MAID_INFO, DEFAULT_DATA);
    }

    private static CustomData getDefaultData() {
        CompoundTag data = new CompoundTag();
        data.putString(ENTITY_ID_TAG_NAME, EntityMaid.ENTITY_ID.toString());
        ProfileData.directSetModelId(data, DEFAULT_MODEL_ID);
        return CustomData.of(data);
    }

    @Override
    public Component getName(ItemStack stack) {
        // 仅在客户端添加这个名称
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            Component name = ItemGarageKitProxy.getName(stack);
            if (name != null) {
                return name;
            }
            return super.getName(stack);
        }
        return super.getName(stack);
    }
}
