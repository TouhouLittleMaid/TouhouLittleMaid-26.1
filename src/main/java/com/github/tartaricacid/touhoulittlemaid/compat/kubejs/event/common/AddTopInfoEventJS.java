package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.AddTopInfoEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.event.KubeEvent;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;

public class AddTopInfoEventJS implements KubeEvent {
    private final EntityMaid maid;
    private final ProbeMode probeMode;
    private final IProbeInfo probeInfo;
    private final IProbeHitEntityData hitEntityData;

    public AddTopInfoEventJS(AddTopInfoEvent event) {
        this.maid = event.getMaid();
        this.probeMode = event.getProbeMode();
        this.probeInfo = event.getProbeInfo();
        this.hitEntityData = event.getHitEntityData();
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public ProbeMode getProbeMode() {
        return probeMode;
    }

    public IProbeInfo getProbeInfo() {
        return probeInfo;
    }

    public IProbeHitEntityData getHitEntityData() {
        return hitEntityData;
    }
}
