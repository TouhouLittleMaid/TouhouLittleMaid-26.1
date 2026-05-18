package com.github.tartaricacid.touhoulittlemaid.util;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class MaidFluidUtil {
    private static final Predicate<FluidResource> NON_EMPTY = r -> !r.isEmpty();

    @Nullable
    private static ResourceHandler<FluidResource> fluidOnItem(ItemStack stack) {
        return ItemAccess.forStack(stack).oneByOne().getCapability(Capabilities.Fluid.ITEM);
    }

    public static boolean tankToBucket(ItemStack container, FluidStacksResourceHandler tankFluidSlot, @Nullable ResourceHandler<ItemResource> maidInv) {
        if (container.isEmpty()) {
            return false;
        }
        if (container.getCount() == 1) {
            return tankToBucketSingle(container, tankFluidSlot);
        }
        if (maidInv == null) {
            return false;
        }
        ItemStack probe = container.copyWithCount(1);
        ResourceHandler<FluidResource> probeFluid = fluidOnItem(probe);
        if (probeFluid == null) {
            return false;
        }
        ItemStack filledSim;
        try (Transaction probeTx = Transaction.openRoot()) {
            if (ResourceHandlerUtil.move(tankFluidSlot, probeFluid, NON_EMPTY, Integer.MAX_VALUE, probeTx) == 0) {
                return false;
            }
            // probe 已在 transaction 内被 move 修改，此时复制以捕获模拟后的状态
            filledSim = probe.copy();
        }

        try (Transaction outer = Transaction.openRoot()) {
            if (!ItemsUtil.insertItemStacked(maidInv, filledSim, true, outer).isEmpty()) {
                return false;
            }
            ItemStack one = container.split(1);
            ResourceHandler<FluidResource> oneFluid = fluidOnItem(one);
            if (oneFluid == null) {
                container.grow(1);
                return false;
            }
            if (ResourceHandlerUtil.move(tankFluidSlot, oneFluid, NON_EMPTY, Integer.MAX_VALUE, outer) == 0) {
                container.grow(1);
                return false;
            }
            if (!ItemsUtil.insertItemStacked(maidInv, one, false, outer).isEmpty()) {
                container.grow(1);
                return false;
            }
            outer.commit();
            return true;
        }
    }

    private static boolean tankToBucketSingle(ItemStack container, FluidStacksResourceHandler tankFluidSlot) {
        ResourceHandler<FluidResource> itemFluid = fluidOnItem(container);
        if (itemFluid == null) {
            return false;
        }
        try (Transaction tx = Transaction.openRoot()) {
            if (ResourceHandlerUtil.move(tankFluidSlot, itemFluid, NON_EMPTY, Integer.MAX_VALUE, tx) == 0) {
                return false;
            }
            tx.commit();
        }
        return true;
    }

    public static boolean bucketToTank(ItemStack container, FluidStacksResourceHandler tankFluidSlot, @Nullable ResourceHandler<ItemResource> maidInv) {
        if (container.isEmpty()) {
            return false;
        }
        if (container.getCount() == 1) {
            return bucketToTankSingle(container, tankFluidSlot);
        }
        if (maidInv == null) {
            return false;
        }
        ItemStack probe = container.copyWithCount(1);
        ResourceHandler<FluidResource> probeFluid = fluidOnItem(probe);
        if (probeFluid == null) {
            return false;
        }
        ItemStack emptiedSim;
        try (Transaction probeTx = Transaction.openRoot()) {
            if (ResourceHandlerUtil.move(probeFluid, tankFluidSlot, NON_EMPTY, Integer.MAX_VALUE, probeTx) == 0) {
                return false;
            }
            // probe 已在 transaction 内被 move 修改，此时复制以捕获模拟后的状态
            emptiedSim = probe.copy();
        }

        try (Transaction outer = Transaction.openRoot()) {
            if (!ItemsUtil.insertItemStacked(maidInv, emptiedSim, true, outer).isEmpty()) {
                return false;
            }
            ItemStack one = container.split(1);
            ResourceHandler<FluidResource> oneFluid = fluidOnItem(one);
            if (oneFluid == null) {
                container.grow(1);
                return false;
            }
            if (ResourceHandlerUtil.move(oneFluid, tankFluidSlot, NON_EMPTY, Integer.MAX_VALUE, outer) == 0) {
                container.grow(1);
                return false;
            }
            if (!ItemsUtil.insertItemStacked(maidInv, one, false, outer).isEmpty()) {
                container.grow(1);
                return false;
            }
            outer.commit();
            return true;
        }
    }

    private static boolean bucketToTankSingle(ItemStack container, FluidStacksResourceHandler tankFluidSlot) {
        ResourceHandler<FluidResource> itemFluid = fluidOnItem(container);
        if (itemFluid == null) {
            return false;
        }
        try (Transaction tx = Transaction.openRoot()) {
            if (ResourceHandlerUtil.move(itemFluid, tankFluidSlot, NON_EMPTY, Integer.MAX_VALUE, tx) == 0) {
                return false;
            }
            tx.commit();
        }
        return true;
    }
}
