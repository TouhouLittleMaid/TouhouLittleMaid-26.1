package com.github.tartaricacid.touhoulittlemaid.entity.backpack.data;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IBackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.MaidFluidUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class TankBackpackData extends SimpleContainer implements IBackpackData {
    public static final int CAPACITY = 10 * FluidType.BUCKET_VOLUME;
    private static final int INPUT_INDEX = 0;
    private static final int OUTPUT_INDEX = 1;
    private final EntityMaid maid;
    private final FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1, CAPACITY);
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) {
                return TankBackpackData.this.tankFluidCount;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                TankBackpackData.this.tankFluidCount = value;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    private int tankFluidCount = 0;

    public TankBackpackData(EntityMaid maid) {
        super(2);
        this.maid = maid;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (!this.maid.level().isClientSide()) {
            ResourceHandler<ItemResource> availableInv = this.maid.getAvailableInv(false);
            FluidStacksResourceHandler tankSlot = createTankFluidSlotView();
            boolean moved = false;
            if (index == INPUT_INDEX) {
                moved = MaidFluidUtil.bucketToTank(stack, tankSlot, availableInv);
            }
            if (index == OUTPUT_INDEX) {
                moved = MaidFluidUtil.tankToBucket(stack, tankSlot, availableInv);
            }
            if (moved) {
                FluidStack after = FluidUtil.getStack(tankSlot, 0).copy();
                this.tank.set(0, FluidResource.of(after), after.getAmount());
            }
            this.tankFluidCount = FluidUtil.getStack(this.tank, 0).getAmount();
            FluidStack fluidStack = FluidUtil.getStack(this.tank, 0);
            if (fluidStack.isEmpty()) {
                this.maid.setBackpackFluid("");
            } else {
                Identifier key = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
                this.maid.setBackpackFluid(key != null ? key.toString() : "");
            }
        }
        super.setItem(index, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    public void load(ValueInput tag, EntityMaid maid) {
        this.clearContent();
        tag.child("Tanks").ifPresent(this::readTankNbt);
        ContainerHelper.loadAllItems(tag, this.getItems());
    }

    @Override
    public void save(ValueOutput tag, EntityMaid maid) {
        FluidStack fluidStack = FluidUtil.getStack(this.tank, 0);
        if (fluidStack.isEmpty()) {
            tag.discard("Tanks");
        } else {
            ValueOutput tanks = tag.child("Tanks");
            this.writeTankCompound(tanks);
        }
        ContainerHelper.saveAllItems(tag, this.getItems());
    }

    @Override
    public void serverTick(EntityMaid maid) {
    }

    public FluidStacksResourceHandler getTank() {
        return this.tank;
    }

    public void loadTank(ValueInput tag, EntityMaid maid) {
        this.readTankNbt(tag);
        this.tankFluidCount = FluidUtil.getStack(this.tank, 0).getAmount();
        FluidStack fluidStack = FluidUtil.getStack(this.tank, 0);
        if (fluidStack.isEmpty()) {
            maid.setBackpackFluid("");
        } else {
            Identifier key = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
            maid.setBackpackFluid(key != null ? key.toString() : "");
        }
    }

    private void readTankNbt(ValueInput tag) {
        tag.read("Fluid", FluidStack.CODEC).ifPresentOrElse(
                s -> {
                    if (s.isEmpty()) {
                        this.tank.set(0, FluidResource.EMPTY, 0);
                    } else {
                        this.tank.set(0, FluidResource.of(s), s.getAmount());
                    }
                },
                () -> this.tank.deserialize(tag)
        );
    }

    private void writeTankCompound(ValueOutput tag) {
        FluidStack fluidStack = FluidUtil.getStack(this.tank, 0);
        if (fluidStack.isEmpty()) {
            return;
        }
        tag.store("Fluid", FluidStack.CODEC, fluidStack);
    }

    private FluidStacksResourceHandler createTankFluidSlotView() {
        NonNullList<FluidStack> slots = NonNullList.withSize(1, FluidStack.EMPTY);
        slots.set(0, FluidUtil.getStack(this.tank, 0).copy());
        return new FluidStacksResourceHandler(slots, CAPACITY);
    }
}
