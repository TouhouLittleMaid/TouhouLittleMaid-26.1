package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidPlaySoundEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.event.KubeEvent;

public class MaidPlaySoundEventJS implements KubeEvent {
    private final EntityMaid maid;

    public MaidPlaySoundEventJS(MaidPlaySoundEvent event) {
        this.maid = event.getMaid();
    }

    public EntityMaid getMaid() {
        return maid;
    }
}
