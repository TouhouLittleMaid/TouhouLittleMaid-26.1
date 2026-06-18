package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.SpawnParticlePackage;
import com.github.tartaricacid.touhoulittlemaid.util.MaidItemStorageHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ItemFilm extends AbstractStoreMaidItem {
    public ItemFilm(Identifier id) {
        super((new Item.Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    public static ItemStack maidToFilm(EntityMaid maid) {
        ItemStack film = InitItems.FILM.get().getDefaultInstance();
        MaidItemStorageHelper.saveMaid(film, maid, MaidItemStorageHelper::sanitizeFilmMaidData);
        return film;
    }

    public static void filmToMaid(ItemStack film, Level worldIn, BlockPos pos, Player player) {
        CustomData compoundData = film.get(InitDataComponent.MAID_INFO);
        if (compoundData == null) {
            if (!worldIn.isClientSide()) {
                player.sendSystemMessage(Component.translatable("tooltips.touhou_little_maid.film.no_data.desc"));
            }
            return;
        }

        CompoundTag data = compoundData.copyTag();
        Optional<String> idOpt = data.getString(Entity.TAG_ID);
        if (idOpt.isEmpty()) {
            if (!worldIn.isClientSide()) {
                player.sendSystemMessage(Component.translatable("tooltips.touhou_little_maid.film.no_data.desc"));
            }
            return;
        }

        EntityMaid maid = new EntityMaid(worldIn);
        MaidItemStorageHelper.loadMaid(film, maid, data);
        maid.snapTo(pos.getX(), pos.getY(), pos.getZ());

        // 实体生成必须在服务端应用
        if (!worldIn.isClientSide()) {
            worldIn.addFreshEntity(maid);
            NetworkHandler.sendToNearby(maid, new SpawnParticlePackage(maid.getId(), SpawnParticlePackage.Type.EXPLOSION));
            worldIn.playSound(null, pos, InitSounds.ALTAR_CRAFT.get(), SoundSource.VOICE, 1.0f, 1.0f);
        }

        film.shrink(1);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext worldIn, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (!stack.has(InitDataComponent.MAID_INFO)) {
            MutableComponent msg = Component.translatable("tooltips.touhou_little_maid.film.no_data.desc");
            tooltip.accept(msg.withStyle(ChatFormatting.DARK_RED));
        }
    }
}
