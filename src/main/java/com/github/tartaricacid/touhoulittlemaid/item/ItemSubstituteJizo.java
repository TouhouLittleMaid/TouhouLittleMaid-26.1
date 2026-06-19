package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.init.InitItems.SUBSTITUTE_JIZO;

@SuppressWarnings("deprecation")
public class ItemSubstituteJizo extends Item {
    public ItemSubstituteJizo(Identifier id) {
        super(new Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
                                                  InteractionHand type) {
        if (!(target instanceof EntityMaid maid)) {
            return InteractionResult.PASS;
        }
        if (maid.isOwnedBy(player) && stack.is(SUBSTITUTE_JIZO) && !maid.getSyncInvulnerable()) {
            maid.setSyncInvulnerable(true);
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext worldIn, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(Component
                .translatable("tooltips.touhou_little_maid.substitute_jizo.desc")
                .withStyle(ChatFormatting.GRAY)
        );
    }
}
