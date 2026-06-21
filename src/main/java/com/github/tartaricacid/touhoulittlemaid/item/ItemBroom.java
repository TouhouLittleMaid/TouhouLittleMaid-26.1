package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBroom;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ItemBroom extends Item {
    public ItemBroom(Identifier id) {
        super((new Properties())
                .stacksTo(1)
                .setId(ResourceKey.create(Registries.ITEM, id)));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.FAIL;
        }

        Level world = context.getLevel();
        BlockPos clickedPos = new BlockPlaceContext(context).getClickedPos();
        Vec3 centerOf = Vec3.atBottomCenterOf(clickedPos);
        AABB boundingBox = EntityBroom.TYPE.getDimensions().makeBoundingBox(centerOf);
        if (!world.noCollision(boundingBox) || !world.getEntities(null, boundingBox).isEmpty()) {
            return InteractionResult.FAIL;
        }

        ItemStack stack = context.getItemInHand();
        if (world instanceof ServerLevel serverWorld) {
            EntityBroom broom = EntityBroom.TYPE.create(serverWorld, e -> {
                Component customName = stack.get(DataComponents.CUSTOM_NAME);
                if (customName != null) {
                    e.setCustomName(customName);
                }
            }, clickedPos, EntitySpawnReason.SPAWN_ITEM_USE, true, true);
            if (broom == null) {
                return InteractionResult.FAIL;
            }
            if (context.getPlayer() != null) {
                broom.setOwner(context.getPlayer());
            }
            world.addFreshEntity(broom);
            world.playSound(null, broom.getX(), broom.getY(), broom.getZ(),
                    SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
        }

        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.accept(Component
                .translatable("tooltips.touhou_little_maid.broom.desc")
                .withStyle(ChatFormatting.GRAY)
        );
    }
}
