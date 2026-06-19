package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityThrowPowerPoint;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemPowerPoint extends Item {
    public ItemPowerPoint(Identifier id) {
        super(new Properties().setId(ResourceKey.create(Registries.ITEM, id)));
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide()) {
            EntityThrowPowerPoint powerPoint = new EntityThrowPowerPoint(world, player);
            powerPoint.setItem(stack);
            powerPoint.shootFromRotation(player, player.getXRot(), player.getYRot(),
                    -20.0F, 0.7F, 1.0F);
            world.addFreshEntity(powerPoint);
        }

        float pitch = 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F);
        player.playSound(SoundEvents.EXPERIENCE_BOTTLE_THROW, 0.5F, pitch);
        player.awardStat(Stats.ITEM_USED.get(this));

        if (!player.isCreative()) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
