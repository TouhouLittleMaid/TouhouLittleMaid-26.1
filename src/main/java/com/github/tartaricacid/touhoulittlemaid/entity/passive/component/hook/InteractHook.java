package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public interface InteractHook extends MaidComponent {
    InteractionResult mobInteract(Player playerIn, InteractionHand hand);
}
