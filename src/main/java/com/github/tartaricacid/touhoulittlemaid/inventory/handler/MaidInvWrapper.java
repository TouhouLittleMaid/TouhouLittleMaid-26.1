package com.github.tartaricacid.touhoulittlemaid.inventory.handler;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

/**
 * 女仆物品栏包装，继承自 {@link CombinedResourceHandler} 并保持对女仆实体的引用。
 * 以此在只有 {@link ResourceHandler} 引用的情况下，
 * 获取关联的女仆实体，从而支持对其持有的其它物品的访问。
 */
public class MaidInvWrapper extends CombinedResourceHandler<@NotNull ItemResource> {
    private final EntityMaid maid;

    @SafeVarargs
    public MaidInvWrapper(EntityMaid maid, ResourceHandler<@NotNull ItemResource>... itemHandler) {
        super(itemHandler);
        this.maid = maid;
    }

    public EntityMaid getMaid() {
        return maid;
    }
}
