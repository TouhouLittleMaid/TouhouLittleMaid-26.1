package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAndItemTransformEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.SpawnParticlePackage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemFilm extends AbstractStoreMaidItem {
    private static final String ID_TAG = "id";

    public ItemFilm(Identifier id) {
        super((new Item.Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    public static ItemStack maidToFilm(EntityMaid maid) {
        ItemStack film = InitItems.FILM.get().getDefaultInstance();
        maid.components.config.setHomeModeEnable(false);

        TagValueOutput valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, maid.registryAccess());
        maid.saveWithoutId(valueOutput);

        CompoundTag maidTag = new CompoundTag();
        maidTag.merge(valueOutput.buildResult());
        removeMaidSomeData(maidTag);
        maidTag.putString(ID_TAG, Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(InitEntities.MAID.get())).toString());

        var event = new MaidAndItemTransformEvent.ToItem(maid, film, maidTag);
        NeoForge.EVENT_BUS.post(event);

        film.set(InitDataComponent.MAID_INFO, CustomData.of(maidTag));
        return film;
    }

    public static void filmToMaid(ItemStack film, Level worldIn, BlockPos pos, Player player) {
        CustomData compoundData = film.get(InitDataComponent.MAID_INFO);
        if (compoundData == null) {
            return;
        }
        CompoundTag data = compoundData.copyTag();
        Optional<String> idOpt = data.getString(ID_TAG);
        if (idOpt.isEmpty()) {
            return;
        }

        Identifier entityId = Identifier.tryParse(idOpt.get());
        Identifier maidId = BuiltInRegistries.ENTITY_TYPE.getKey(InitEntities.MAID.get());

        if (entityId != null && entityId.equals(maidId)) {
            EntityMaid maid = new EntityMaid(worldIn);

            var event = new MaidAndItemTransformEvent.ToMaid(maid, film, data);
            NeoForge.EVENT_BUS.post(event);

            maid.load(TagValueInput.create(ProblemReporter.DISCARDING, worldIn.registryAccess(), data));
            maid.setPos(pos.getX(), pos.getY(), pos.getZ());
            // 实体生成必须在服务端应用
            if (!worldIn.isClientSide()) {
                worldIn.addFreshEntity(maid);
                NetworkHandler.sendToNearby(maid, new SpawnParticlePackage(maid.getId(), SpawnParticlePackage.Type.EXPLOSION));
                worldIn.playSound(null, pos, InitSounds.ALTAR_CRAFT.get(), SoundSource.VOICE, 1.0f, 1.0f);
            }
            film.shrink(1);
            return;
        }

        if (!worldIn.isClientSide()) {
            player.sendSystemMessage(Component.translatable("tooltips.touhou_little_maid.film.no_data.desc"));
        }
    }

    private static void removeMaidSomeData(CompoundTag nbt) {
        // TODO 删除部分不需要的内容
//        nbt.remove(EntityMaid.MAID_BACKPACK_TYPE);
//        nbt.remove(EntityMaid.MAID_INVENTORY_TAG);
//        nbt.remove(EntityMaid.MAID_BAUBLE_INVENTORY_TAG);
//        nbt.remove(EntityMaid.EXPERIENCE_TAG);
        nbt.remove("ArmorItems");
        nbt.remove("HandItems");
        nbt.remove("Leash");
        nbt.remove("Health");
        nbt.remove("HurtTime");
        nbt.remove("DeathTime");
        nbt.remove("HurtByTimestamp");
        nbt.remove("Pos");
        nbt.remove("Motion");
        nbt.remove("FallDistance");
        nbt.remove("Fire");
        nbt.remove("Air");
        nbt.remove("TicksFrozen");
        nbt.remove("HasVisualFire");
        nbt.remove("Passengers");
        nbt.remove("ActiveEffects");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext worldIn, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (stack.get(InitDataComponent.MAID_INFO) == null) {
            tooltip.accept(Component.translatable("tooltips.touhou_little_maid.film.no_data.desc").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
