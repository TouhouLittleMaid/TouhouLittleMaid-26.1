package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class ItemFairySpawnEgg extends SpawnEggItem {
    public ItemFairySpawnEgg(Identifier id) {
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id)));
    }

    // FIXME 需要新的方案修正
//    @Override
//    public Optional<Mob> offspringFromSpawnEgg(Player player, Mob parentMob, EntityType<? extends Mob> type, ServerLevel level, Vec3 pos, ItemStack stack) {
//        if (!SpawnEggItem.spawnsEntity(stack, type)) {
//            return Optional.empty();
//        }
//        if (!(parentMob instanceof EntityFairy)) {
//            return Optional.empty();
//        }
//        EntityFairy fairy = EntityFairy.TYPE.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
//        if (fairy == null) {
//            return Optional.empty();
//        }
//        fairy.finalizeSpawn(level, level.getCurrentDifficultyAt(fairy.blockPosition()), EntitySpawnReason.SPAWN_ITEM_USE, null);
//        fairy.setBaby(true);
//        if (!fairy.isBaby()) {
//            return Optional.empty();
//        }
//        fairy.snapTo(pos.x(), pos.y(), pos.z(), 0.0F, 0.0F);
//        level.addFreshEntityWithPassengers(fairy);
//        fairy.setCustomName(stack.get(DataComponents.CUSTOM_NAME));
//        if (!player.isCreative()) {
//            stack.shrink(1);
//        }
//        return Optional.of(fairy);
//    }
}
