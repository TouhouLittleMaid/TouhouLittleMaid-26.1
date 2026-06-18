package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.util.PlaceHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ItemPhoto extends AbstractStoreMaidItem {
    public ItemPhoto(Identifier id) {
        super((new Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction clickedFace = context.getClickedFace();
        Player player = context.getPlayer();
        Level worldIn = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        if (player == null) {
            return super.useOn(context);
        }

        ItemStack itemInHand = context.getItemInHand();
        if (clickedFace == Direction.UP && !PlaceHelper.notSuitableForPlaceMaid(worldIn, clickedPos)) {
            EntityMaid maid = InitEntities.MAID.get().create(worldIn, EntitySpawnReason.SPAWN_ITEM_USE);
            if (maid == null) {
                return super.useOn(context);
            }
            return spawnFromStore(context, player, worldIn, maid, () -> itemInHand.shrink(1));
        }

        if (!worldIn.isClientSide()) {
            MutableComponent msg = Component.translatable("message.touhou_little_maid.photo.not_suitable_for_place_maid");
            player.sendSystemMessage(msg);
        }
        return super.useOn(context);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext worldIn, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (!stack.has(InitDataComponent.MAID_INFO)) {
            tooltip.accept(Component.translatable("tooltips.touhou_little_maid.photo.no_data.desc")
                    .withStyle(ChatFormatting.DARK_RED));
        }
    }
}
