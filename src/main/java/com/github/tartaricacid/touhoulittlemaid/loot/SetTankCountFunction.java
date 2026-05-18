package com.github.tartaricacid.touhoulittlemaid.loot;

import com.github.tartaricacid.touhoulittlemaid.entity.backpack.data.TankBackpackData;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.TANK_BACKPACK_TAG;

public class SetTankCountFunction extends LootItemConditionalFunction {
    public static MapCodec<SetTankCountFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(instance.group(
                    Identifier.CODEC.fieldOf("fluid_id").forGetter(f -> f.fluidId),
                    Codec.INT.fieldOf("count").forGetter(f -> f.count)
            )).apply(instance, SetTankCountFunction::new));

    private final Identifier fluidId;
    private final int count;

    public SetTankCountFunction(List<LootItemCondition> predicates, Identifier fluidId, int count) {
        super(predicates);
        this.fluidId = fluidId;
        this.count = count;
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        CompoundTag tags = stack.get(TANK_BACKPACK_TAG);
        if (tags == null) {
            tags = new CompoundTag();
        }
        Fluid fluid = BuiltInRegistries.FLUID.getValue(this.fluidId);
        if (fluid == null || fluid.isSame(Fluids.EMPTY)) {
            stack.set(InitDataComponent.TANK_BACKPACK_TAG, tags);
            return stack;
        }
        FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1, TankBackpackData.CAPACITY);
        FluidStack fluidStack = new FluidStack(fluid, this.count);
        tank.set(0, FluidResource.of(fluidStack), this.count);
        tags.store("Fluid", FluidStack.CODEC, context.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE), FluidUtil.getStack(tank, 0));
        stack.set(InitDataComponent.TANK_BACKPACK_TAG, tags);
        return stack;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetTankCountFunction.Builder> {
        private final Fluid fluid;
        private final int bucketCount;

        public Builder(Fluid fluid, int bucketCount) {
            this.fluid = fluid;
            this.bucketCount = bucketCount;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            Identifier key = BuiltInRegistries.FLUID.getKey(fluid);
            return new SetTankCountFunction(this.getConditions(), key, bucketCount * FluidType.BUCKET_VOLUME);
        }
    }
}
