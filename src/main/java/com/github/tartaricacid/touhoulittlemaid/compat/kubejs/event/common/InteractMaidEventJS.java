package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InteractMaidEventJS implements KubeEvent {
    private final Level world;
    private final Player player;
    private final EntityMaid maid;
    private final ItemStack stack;

    public InteractMaidEventJS(InteractMaidEvent event) {
        this.world = event.getWorld();
        this.player = event.getPlayer();
        this.maid = event.getMaid();
        this.stack = event.getStack();
    }

    public Player getPlayer() {
        return player;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Level getWorld() {
        return world;
    }
}
