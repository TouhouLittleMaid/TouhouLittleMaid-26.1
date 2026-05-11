package com.github.tartaricacid.touhoulittlemaid.entity.backpack.data;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IBackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.github.tartaricacid.touhoulittlemaid.util.MaidFluidUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.List;

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
        if (!this.maid.level.isClientSide()) {
            CombinedResourceHandler<ItemResource> availableInv = this.maid.getAvailableInv(false);
            if (index == INPUT_INDEX || index == OUTPUT_INDEX) {
                //TODO 逻辑检查
                ItemStacksResourceHandler dummyHandler = ItemsUtil.createDummyHandler(List.of(stack));
                if (index == INPUT_INDEX)
                    MaidFluidUtil.bucketToTank(ItemAccess.forHandlerIndex(dummyHandler, 0), tank);
                else
                    MaidFluidUtil.tankToBucket(ItemAccess.forHandlerIndex(dummyHandler, 0), tank);
                if (!dummyHandler.getResource(0).isEmpty()) {
                    ResourceHandlerUtil.move(dummyHandler, availableInv, _ -> true, Integer.MAX_VALUE, null);
                }
                stack = dummyHandler.getResource(0).toStack(dummyHandler.getAmountAsInt(0));
            }
            this.tankFluidCount = tank.getAmountAsInt(0);
            Identifier key = BuiltInRegistries.FLUID.getKey(tank.getResource(0).getFluid());
            maid.setBackpackFluid(key.toString());
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
        tank.deserialize(tag.childOrEmpty("Tank"));
        loadTank(maid);
        ItemsUtil.loadContainer(this, "Items", tag);
    }

    @Override
    public void save(ValueOutput output, EntityMaid maid) {
        tank.serialize(output.child("Tank"));
        ItemsUtil.saveContainer(this, "Items", output);
    }

    @Override
    public void serverTick(EntityMaid maid) {
    }

    public FluidStacksResourceHandler getTank() {
        return tank;
    }

    public void loadTank(EntityMaid maid) {
        this.tankFluidCount = tank.getAmountAsInt(0);
        Identifier key = BuiltInRegistries.FLUID.getKey(tank.getResource(0).getFluid());
        maid.setBackpackFluid(key.toString());
    }
}
