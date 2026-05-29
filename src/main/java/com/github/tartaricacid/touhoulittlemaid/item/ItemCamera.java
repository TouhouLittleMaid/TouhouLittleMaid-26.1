package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAndItemTransformEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.*;
import com.github.tartaricacid.touhoulittlemaid.util.MaidRayTraceHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemCamera extends Item {
    public ItemCamera(Identifier id) {
        super((new Properties())
                .stacksTo(1)
                .durability(50)
                .setId(ResourceKey.create(Registries.ITEM, id)));
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (handIn == InteractionHand.MAIN_HAND) {
            int searchDistance = 8;
            ItemStack camera = playerIn.getItemInHand(handIn);
            Optional<EntityMaid> result = MaidRayTraceHelper.rayTraceMaid(playerIn, searchDistance);
            if (result.isPresent()) {
                EntityMaid maid = result.get();
                if (!worldIn.isClientSide() && maid.isAlive() && maid.isOwnedBy(playerIn) && !maid.isSleeping()) {
                    spawnMaidPhoto(worldIn, maid, playerIn);
                    maid.discard();
                    playerIn.getCooldowns().addCooldown(camera, 20);
                    camera.hurtAndBreak(1, playerIn, EquipmentSlot.MAINHAND);
                    if (playerIn instanceof ServerPlayer serverPlayer) {
                        InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.PHOTO_MAID);
                    }
                }
                maid.components().particle.spawnExplosionParticle();
                playerIn.playSound(InitSounds.CAMERA_USE.get(), 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }

    public static void spawnMaidPhoto(Level worldIn, CompoundTag data, Player playerIn) {
        ItemStack photo = InitItems.PHOTO.get().getDefaultInstance();
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, worldIn.registryAccess(), data);
        Optional<Entity> optional = EntityType.create(input, worldIn, EntitySpawnReason.SPAWN_ITEM_USE);
        if (optional.isEmpty() || !(optional.get() instanceof EntityMaid maid)) {
            return;
        }

        maid.components().config.setHomeModeEnable(false);
        TagValueOutput valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, maid.registryAccess());
        maid.saveWithoutId(valueOutput);

        CompoundTag maidTag = new CompoundTag();
        maidTag.merge(valueOutput.buildResult());
        maidTag.putString("id", Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(InitEntities.MAID.get())).toString());

        var event = new MaidAndItemTransformEvent.ToItem(maid, photo, maidTag);
        NeoForge.EVENT_BUS.post(event);

        photo.set(InitDataComponent.MAID_INFO, CustomData.of(maidTag));
        playerIn.getInventory().placeItemBackInInventory(photo);
    }

    private void spawnMaidPhoto(Level worldIn, EntityMaid maid, Player playerIn) {
        ItemStack photo = InitItems.PHOTO.get().getDefaultInstance();
        maid.components().config.setHomeModeEnable(false);
        TagValueOutput valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, maid.registryAccess());
        maid.saveWithoutId(valueOutput);

        CompoundTag maidTag = new CompoundTag();
        maidTag.merge(valueOutput.buildResult());
        maidTag.putString("id", Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(InitEntities.MAID.get())).toString());

        var event = new MaidAndItemTransformEvent.ToItem(maid, photo, maidTag);
        NeoForge.EVENT_BUS.post(event);

        photo.set(InitDataComponent.MAID_INFO, CustomData.of(maidTag));
        Containers.dropItemStack(worldIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), photo);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        // 返回 true，阻止打开女仆 GUI
        if (stack.getItem() == this && target.isAlive() && target instanceof EntityMaid && ((EntityMaid) target).isOwnedBy(playerIn)) {
            this.use(playerIn.level, playerIn, hand);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, playerIn, target, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.accept(Component.translatable("tooltips.touhou_little_maid.camera.desc").withStyle(ChatFormatting.DARK_GREEN));
    }
}
