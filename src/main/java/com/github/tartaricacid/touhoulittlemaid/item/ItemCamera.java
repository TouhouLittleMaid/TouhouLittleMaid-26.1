package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.util.MaidItemStorageHelper;
import com.github.tartaricacid.touhoulittlemaid.util.MaidRayTraceHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.Consumers;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ItemCamera extends Item {
    private static final int SEARCH_DISTANCE = 24;

    public ItemCamera(Identifier id) {
        super((new Properties())
                .stacksTo(1)
                .durability(50)
                .setId(ResourceKey.create(Registries.ITEM, id)));
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (handIn != InteractionHand.MAIN_HAND) {
            return super.use(worldIn, playerIn, handIn);
        }

        ItemStack camera = playerIn.getItemInHand(handIn);
        Optional<EntityMaid> result = MaidRayTraceHelper.rayTraceMaid(playerIn, SEARCH_DISTANCE);
        if (result.isEmpty()) {
            return super.use(worldIn, playerIn, handIn);
        }

        EntityMaid maid = result.get();
        if (maid.isAlive() && maid.isOwnedBy(playerIn) && !maid.isSleeping()) {
            ItemStack photo = InitItems.PHOTO.get().getDefaultInstance();
            MaidItemStorageHelper.saveMaid(photo, maid, Consumers.nop());
            playerIn.getInventory().placeItemBackInInventory(photo);

            maid.spawnExplosionParticle();
            maid.discard();

            camera.hurtAndBreak(1, playerIn, EquipmentSlot.MAINHAND);
            playerIn.getCooldowns().addCooldown(camera, 20);
            playerIn.playSound(InitSounds.CAMERA_USE.get(), 1.0f, 1.0f);

            if (playerIn instanceof ServerPlayer serverPlayer) {
                InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.PHOTO_MAID);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        // 返回 true，阻止打开女仆 GUI
        if (target instanceof EntityMaid maid && maid.isOwnedBy(playerIn)) {
            this.use(playerIn.level, playerIn, hand);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, playerIn, target, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.accept(Component
                .translatable("tooltips.touhou_little_maid.camera.desc")
                .withStyle(ChatFormatting.DARK_GREEN)
        );
    }
}
