package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.entity.data.ProfileData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.inventory.tooltip.ItemMaidTooltip;
import com.github.tartaricacid.touhoulittlemaid.util.MaidItemStorageHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

import static net.minecraft.world.entity.Entity.TAG_CUSTOM_NAME;

public abstract class AbstractStoreMaidItem extends Item {
    private static final String MAID_OWNER = "Owner";

    public AbstractStoreMaidItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() > -32768) {
            entity.setUnlimitedLifetime();
        }
        if (!entity.isCurrentlyGlowing()) {
            entity.setGlowingTag(true);
        }
        if (!entity.isInvulnerable()) {
            entity.setInvulnerable(true);
        }
        Vec3 position = entity.position();
        int minY = entity.level.getMinY();
        if (position.y < minY) {
            entity.setNoGravity(true);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.setPos(position.x, minY, position.z);
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        CustomData maidInfo = stack.get(InitDataComponent.MAID_INFO);
        if (maidInfo == null) {
            return Optional.empty();
        }

        CompoundTag tag = maidInfo.copyTag();
        String modelId = ProfileData.directGetModelId(tag);
        var customName = tag.read(TAG_CUSTOM_NAME, ComponentSerialization.CODEC).orElse(null);
        return Optional.of(new ItemMaidTooltip(modelId, customName));
    }

    public InteractionResult spawnFromStore(UseOnContext context, Player player, Level worldIn, EntityMaid maid, Runnable runnable) {
        ItemStack stack = context.getItemInHand();
        CustomData compoundData = stack.get(InitDataComponent.MAID_INFO);
        if (compoundData == null) {
            if (!worldIn.isClientSide()) {
                MutableComponent msg = Component.translatable("message.touhou_little_maid.photo.have_no_nbt_data");
                player.sendSystemMessage(msg);
            }
            return InteractionResult.FAIL;
        }

        CompoundTag maidCompound = compoundData.copyTag();
        UUID ownerUid = maidCompound.read(UUIDUtil.CODEC.fieldOf(MAID_OWNER)).orElse(null);
        if (!player.getUUID().equals(ownerUid)) {
            MutableComponent msg = Component
                    .translatable("tooltips.touhou_little_maid.smart_slab.not_your_maid")
                    .withStyle(ChatFormatting.DARK_RED);
            if (!worldIn.isClientSide()) {
                player.sendSystemMessage(msg);
            }
            return InteractionResult.FAIL;
        }

        MaidItemStorageHelper.loadMaid(stack, maid, maidCompound);
        maid.snapTo(context.getClickedPos().above(), 0, 0);
        if (worldIn instanceof ServerLevel) {
            worldIn.addFreshEntity(maid);
        }

        maid.spawnExplosionParticle();
        maid.playSound(SoundEvents.PLAYER_SPLASH, 1.0F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);

        runnable.run();
        return InteractionResult.SUCCESS;
    }
}
