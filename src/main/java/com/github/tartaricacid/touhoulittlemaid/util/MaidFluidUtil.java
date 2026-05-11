package com.github.tartaricacid.touhoulittlemaid.util;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class MaidFluidUtil {
    public static boolean tankToBucket(ItemAccess bucket, ResourceHandler<FluidResource> tank) {
        if (bucket.getResource().isEmpty()) {
            return false;
        }
        ItemResource bucketItem = bucket.getResource();
        ResourceHandler<FluidResource> capability = bucketItem.toStack(bucket.getAmount()).getCapability(Capabilities.Fluid.ITEM, bucket);
        if (capability == null) {
            return false;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            for (int i = 0; i < tank.size(); i++) {
                FluidResource toFill = tank.getResource(i);
                for (int j = 0; toFill.isEmpty() && j < capability.size(); j++)
                    if (!capability.getResource(j).isEmpty())
                        toFill = capability.getResource(j);

                int toFillAmount = 0;
                try (Transaction simulate = Transaction.open(transaction)) {
                    int extractable = capability.extract(toFill, Integer.MAX_VALUE, simulate);
                    toFillAmount = tank.insert(i, toFill, extractable, simulate);
                }
                if (toFillAmount > 0) {
                    int filled = capability.extract(toFill, toFillAmount, transaction);
                    tank.insert(i, toFill, filled, transaction);
                }
            }
            transaction.commit();
        }

        return true;
    }

    public static boolean bucketToTank(ItemAccess bucket, ResourceHandler<FluidResource> tank) {
        // 检查桶是否为空（没有物品）
        if (bucket.getResource().isEmpty()) {
            return false;
        }
        ItemResource bucketItem = bucket.getResource();
        // 获取桶的流体能力（作为流体容器）
        ResourceHandler<FluidResource> capability = bucketItem.toStack(bucket.getAmount()).getCapability(Capabilities.Fluid.ITEM, bucket);
        if (capability == null) {
            return false;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            for (int i = 0; i < tank.size(); i++) {
                FluidResource toExtract = tank.getResource(i);
                // 跳过空槽位
                if (toExtract.isEmpty()) {
                    continue;
                }
                int transferred = 0;
                // 模拟转移，确定实际可转移量
                try (Transaction simulate = Transaction.open(transaction)) {
                    int extractable = tank.extract(i, toExtract, Integer.MAX_VALUE, simulate);
                    transferred = capability.insert(toExtract, extractable, simulate);
                }
                if (transferred > 0) {
                    // 执行真正的流体转移
                    int extracted = tank.extract(i, toExtract, transferred, transaction);
                    capability.insert(toExtract, extracted, transaction);
                }
            }
            transaction.commit();
        }
        return true;
    }
}
