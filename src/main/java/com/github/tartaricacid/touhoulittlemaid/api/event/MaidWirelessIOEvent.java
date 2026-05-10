package com.github.tartaricacid.touhoulittlemaid.api.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 在女仆使用隙间传输物品时触发
 * <p>
 * 此事件可以取消，取消后则不会进行物品传输
 */
public abstract class MaidWirelessIOEvent extends Event implements ICancellableEvent {
    private final EntityMaid maid;
    /**
     * 女仆的物品栏
     */
    private final ResourceHandler<@NotNull ItemResource> maidInv;
    /**
     * 箱子的物品栏
     */
    private final ResourceHandler<@NotNull ItemResource> chestInv;
    /**
     * 隙间过滤器的标记的物品
     */
    private final ResourceHandler<@NotNull ItemResource> filterInv;
    /**
     * 是否是黑名单模式
     */
    private final boolean isBlacklist;
    /**
     * 物品栏的配置，true 表示该槽位 不 允许传输
     */
    private final List<Boolean> slotConfig;

    public MaidWirelessIOEvent(EntityMaid maid, ResourceHandler<@NotNull ItemResource> maidInv, ResourceHandler<@NotNull ItemResource> chestInv, ResourceHandler<@NotNull ItemResource> filterInv, boolean isBlacklist, List<Boolean> slotConfig) {
        this.maid = maid;
        this.maidInv = maidInv;
        this.chestInv = chestInv;
        this.filterInv = filterInv;
        this.isBlacklist = isBlacklist;
        this.slotConfig = slotConfig;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public ResourceHandler<@NotNull ItemResource> getMaidInv() {
        return maidInv;
    }

    public ResourceHandler<@NotNull ItemResource> getChestInv() {
        return chestInv;
    }

    public ResourceHandler<@NotNull ItemResource> getFilterInv() {
        return filterInv;
    }

    public boolean isBlacklist() {
        return isBlacklist;
    }

    public List<Boolean> getSlotConfig() {
        return slotConfig;
    }

    public static class MaidToChest extends MaidWirelessIOEvent {
        public MaidToChest(EntityMaid maid, ResourceHandler<@NotNull ItemResource> maidInv, ResourceHandler<@NotNull ItemResource> chestInv, ResourceHandler<@NotNull ItemResource> filterInv, boolean isBlacklist, List<Boolean> slotConfig) {
            super(maid, maidInv, chestInv, filterInv, isBlacklist, slotConfig);
        }
    }

    public static class ChestToMaid extends MaidWirelessIOEvent {
        public ChestToMaid(EntityMaid maid, ResourceHandler<@NotNull ItemResource> maidInv, ResourceHandler<@NotNull ItemResource> chestInv, ResourceHandler<@NotNull ItemResource> filterInv, boolean isBlacklist, List<Boolean> slotConfig) {
            super(maid, maidInv, chestInv, filterInv, isBlacklist, slotConfig);
        }
    }
}
