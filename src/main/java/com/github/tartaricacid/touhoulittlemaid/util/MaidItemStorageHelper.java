package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAndItemTransformEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.data.BackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidItemManager;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.function.Consumers;

import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.world.entity.Entity.TAG_ID;

public final class MaidItemStorageHelper {
    /**
     * 将女仆数据保存到物品中
     *
     * @param stack     物品
     * @param maid      女仆实体
     * @param tagEditor 用于编辑女仆数据的函数，比如胶片需要剔除部分内容
     */
    public static void saveMaid(ItemStack stack, EntityMaid maid, Consumer<CompoundTag> tagEditor) {
        // 一定要把女仆 home 设置为 false，避免后续放出后里面传送走的 bug
        maid.setHomeModeEnable(false);

        TagValueOutput valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, maid.registryAccess());
        maid.saveWithoutId(valueOutput);
        CompoundTag maidTag = valueOutput.buildResult();

        maidTag.putString(TAG_ID, EntityMaid.ENTITY_ID.toString());
        tagEditor.accept(maidTag);

        var event = new MaidAndItemTransformEvent.ToItem(maid, stack, maidTag);
        NeoForge.EVENT_BUS.post(event);

        stack.set(InitDataComponent.MAID_INFO, CustomData.of(maidTag));
    }

    /**
     * 直接在玩家背包里生成一个带有指定 NBT 数据的女仆照片
     *
     * @param worldIn  事件
     * @param data     NBT 数据
     * @param playerIn 玩家
     */
    public static void spawnMaidPhoto(Level worldIn, CompoundTag data, Player playerIn) {
        ItemStack photo = InitItems.PHOTO.get().getDefaultInstance();
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, worldIn.registryAccess(), data);
        Optional<Entity> optional = EntityType.create(input, worldIn, EntitySpawnReason.SPAWN_ITEM_USE);
        if (optional.isEmpty() || !(optional.get() instanceof EntityMaid maid)) {
            return;
        }

        MaidItemStorageHelper.saveMaid(photo, maid, Consumers.nop());
        playerIn.getInventory().placeItemBackInInventory(photo);
    }

    /**
     * 将 NBT 数据写入女仆实体中
     *
     * @param stack   存储女仆数据的物品，这里仅用于事件
     * @param maid    准备写入 NBT 的女仆对象
     * @param maidTag 准备写入的 NBT
     */
    public static void loadMaid(ItemStack stack, EntityMaid maid, CompoundTag maidTag) {
        var event = new MaidAndItemTransformEvent.ToMaid(maid, stack, maidTag);
        NeoForge.EVENT_BUS.post(event);

        RegistryAccess access = maid.level().registryAccess();
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, access, maidTag);
        maid.load(input);
    }

    /**
     * 胶片化时，需要删除的 tag 数据
     */
    public static void sanitizeFilmMaidData(CompoundTag tag) {
        tag.getCompound(AttachmentHolder.ATTACHMENTS_NBT_KEY).ifPresent(data -> {
            data.remove(BackpackData.KEY);
            data.remove(MaidItemManager.MAID_INVENTORY_TAG);
            data.remove(MaidItemManager.MAID_BAUBLE_INVENTORY_TAG);
            data.remove(MaidItemManager.MAID_HIDE_INVENTORY_TAG);
            data.remove(MaidItemManager.MAID_TASK_INVENTORY_TAG);
        });

        tag.remove(Entity.TAG_POS);
        tag.remove(Entity.TAG_MOTION);
        tag.remove(Entity.TAG_FALL_DISTANCE);
        tag.remove(Entity.TAG_FIRE);
        tag.remove(Entity.TAG_AIR);
        tag.remove(Entity.TAG_PASSENGERS);

        tag.remove(LivingEntity.TAG_EQUIPMENT);
        tag.remove(LivingEntity.TAG_HEALTH);
        tag.remove(LivingEntity.TAG_HURT_TIME);
        tag.remove(LivingEntity.TAG_DEATH_TIME);
        tag.remove(LivingEntity.TAG_HURT_BY_TIMESTAMP);

        tag.remove(Leashable.LEASH_TAG);

        tag.remove("TicksFrozen");
        tag.remove("HasVisualFire");
        tag.remove("active_effects"); // LivingEntity.TAG_ACTIVE_EFFECTS
    }
}
