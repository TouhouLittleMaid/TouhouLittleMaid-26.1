package com.github.tartaricacid.touhoulittlemaid.client.proxy;

import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.data.ProfileData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.item.ItemGarageKit;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;

public class ItemGarageKitProxy {
    /**
     * 手办名字前缀
     */
    private static final MutableComponent PREFIX = Component.translatable("block.touhou_little_maid.garage_kit.prefix");

    @Nullable
    public static Component getName(ItemStack stack) {
        if (Minecraft.getInstance().level == null) {
            return null;
        }

        CustomData data = ItemGarageKit.getMaidData(stack);
        CompoundTag tag = data.copyTag();

        MutableComponent prefix = PREFIX.copy();

        String entityId = tag.getStringOr(Entity.TAG_ID, EntityMaid.ENTITY_ID.toString());
        // 如果是其他实体，那么不需要显示 model id
        if (!entityId.equals(EntityMaid.ENTITY_ID.toString())) {
            Identifier parseId = Identifier.parse(entityId);
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(parseId);
            return prefix.append(entityType.getDescription());
        }

        String modelId = ProfileData.directGetModelId(tag);
        MaidModelInfo info = CustomPackLoader.MAID_MODELS.getInfo(modelId).orElse(null);
        if (info != null) {
            return prefix.append(ParseI18n.parse(info.getName()));
        }
        return null;
    }
}
