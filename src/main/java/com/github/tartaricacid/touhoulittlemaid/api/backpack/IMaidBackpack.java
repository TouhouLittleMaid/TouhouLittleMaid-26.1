package com.github.tartaricacid.touhoulittlemaid.api.backpack;

import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityTombstone;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.item.BackpackLevel;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;


public abstract class IMaidBackpack {
    /**
     * 获取女仆背包类型的唯一标识。
     *
     * @return 用于注册和查找该背包类型的 ID
     */
    public abstract Identifier getId();

    /**
     * 获取与该背包类型对应的物品。
     *
     * @return 背包物品
     */
    public abstract Item getItem();

    /**
     * 当玩家给女仆穿戴该背包时调用。
     *
     * @param stack  玩家用于穿戴的背包物品堆
     * @param player 执行穿戴操作的玩家
     * @param maid   被穿戴背包的女仆
     */
    public void onPutOn(ItemStack stack, Player player, EntityMaid maid) {
    }

    /**
     * 获取卸下该背包时应返还给玩家或放入墓碑的背包自身。
     * <p>
     * 玩家主动卸下或替换背包时，返回值会放回玩家物品栏；女仆死亡生成墓碑时，返回值会放入墓碑。
     * 默认返回该背包对应物品的默认实例。
     *
     * @param stack  触发卸下或替换背包的物品；死亡生成墓碑时为空物品堆
     * @param player 执行卸下或替换操作的玩家；死亡生成墓碑时为 {@code null}
     * @param maid   被卸下背包的女仆
     * @return 应返还或保存的背包物品
     */
    public ItemStack getTakeOffItemStack(ItemStack stack, @Nullable Player player, EntityMaid maid) {
        return this.getItem().getDefaultInstance();
    }

    /**
     * 当玩家从女仆身上卸下或替换掉该背包时调用。
     *
     * @param stack  触发卸下或替换背包的物品堆
     * @param player 执行卸下或替换操作的玩家
     * @param maid   被卸下背包的女仆
     */
    public void onTakeOff(ItemStack stack, Player player, EntityMaid maid) {
    }

    /**
     * 当女仆死亡并生成墓碑时调用，用于写入该背包的额外掉落或状态。
     * <p>
     * 背包本体已通过 {@link #getTakeOffItemStack(ItemStack, Player, EntityMaid)} 放入墓碑。
     *
     * @param maid      死亡并生成墓碑的女仆
     * @param tombstone 生成的墓碑实体
     */
    public void onSpawnTombstone(EntityMaid maid, EntityTombstone tombstone) {
    }

    /**
     * 获取打开该背包界面时使用的 MenuProvider。
     *
     * @param entityId 女仆实体 ID，用于菜单在服务端和客户端定位同一女仆
     * @return 该背包对应的 MenuProvider
     */
    public abstract MenuProvider getGuiProvider(int entityId);

    /**
     * 获取该背包允许女仆物品栏使用的最大容器索引。
     * <p>
     * 该值用于限制女仆可用容量，也用于卸下背包时丢出超出当前容量的物品。
     *
     * @return 可用容器索引上限
     */
    public abstract int getAvailableMaxContainerIndex();

    /**
     * 获取该背包的客户端渲染数据。
     *
     * @return 背包渲染数据
     */
    public abstract MaidBackpackRenderData getRenderData();

    /**
     * 丢出女仆背包物品栏中的全部物品。
     *
     * @param maid 需要丢出背包物品的女仆
     */
    protected final void dropAllItems(EntityMaid maid) {
        ItemsUtil.dropEntityItems(maid, maid.getMaidInv(), BackpackLevel.EMPTY_CAPACITY, null);
    }

    /**
     * 根据即将穿戴的新背包容量，丢出女仆物品栏中超出新容量的物品。
     * <p>
     * 如果传入物品堆找不到对应背包类型，则退回为丢出全部背包物品。
     *
     * @param stack 即将穿戴的新背包物品堆
     * @param maid  需要检查并丢出物品的女仆
     */
    protected final void dropRelativeItems(ItemStack stack, EntityMaid maid) {
        BackpackManager.findBackpack(stack).ifPresentOrElse(backpack -> {
            int startIndex = backpack.getAvailableMaxContainerIndex();
            ItemsUtil.dropEntityItems(maid, maid.getMaidInv(), startIndex, null);
        }, () -> this.dropAllItems(maid));
    }
}
