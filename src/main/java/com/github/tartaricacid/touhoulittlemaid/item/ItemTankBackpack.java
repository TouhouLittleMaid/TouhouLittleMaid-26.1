package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.entity.backpack.data.TankBackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

import java.util.Optional;
import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.TANK_BACKPACK_TAG;

public class ItemTankBackpack extends ItemMaidBackpack {
    public ItemTankBackpack(Identifier id) {
        super(id);
    }

    public static ItemStack getTankBackpack(HolderLookup.Provider provider, TankBackpackData data) {
        ItemStack backpack = InitItems.TANK_BACKPACK.get().getDefaultInstance();
        CompoundTag tags = backpack.get(TANK_BACKPACK_TAG);
        if (tags == null) {
            tags = new CompoundTag();
            backpack.set(InitDataComponent.TANK_BACKPACK_TAG, tags);
        }
        FluidStack fluidStack = FluidUtil.getStack(data.getTank(), 0);
        if (fluidStack.isEmpty()) {
            tags.remove("Fluid");
        } else {
            tags.store("Fluid", FluidStack.CODEC, provider.createSerializationContext(NbtOps.INSTANCE), fluidStack);
        }
        return backpack;
    }

    public static void setTankBackpack(EntityMaid maid, TankBackpackData data, ItemStack backpack) {
        CompoundTag tags = backpack.get(TANK_BACKPACK_TAG);
        if (tags == null) {
            tags = new CompoundTag();
            backpack.set(InitDataComponent.TANK_BACKPACK_TAG, tags);
        }
        data.loadTank(tags, maid);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag nbt = stack.get(TANK_BACKPACK_TAG);
        if (nbt == null) {
            return;
        }
        Optional<CompoundTag> fluidCompound = nbt.getCompound("Fluid");
        if (fluidCompound.isEmpty() || fluidCompound.get().isEmpty()) {
            return;
        }
        HolderLookup.Provider registries = context.registries();
        if (registries == null) {
            return;
        }
        FluidStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), fluidCompound.get()).result().ifPresent(fluidStack -> {
            if (fluidStack.getFluid() == Fluids.EMPTY || fluidStack.getAmount() == 0) {
                tooltip.accept(Component.translatable("tooltips.touhou_little_maid.tank_backpack.empty_fluid").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.accept(Component.translatable("tooltips.touhou_little_maid.tank_backpack.fluid",
                        fluidStack.getFluid().getFluidType().getDescription(), fluidStack.getAmount()).withStyle(ChatFormatting.GRAY));
            }
        });
    }
}
