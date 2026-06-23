package com.github.tartaricacid.touhoulittlemaid.compat.gun.common;

import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.PlayState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.LoopType;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

// TODO 枪械类模组还没有升级到 26.1
public class GunClientUtil {
    @Nullable
    public static PlayState playGunMainAnimation(EntityMaid maid, AnimationEvent<GeckoMaidEntity<?>> event, String animationName, LoopType loopType) {
        return null;
    }

    @Nullable
    public static PlayState playGunHoldAnimation(ItemStack mainHandItem, AnimationEvent<GeckoMaidEntity<?>> event) {
        return null;
    }
}
