package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.world.data.MaidInfo;
import com.github.tartaricacid.touhoulittlemaid.world.data.MaidWorldData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ItemTrumpet extends Item {
    private static final int MIN_USE_DURATION = 20;

    public ItemTrumpet(Identifier id) {
        super(new Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player) || timeLeft < MIN_USE_DURATION) {
            return false;
        }

        if (worldIn instanceof ServerLevel serverLevel) {
            serverLevel.getEntities(EntityMaid.TYPE, Entity::isAlive).stream()
                    .filter(maid -> maid.isOwnedBy(player))
                    .forEach(maid -> teleportToOwner(maid, player));

            MaidWorldData data = MaidWorldData.get(worldIn);
            if (data != null) {
                List<MaidInfo> infos = data.getPlayerMaidInfos(player);
                if (infos != null && !infos.isEmpty()) {
                    player.sendSystemMessage(Component
                            .translatable("message.touhou_little_maid.trumpet.unloaded_maid", infos.size())
                            .withStyle(ChatFormatting.DARK_RED)
                    );
                }
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.USE_TRUMPET);
        }
        player.getCooldowns().addCooldown(stack, 200);
        return true;
    }

    private void teleportToOwner(EntityMaid maid, Player player) {
        maid.setHomeModeEnable(false);
        // 如果女仆是骑乘某个实体的，先让女仆下来
        if (maid.isPassenger()) {
            maid.stopRiding();
        }
        double x = player.getX() + player.getRandom().nextInt(3) - 1;
        double z = player.getZ() + player.getRandom().nextInt(3) - 1;
        maid.teleportTo(x, player.getY(), z);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity pEntity) {
        return 100;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        playerIn.startUsingItem(handIn);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext worldIn, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(Component
                .translatable("tooltips.touhou_little_maid.trumpet.desc.usage")
                .withStyle(ChatFormatting.GRAY)
        );

        tooltip.accept(Component
                .translatable("tooltips.touhou_little_maid.trumpet.desc.note")
                .withStyle(ChatFormatting.DARK_RED)
        );
    }
}
